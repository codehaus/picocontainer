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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class NanoWebServletTestCase extends TestCase {
    private NanoWebServlet nanoServlet;
    private Mock requestMock;
    private Mock responseMock;
    private Mock servletContextMock;
    private Mock servletConfigMock;
    private Mock containerBuilderMock;
    private MutablePicoContainer requestContainer;
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
        servletContextMock.expect("log", C.args(C.eq("NanoWeb: init")));
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

        nanoServlet.init((ServletConfig) servletConfigMock.proxy());
    }

    public void testParametersShouldBeSetAndExecuteInvokedOnJavaAction() throws IOException, ServletException {
        // url params
        Map parameterMap = new HashMap();
        parameterMap.put("year", "2004");
        parameterMap.put("cars", new String[]{"renault", "fiat"});
        requestMock.expectAndReturn("getParameterMap", parameterMap);

        // path, action and view
        requestMock.expectAndReturn("getServletPath", "/test.nano");
        requestContainer.registerComponentImplementation("/test.nano", MyAction.class);
        requestMock.expect("setAttribute", C.args(C.eq("action"), C.isA(MyAction.class)));
        requestMock.expectAndReturn("getRequestDispatcher", C.args(C.eq("/test_success.vm")), requestDispatcherMock.proxy());

        // doit
        nanoServlet.service((HttpServletRequest)requestMock.proxy(), (HttpServletResponse)responseMock.proxy());

        // assertions
        MyAction action = (MyAction) requestContainer.getComponentInstance("/test.nano");
        assertEquals(2004, action.getYear());
        assertEquals(Arrays.asList(new String[]{"renault", "fiat"}), action.getCars());

        verifyMocks();
    }

    public void testParametersShouldBeSetAndExecuteInvokedOnGroovyAction() throws IOException, ServletException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        // url params
        Map parameterMap = new HashMap();
        parameterMap.put("year", "2003");
        parameterMap.put("cars", new String[]{"renault", "fiat"});
        requestMock.expectAndReturn("getParameterMap", parameterMap);

        // path, action and view
        requestMock.expectAndReturn("getServletPath", "/test.groovy");
        requestMock.expect("setAttribute", C.args(C.eq("action"), C.isA(Object.class)));
        requestMock.expectAndReturn("getRequestDispatcher", C.args(C.eq("/test_error.vm")), requestDispatcherMock.proxy());

        nanoServlet.service((HttpServletRequest)requestMock.proxy(), (HttpServletResponse)responseMock.proxy());

        // assertions
        Object action = requestContainer.getComponentInstance("/test.groovy");
        Method getYear = action.getClass().getMethod("getYear", null);
        Object resultYear = getYear.invoke(action, null);
        assertEquals("2003", resultYear);

        Method getCars = action.getClass().getMethod("getCars", null);
        Object resultCars = getCars.invoke(action, null);
        assertEquals(Arrays.asList(new String[]{"renault", "fiat"}), resultCars);

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