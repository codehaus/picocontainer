package org.nanocontainer.nanowar.nanoweb;

import ognl.Ognl;
import ognl.OgnlException;
import org.nanocontainer.nanowar.ApplicationScopeObjectReference;
import org.nanocontainer.nanowar.KeyConstants;
import org.nanocontainer.nanowar.RequestScopeObjectReference;
import org.nanocontainer.nanowar.ServletRequestContainerLauncher;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.ObjectReference;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;

/**
 * Dispatcher servlet for NanoWeb.
 * NanoWeb is an ultra simple MVC framework inspired from WebWork It is based on ScriptedContainerBuilderFactory,
 * PicoContainer, Ognl, Groovy and Velocity.
 * Design goals:
 * <ul>
 * <li>One-file configuration (all in an embedded ScriptedContainerBuilderFactory script in web.xml)</li>
 * <li>Sensible defaults with the goal to reduce the need for complex configuration</li>
 * <li>Non intrusiveness. Actions are PicoComponents/POJOs that extend nothing</li>
 * <li>Actions can be written in a compilable scripting language like Groovy</li>
 * <li>Actions must implement a String execute() method</li>
 * <li>The result from execute will be used to determine the view path. Example: /something.nano + "success" -> /something_success.vm</li>
 * </ul>
 * Other things:
 * <ul>
 * <li>Map action paths to action classes in nanocontainer servlet script (using groovy)</li>
 * <li>Views can set values in actions with Ognl expressions</li>
 * </ul>
 *
 * @author Aslak Helles&oslash;y
 * @author Kouhei Mori
 * @version $Revision$
 */
public class NanoWebServlet extends HttpServlet implements KeyConstants {

