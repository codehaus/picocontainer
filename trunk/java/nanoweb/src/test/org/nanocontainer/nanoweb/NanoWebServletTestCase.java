package org.nanocontainer.nanoweb;

import junit.framework.TestCase;
import org.jmock.C;
import org.jmock.Mock;
import org.nanocontainer.integrationkit.ContainerBuilder;
import org.nanocontainer.servlet.KeyConstants;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Vector;
import java.beans.IntrospectionException;

import groovy.lang.MetaClass;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.GroovyShell;
import groovy.lang.Binding;
import groovy.lang.Reference;

/**
 * @author Aslak Helles&oslash;y
 * @author Kouhei Mori
 * @version $Revision$
 */
public class NanoWebServletTestCase extends TestCase {
    private NanoWebServlet nanoServlet;
    private MutablePicoContainer requestContainer;

    private Mock requestMock;
    private Mock responseMock;
    private Mock servletContextMock;
    private Mock servletConfigMock;
    private Mock containerBuilderMock;
    private Mock requestDispatcherMock;

    protected void setUp() throws Exception {
        super.setUp();
        nanoServlet = new NanoWebServlet();
        requestMock = new Mock(HttpServletRequest.class);
        responseMock = new Mock(HttpServletResponse.class);
        servletContextMock = new Mock(ServletContext.class);
        servletConfigMock = new Mock(ServletConfig.class);
        servletConfigMock.expectAndReturn("getServletContext", servletContextMock.proxy());
        servletConfigMock.expectAndReturn("getServletName", "NanoWeb");
        servletConfigMock.expectAndReturn("getServletContext", servletContextMock.proxy());
        servletConfigMock.expectAndReturn("getServletContext", servletContextMock.proxy());
        servletContextMock.expect("log", C.args(C.eq("NanoWeb: init")));
        servletContextMock.expectAndReturn("getResource", C.args(C.eq("/test_doit_success.vm")), new URL("http://dummy/"));
        containerBuilderMock = new Mock(ContainerBuilder.class);
        servletContextMock.expectAndReturn("getAttribute", C.args(C.eq(KeyConstants.BUILDER)), containerBuilderMock.proxy());
        requestContainer = new DefaultPicoContainer();
        requestMock.expectAndReturn("getAttribute", C.args(C.eq(KeyConstants.REQUEST_CONTAINER)), requestContainer);
        requestMock.expectAndReturn("getSession", C.args(C.eq(Boolean.TRUE)), null);
        containerBuilderMock.expect("buildContainer", C.ANY_ARGS);
        containerBuilderMock.expect("killContainer", C.ANY_ARGS);
        requestMock.expectAndReturn("getAttribute", C.args(C.eq("javax.servlet.include.servlet_path")), null);
        requestMock.expectAndReturn("getAttribute", C.args(C.eq("javax.servlet.include.servlet_path")), null);
        requestMock.expectAndReturn("getAttribute", C.args(C.eq(KeyConstants.REQUEST_CONTAINER)), requestContainer);
        requestDispatcherMock = new Mock(RequestDispatcher.class);
        requestDispatcherMock.expect("forward", C.args(C.eq(requestMock.proxy()), C.eq(responseMock.proxy())));

        // url params
        Vector paramtereNames = new Vector();
        paramtereNames.add("year");
        requestMock.expectAndReturn("getParameterNames", paramtereNames.elements());
        requestMock.expectAndReturn("getParameter", C.args(C.eq("year")), "2004");

        // path, action and view
        requestMock.expectAndReturn("getServletPath", "/test/doit.nano");
        requestMock.expectAndReturn("getRequestDispatcher", C.args(C.eq("/test_doit_success.vm")), requestDispatcherMock.proxy());

        nanoServlet.init((ServletConfig) servletConfigMock.proxy());
    }

    public void testParametersShouldBeSetAndExecuteInvokedOnJavaAction() throws IOException, ServletException {
        requestContainer.registerComponentImplementation("/test", MyAction.class);
        requestMock.expect("setAttribute", C.args(C.eq("action"), C.isA(MyAction.class)));

        nanoServlet.service((HttpServletRequest)requestMock.proxy(), (HttpServletResponse)responseMock.proxy());

        MyAction action = (MyAction) requestContainer.getComponentInstance("/test");
        assertEquals(2004, action.getYear());
        assertEquals("success", action.doit());

        verifyMocks();
    }

    public void testParametersShouldBeSetAndExecuteInvokedOnGroovyAction() throws IOException, ServletException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        servletConfigMock.expectAndReturn("getServletContext", servletContextMock.proxy());
        servletContextMock.expectAndReturn("getResource", C.args(C.eq("/test.groovy")), getClass().getResource("/test.groovy"));
        requestMock.expect("setAttribute", C.args(C.eq("action"), C.isA(Object.class)));

        nanoServlet.service((HttpServletRequest)requestMock.proxy(), (HttpServletResponse)responseMock.proxy());

        Object action = requestContainer.getComponentInstance("/test.groovy");
        Method getYear = action.getClass().getMethod("getYear", null);
        Object resultYear = getYear.invoke(action, null);
        assertEquals("2004", resultYear);

        verifyMocks();
    }

    private void verifyMocks() {
        requestMock.verify();
        responseMock.verify();
        servletContextMock.verify();
        servletConfigMock.verify();
        containerBuilderMock.verify();
        requestDispatcherMock.verify();
    }
}