package org.nanocontainer.servlet;

import junit.framework.TestCase;
import org.jmock.C;
import org.jmock.Mock;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.nanocontainer.script.jython.JythonContainerBuilder;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import java.io.StringReader;
import java.util.Vector;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ServletContainerListenerTestCase extends TestCase implements KeyConstants {
    public void testXXX(){}

    private String pythonScript =
            "from java.util import ArrayList\n" +
            "pico = DefaultPicoContainer()\n" +
            "pico.registerComponentImplementation(ArrayList)\n";

    public void testApplicationScopeContainerIsCreatedWhenServletContextIsInitialised() {
        ServletContainerListener listener = new ServletContainerListener();

        Mock servletContextMock = new Mock(ServletContext.class);
        final Vector initParams = new Vector();
        initParams.add("nanocontainer.py");
        servletContextMock.expectAndReturn("getInitParameterNames", initParams.elements());
        servletContextMock.expectAndReturn("getInitParameter", C.args(C.eq("nanocontainer.py")), pythonScript);
        servletContextMock.expect("setAttribute", C.args(C.eq(BUILDER), C.isA(JythonContainerBuilder.class)));
        servletContextMock.expect("setAttribute", C.args(C.eq(APPLICATION_CONTAINER), C.isA(PicoContainer.class)));

        listener.contextInitialized(new ServletContextEvent((ServletContext) servletContextMock.proxy()));
        servletContextMock.verify();
    }

    public void XXtestSessionScopeContainerWithAppScopeContainerAsParentIsCreatedWhenServletContextIsInitialised() {
        ServletContainerListener listener = new ServletContainerListener();

        Mock httpSessionMock = new Mock(HttpSession.class);
        Mock servletContextMock = new Mock(ServletContext.class);
        httpSessionMock.expectAndReturn("getServletContext", servletContextMock.proxy());
        servletContextMock.expectAndReturn("getAttribute", C.args(C.eq(APPLICATION_CONTAINER)), new DefaultPicoContainer());
        servletContextMock.expectAndReturn("getAttribute", C.args(C.eq(BUILDER)), new JythonContainerBuilder(new StringReader(pythonScript), getClass().getClassLoader()));

        httpSessionMock.expect("setAttribute", C.args(C.eq(SESSION_CONTAINER), C.isA(PicoContainer.class)));

        listener.sessionCreated(new HttpSessionEvent((HttpSession) httpSessionMock.proxy()));

        httpSessionMock.verify();
    }

}