    private Dispatcher dispatcher;
    // TODO make this configurable from web.xml
    private final CachingScriptClassLoader cachingScriptClassLoader = new CachingScriptClassLoader();

    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        MutablePicoContainer applicationContainer = getApplicationContainer(servletContext);
        initDispatcher(applicationContainer);
    }

    private void initDispatcher(MutablePicoContainer applicationContainer) {
        dispatcher = (Dispatcher) applicationContainer.getComponentInstanceOfType(Dispatcher.class);
        if (dispatcher == null) {
            dispatcher = new ChainingDispatcher(".vm");
        }
    }

    protected void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        ServletRequestContainerLauncher containerLauncher = new ServletRequestContainerLauncher(getServletContext(), httpServletRequest);
        try {
            containerLauncher.startContainer();
            String servletPath = getServletPath(httpServletRequest);
            String scriptPathWithoutExtension = servletPath.substring(0, servletPath.lastIndexOf('/'));

            Object action = getActionObject(scriptPathWithoutExtension, httpServletRequest);
            setPropertiesWithOgnl(httpServletRequest, action);

            int dot = servletPath.lastIndexOf('.');
            String actionMethod = servletPath.substring(servletPath.lastIndexOf('/') + 1, dot);
            String result = execute(action, actionMethod);

            httpServletRequest.setAttribute("action", action);
            ServletContext servletContext = getServletContext();
            dispatcher.dispatch(servletContext, httpServletRequest, httpServletResponse, scriptPathWithoutExtension, actionMethod, result);
        } catch (ScriptException e) {
            handleServiceScriptException(e, httpServletResponse, httpServletRequest);
        } finally {
            containerLauncher.killContainer();
        }
    }

    /**
     * This overridable method contains the default behavior for the catching of ScriptException during the
     * service call. Because different containers suck at handling exceptions, this default behavior displays
     * a simple page. Teams deploying NanoWebServlet may want to extend and replace this functionality. This
     * servlet may be participating with some larger model-2 design.
     *
     * @param e the script exception
     * @param httpServletResponse the servlet response
     * @param httpServletRequest the servlet request
     * @throws IOException if for some bizarre reason the outgoing page cannot be written to.
     */
    protected void handleServiceScriptException(ScriptException e, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) throws IOException {
        e.printStackTrace();
        // Print the stack trace and the script (for debugging)
        PrintWriter writer = httpServletResponse.getWriter();
        writer.println("<html>");
        writer.println("<pre>");
        e.printStackTrace(writer);
        writer.println(httpServletRequest.getRequestURI());
        URL scriptURL = e.getScriptURL();
        InputStream in = scriptURL.openStream();
        int c;
        while ((c = in.read()) != -1) {
            writer.write(c);
        }
        writer.println("</pre>");
        writer.println("</html>");
    }

    private Object getActionObject(String path, HttpServletRequest request) throws ServletException, ScriptException {
        MutablePicoContainer requestContainer = getRequestContainer(request);
        // Try to get an action as specified in the configuration
        Object action = requestContainer.getComponentInstance(path);
        if (action == null) {
            // Try to get an action from a script (groovy)
            try {
                action = getScriptAction(path + ".groovy", requestContainer);
            } catch (IOException e) {
                log("Failed to load action class", e);
                throw new ServletException(e);
            }
        }
        if (action == null) {
            String msg = "No action found for '" + path + "'";
            throw new ServletException(msg);
        }
        return action;
    }

    private Object getScriptAction(String scriptPath, MutablePicoContainer requestContainer) throws IOException, ScriptException {
        URL scriptURL = getServletContext().getResource(scriptPath);
        Object result = null;
        if (scriptURL != null) {
            Class scriptClass = cachingScriptClassLoader.getClass(scriptURL);
            requestContainer.registerComponentImplementation(scriptPath, scriptClass);
            result = requestContainer.getComponentInstance(scriptPath);
        }
        return result;
    }

    private void setPropertiesWithOgnl(HttpServletRequest servletRequest, Object action) throws ServletException {
        Enumeration parameterNames = servletRequest.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = (String) parameterNames.nextElement();
            String parameterValue = servletRequest.getParameter(parameterName);
            try {
                Ognl.setValue(parameterName, action, parameterValue);
            } catch (OgnlException e) {
                log("Failed to set property with OGNL ('" + parameterName +"', '" + action + "', '" + parameterValue + "')", e);
                throw new ServletException(e);
            }
        }
    }

    private String execute(Object actionObject, String actionMethodName) throws ServletException {
        Method actionMethod = null;
        try {
            actionMethod = actionObject.getClass().getMethod(actionMethodName, (Class[])null);
        } catch (NoSuchMethodException e) {
            String message = "The " + actionObject.getClass().getName() + " doesn't have the method " + actionMethodName + "()";
            log(message, e);
            throw new ServletException(message, e);
        }
        try {
            String view = (String) actionMethod.invoke(actionObject, (Object[])null);
            return view;
        } catch (IllegalAccessException e) {
            String message = actionObject.getClass().getName() + "." + actionMethodName + "() isn't public";
            log(message, e);
            throw new ServletException(message, e);
        } catch (InvocationTargetException e) {
            String message = "Failed to actionMethod " + actionObject.getClass().getName() + "." + actionMethodName + "()";
            log(message, e);
            throw new ServletException(message, e);
        }
    }

    static String getServletPath(HttpServletRequest httpServletRequest) {
        String servletPath = (String) httpServletRequest.getAttribute("javax.servlet.include.servlet_path");
        if (servletPath == null) {
            servletPath = httpServletRequest.getServletPath();
        }
        return servletPath;
    }

    private MutablePicoContainer getApplicationContainer(ServletContext context) {
        ObjectReference ref = new ApplicationScopeObjectReference(context, APPLICATION_CONTAINER);
        return (MutablePicoContainer) ref.get();
    }

    private MutablePicoContainer getRequestContainer(ServletRequest request) {
        ObjectReference ref = new RequestScopeObjectReference(request, REQUEST_CONTAINER);
        return (MutablePicoContainer) ref.get();
    }
}