package org.nanocontainer.nanowar.nanoweb;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.nanocontainer.nanowar.KeyConstants;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapter;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * @version $Id$
 */
public class NanoWebServletTestCase extends MockObjectTestCase {

    private NanoWebServletComponent nanoServletComponent;

    private Mock requestMock;

    private Mock responseMock;

    private MutablePicoContainer requestContainer;

    private Mock requestDispatcherMock;

    protected void setUp() throws Exception {
        super.setUp();
        requestMock = mock(HttpServletRequest.class);
        responseMock = mock(HttpServletResponse.class);

        requestContainer = new DefaultPicoContainer();
        requestContainer.registerComponent(new ConverterComponentAdapter(Car.class, new ConstructorInjectionComponentAdapter(CarConverter.class, CarConverter.class)));

        requestDispatcherMock = new Mock(RequestDispatcher.class);
        requestDispatcherMock.expects(atLeastOnce()).method("forward").with(eq(requestMock.proxy()), eq(responseMock.proxy()));

        Mock httpSessionMock = mock(HttpSession.class);

        requestMock.expects(once()).method("getSession").with(eq(true)).will(returnValue(httpSessionMock.proxy()));
        requestMock.expects(atLeastOnce()).method("getAttribute").with(eq("javax.servlet.include.servlet_path")).will(returnValue(null));
        requestMock.expects(atLeastOnce()).method("getAttribute").with(eq(KeyConstants.REQUEST_CONTAINER)).will(returnValue(requestContainer));
        requestMock.expects(atLeastOnce()).method("getRequestDispatcher").with(ANYTHING).will(returnValue(requestDispatcherMock.proxy()));
    }

    public void testParametersShouldBeSetAndExecuteInvokedOnGroovyAction() throws IOException, ServletException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Mock cachingScriptClassLoaderMock = mock(ActionFactory.class);
        nanoServletComponent = new NanoWebServletComponent((ActionFactory) cachingScriptClassLoaderMock.proxy());

        // cachingScriptClassLoader
        MyAction action = new MyAction();
        cachingScriptClassLoaderMock.expects(once()).method("getInstance").with(isA(PicoContainer.class), eq("/test")).will(returnValue(action));

        // url params
        Map parameterMap = new HashMap();
        parameterMap.put("year", new String[] { "2003" });
        parameterMap.put("ImNotAnActionParameter", new String[] { "boo" });
        parameterMap.put("cars", new String[] { "renault", "fiat" });
        requestMock.expects(atLeastOnce()).method("getParameterMap").will(returnValue(parameterMap));

        // path, action and view
        requestMock.expects(atLeastOnce()).method("getServletPath").will(returnValue("/test.nwa"));
        requestMock.expects(atLeastOnce()).method("setAttribute").with(eq("year"), eq(new Integer(2003)));
        requestMock.expects(once()).method("setAttribute").with(eq("cars"), eq(new Car[] { new Car("renault"), new Car("fiat") }));
        requestMock.expects(atLeastOnce()).method("setAttribute").with(eq("country"), eq(null));

        nanoServletComponent.service((HttpServletRequest) requestMock.proxy(), (HttpServletResponse) responseMock.proxy());

        // assertions
        assertEquals(2003, action.valueOfYear);
        assertNull(action.valueOfCountry);
        assertEquals(Arrays.asList(new Car[] { new Car("renault"), new Car("fiat") }), Arrays.asList(action.valueOfCars));
    }

}
