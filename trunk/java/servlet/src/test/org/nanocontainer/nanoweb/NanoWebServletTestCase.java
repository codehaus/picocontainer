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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class NanoWebServletTestCase extends TestCase {

    public void testIntegration() throws IOException, ServletException {
        NanoWebServlet nanoServlet = new NanoWebServlet();
        Mock requestMock = new Mock(HttpServletRequest.class);
        Mock responseMock = new Mock(HttpServletResponse.class);
        Mock servletContextMock = new Mock(ServletContext.class);
        Mock servletConfigMock = new Mock(ServletConfig.class);
        servletConfigMock.expectAndReturn("getServletContext", servletContextMock.proxy());
        servletConfigMock.expectAndReturn("getServletName", "NanoWeb");
        servletConfigMock.expectAndReturn("getServletContext", servletContextMock.proxy());
        servletContextMock.expect("log", C.args(C.eq("NanoWeb: init")));
        Mock containerBuilderMock = new Mock(ContainerBuilder.class);
        servletContextMock.expectAndReturn("getAttribute", C.args(C.eq(KeyConstants.BUILDER)), containerBuilderMock.proxy());
        MutablePicoContainer requestContainer = new DefaultPicoContainer();
        requestMock.expectAndReturn("getAttribute", C.args(C.eq(KeyConstants.REQUEST_CONTAINER)), requestContainer);
        requestMock.expectAndReturn("getSession", C.args(C.eq(Boolean.TRUE)), null);
        containerBuilderMock.expect("buildContainer", C.ANY_ARGS);
        containerBuilderMock.expect("killContainer", C.ANY_ARGS);
        requestMock.expectAndReturn("getAttribute", C.args(C.eq("javax.servlet.include.servlet_path")), null);
        requestMock.expectAndReturn("getServletPath", "/test.nano");
        requestMock.expect("setAttribute", C.args(C.eq("action"), C.isA(MyAction.class)));
        requestMock.expectAndReturn("getAttribute", C.args(C.eq("javax.servlet.include.servlet_path")), null);
        requestMock.expectAndReturn("getAttribute", C.args(C.eq(KeyConstants.REQUEST_CONTAINER)), requestContainer);
        Map parameterMap = new HashMap();
        parameterMap.put("year", "2004");
        parameterMap.put("cars", new String[]{"renault", "fiat"});
        requestMock.expectAndReturn("getParameterMap", parameterMap);

        Mock requestDispatcherMock = new Mock(RequestDispatcher.class);
        requestDispatcherMock.expect("forward", C.args(C.eq(requestMock.proxy()), C.eq(responseMock.proxy())));
        requestMock.expectAndReturn("getRequestDispatcher", C.args(C.eq("/test_success.vm")), requestDispatcherMock.proxy());

        requestContainer.registerComponentImplementation("/test.nano", MyAction.class);

        nanoServlet.init((ServletConfig) servletConfigMock.proxy());
        nanoServlet.service((HttpServletRequest)requestMock.proxy(), (HttpServletResponse)responseMock.proxy());

        MyAction action = (MyAction) requestContainer.getComponentInstance("/test.nano");
        assertEquals(2004, action.getYear());
        assertEquals(Arrays.asList(new String[]{"renault", "fiat"}), action.getCars());

        requestMock.verify();
        responseMock.verify();
        servletContextMock.verify();
        servletConfigMock.verify();
        containerBuilderMock.verify();
        requestDispatcherMock.verify();
    }
}