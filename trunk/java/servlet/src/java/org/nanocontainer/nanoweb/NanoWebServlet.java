package org.nanocontainer.nanoweb;

import ognl.Ognl;
import ognl.OgnlException;
import org.nanocontainer.servlet.KeyConstants;
import org.nanocontainer.servlet.RequestScopeObjectReference;
import org.nanocontainer.servlet.ServletRequestContainerLauncher;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ObjectReference;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Dispatcher servlet for NanoWeb.
 * NanoWeb is an ultra simple MVC framework inspired from WebWork It is based on NanoContainer,
 * PicoContainer, Ognl and Velocity.
 * Design goals:
 * <ul>
 * <li>One-file configuration (all in an embedded NanoContainer script in web.xml)</li>
 * <li>Sensible defaults with the goal to reduce the need for complex configuration</li>
 * <li>Non intrusiveness. Actions are PicoComponents/POJOs that extend nothing</li>
 * <li>Actions must implement a String execute() method</li>
 * <li>The result from execute will be used to determine the view path. Example: /something.nano + "success" -> /something_success.vm</li>
 * </ul>
 * Other things:
 * <ul>
 * <li>Map action paths to action classes in nanocontainer servlet script (groovy)</li>
 * <li>Views can set values in actions with Ognl expressions</li>
 * </ul>
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class NanoWebServlet extends HttpServlet implements KeyConstants {
    // TODO make this configurable from web.xml
    private Dispatcher dispatcher = new ChainingDispatcher();

    protected void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        ServletRequestContainerLauncher containerLauncher = new ServletRequestContainerLauncher(getServletContext(), httpServletRequest);
        try {
            containerLauncher.startContainer();
            String actionPath = getServletPath(httpServletRequest);
            Object action = getAction(actionPath, httpServletRequest);
            setPropertiesWithOgnl(httpServletRequest, action);
            String result = execute(action);
            httpServletRequest.setAttribute("action", action);
            dispatcher.dispatch(httpServletRequest, httpServletResponse, actionPath, result);
        } finally {
            try {
                containerLauncher.killContainer();
            } catch (Exception e) {
                throw new ServletException(e);
            }
        }
    }

    private Object getAction(String key, ServletRequest request) throws ServletException {
        MutablePicoContainer container = new DefaultPicoContainer(getRequestContainer(request));
        Object action = container.getComponentInstance(key);
        if (action == null) {
            String msg = "No action found for '" + key + "'";
            throw new ServletException(msg);
        }
        return action;
    }

    private void setPropertiesWithOgnl(HttpServletRequest servletRequest, Object action) throws ServletException {
        Map parameterMap = servletRequest.getParameterMap();
        Set parameterKeys = parameterMap.keySet();
        for (Iterator iterator = parameterKeys.iterator(); iterator.hasNext();) {
            String parameterKey = (String) iterator.next();
            Object value = parameterMap.get(parameterKey);
            if (value instanceof String[]) {
                value = ((String[]) value)[0];
            }
            try {
                Ognl.setValue(parameterKey, action, value);
            } catch (OgnlException e) {
                throw new ServletException(e);
            }
        }
    }

    private String execute(Object action) throws ServletException {
        try {
            Method execute = action.getClass().getMethod("execute", null);
            String view = (String) execute.invoke(action, null);
            return view;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }

    static String getServletPath(HttpServletRequest httpServletRequest) {
        String servletPath = (String) httpServletRequest.getAttribute("javax.servlet.include.servlet_path");
        if (servletPath == null) {
            servletPath = httpServletRequest.getServletPath();
        }
        return servletPath;
    }

    private MutablePicoContainer getRequestContainer(ServletRequest request) {
        ObjectReference ref = new RequestScopeObjectReference(request, REQUEST_CONTAINER);
        return (MutablePicoContainer) ref.get();
    }
}