/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/
package org.nanocontainer.ejb;

import java.net.SocketTimeoutException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Properties;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;
import javax.ejb.EJBObject;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;

import junit.framework.Test;

import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.nanocontainer.ejb.testmodel.BarHomeImpl;
import org.nanocontainer.ejb.testmodel.FooBar;
import org.nanocontainer.ejb.testmodel.FooBarHome;
import org.nanocontainer.ejb.testmodel.FooHomeImpl;
import org.nanocontainer.ejb.testmodel.Hello;
import org.nanocontainer.ejb.testmodel.HelloHome;
import org.nanocontainer.ejb.testmodel.HelloHomeImpl;
import org.nanocontainer.ejb.testmodel.HelloImpl;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.PicoInvocationTargetInitializationException;


/**
 * Unit test for EJBClientComponentAdapter.
 * @author J&ouml;rg Schaible
 */
public class EJBClientComponentAdapterTest extends MockObjectTestCase {

    private Hashtable m_environment;
    private Mock m_initialContextMock;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        m_initialContextMock = mock(InitialContextMock.class);
        InitialContextFactoryMock.setInitialContext((InitialContext)m_initialContextMock.proxy());

        m_environment = new Hashtable();
        m_environment.put(Context.INITIAL_CONTEXT_FACTORY, InitialContextFactoryMock.class.getName());
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        InitialContextFactoryMock.setInitialContext(null);
        super.tearDown();
    }

    /**
     * Test for constructor of EJBClientComponentAdapter
     */
    public final void testConstructors() {
        try {
            new EJBClientComponentAdapter("Foo", Test.class);
            fail("Should have thrown a ClassNotFoundException");
        } catch (ClassNotFoundException e) {
            assertTrue(e.getMessage().indexOf("TestHome") > 0);
        }
        try {
            new EJBClientComponentAdapter("Bar", FooBar.class, FooBar.class, m_environment, false);
            fail("Should have thrown a AssignabilityRegistrationException");
        } catch (AssignabilityRegistrationException e) {
            assertTrue(e.getMessage().indexOf(EJBHome.class.getName()) > 0);
        }
        try {
            new EJBClientComponentAdapter("Bar", FooBarHome.class, FooBarHome.class, m_environment, false);
            fail("Should have thrown a AssignabilityRegistrationException");
        } catch (AssignabilityRegistrationException e) {
            assertTrue(e.getMessage().indexOf(EJBObject.class.getName()) > 0);
        }
        try {
            new EJBClientComponentAdapter("Hello", HelloImpl.class, HelloHome.class, m_environment, false);
            fail("Should have thrown a PicoIntrospectionException");
        } catch (PicoIntrospectionException e) {
            assertTrue(e.getMessage().endsWith("interface"));
        }
    }

    /**
     * Test lookup failures with JNDI
     * @throws ClassNotFoundException
     * @throws RemoteException
     */
    public void testJNDILookup() throws ClassNotFoundException, RemoteException {
        m_initialContextMock.expects(once()).method("lookup").with(eq("Foo")).will(returnValue(new FooHomeImpl()));
        try {
            new EJBClientComponentAdapter("Foo", FooBar.class, m_environment, true);
            fail("Should have thrown a PicoIntrospectionException");
        } catch (PicoIntrospectionException e) {
            assertTrue(e.getCause() instanceof NoSuchMethodException);
        }
        m_initialContextMock.expects(once()).method("lookup").with(eq("Bar")).will(returnValue(new BarHomeImpl()));
        try {
            new EJBClientComponentAdapter("Bar", FooBar.class, m_environment, true);
            fail("Should have thrown a PicoIntrospectionException");
        } catch (PicoIntrospectionException e) {
            assertTrue(e.getCause() instanceof ClassCastException);
        }
        m_initialContextMock.expects(once()).method("lookup").with(eq("NoEntry")).will(throwException(new NameNotFoundException()));
        try {
            new EJBClientComponentAdapter("NoEntry", Hello.class, m_environment, true);
            fail("Should have thrown a ServiceUnavailableException");
        } catch (ServiceUnavailableException e) {
            assertTrue(e.getCause() instanceof NamingException);
        }
        final Properties systemProperties = System.getProperties();
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "foo.Bar");
        final ComponentAdapter componentAdapter = new EJBClientComponentAdapter("Home", Hello.class);
        final Hello hello = (Hello)componentAdapter.getComponentInstance(null);
        try {
            hello.getHelloWorld();
            fail("Should have thrown a PicoInitializationException");
        } catch (PicoInitializationException e) {
            assertTrue(e.getCause() instanceof NoInitialContextException);
        } finally {
            System.setProperties(systemProperties);
        }
    }
    
    /**
     * Test failures creating the EJB.
     * @throws ClassNotFoundException
     * @throws RemoteException
     */
    public void testComponentCreationFailures() throws ClassNotFoundException, RemoteException {
        final Mock helloHomeMock = mock(HelloHome.class);
        final Mock helloMock = mock(Hello.class);
        m_initialContextMock.stubs().method("lookup").with(eq("Hello")).will(returnValue(helloHomeMock.proxy()));
        Throwable t = new SecurityException("junit");
        helloHomeMock.expects(once()).method("create").withNoArguments().will(throwException(t));
        try {
            new EJBClientComponentAdapter("Hello", Hello.class, m_environment, true);
            fail("Should have thrown a SecurityException");
        } catch (SecurityException e) {
            assertSame(t, e);
        }
        t = new RuntimeException("junit");
        helloHomeMock.expects(once()).method("create").withNoArguments().will(throwException(t));
        try {
            new EJBClientComponentAdapter("Hello", Hello.class, m_environment, true);
            fail("Should have thrown a RuntimeException");
        } catch (RuntimeException e) {
            assertSame(t, e);
        }
        t = new Error("junit");
        helloHomeMock.expects(once()).method("create").withNoArguments().will(throwException(t));
        try {
            new EJBClientComponentAdapter("Hello", Hello.class, m_environment, true);
            fail("Should have thrown an Error");
        } catch (Error e) {
            assertSame(t, e);
        }
        t = new CreateException("junit");
        helloHomeMock.expects(once()).method("create").withNoArguments().will(throwException(t));
        try {
            new EJBClientComponentAdapter("Hello", Hello.class, m_environment, true);
            fail("Should have thrown a PicoInvocationTargetInitializationException");
        } catch (PicoInvocationTargetInitializationException e) {
            assertSame(t, e.getCause());
        }
        final ComponentAdapter componentAdapter = new EJBClientComponentAdapter("Hello", Hello.class, m_environment, false);
        helloHomeMock.expects(once()).method("create").withNoArguments().will(returnValue(helloMock.proxy()));
        final Hello hello = (Hello)componentAdapter.getComponentInstance(null);
        t = new RemoteException("junit");
        helloMock.expects(once()).method("getHelloWorld").withNoArguments().will(throwException(t));
        try {
            hello.getHelloWorld();
            fail("Should have thrown a RemoteException");
        } catch (RemoteException e) {
            assertSame(t, e);
        }
        t = new NoSuchObjectException("junit");
        helloHomeMock.expects(once()).method("create").withNoArguments().will(returnValue(helloMock.proxy()));
        helloMock.expects(once()).method("getHelloWorld").withNoArguments().will(throwException(t));
        try {
            hello.getHelloWorld();
            fail("Should have thrown a ServiceUnavailableException");
        } catch (ServiceUnavailableException e) {
            assertSame(t, e.getCause());
        }
    }

    /**
     * Test for failover capability.
     * @throws ClassNotFoundException
     * @throws RemoteException
     */
    public final void testFailover() throws ClassNotFoundException, RemoteException {
        final ComponentAdapter componentAdapter = new EJBClientComponentAdapter("Hello", Hello.class, m_environment, false);
        final Hello hello = (Hello)componentAdapter.getComponentInstance(null);
        assertNotNull(hello);
        NamingException exception = new CommunicationException();
        exception.setRootCause(new SocketTimeoutException());
        m_initialContextMock.stubs().method("lookup").with(eq("Hello")).will(
                onConsecutiveCalls(throwException(exception), returnValue(new HelloHomeImpl())));
        try {
            hello.getHelloWorld();
            fail("Should have thrown a ServiceUnavailableException");
        } catch (ServiceUnavailableException e) {
            assertTrue(((NamingException)e.getCause()).getRootCause() instanceof SocketTimeoutException);
        }
        exception.setRootCause(new NoSuchObjectException("Hello"));
        m_initialContextMock.stubs().method("lookup").with(eq("Hello")).will(
                onConsecutiveCalls(throwException(exception), returnValue(new HelloHomeImpl())));
        try {
            hello.getHelloWorld();
            fail("Should have thrown a ServiceUnavailableException");
        } catch (ServiceUnavailableException e) {
            assertTrue(((NamingException)e.getCause()).getRootCause() instanceof NoSuchObjectException);
        }
        assertEquals("Hello World!", hello.getHelloWorld());
    }

    /**
     * Test for EJBClientComponentAdapter.getComponentInstance()
     * @throws ClassNotFoundException
     * @throws RemoteException
     */
    public final void testGetComponentInstance() throws ClassNotFoundException, RemoteException {
        m_initialContextMock.expects(once()).method("lookup").with(eq("Hello")).will(returnValue(new HelloHomeImpl()));
        final ComponentAdapter componentAdapter = new EJBClientComponentAdapter("Hello", Hello.class, m_environment, true);
        componentAdapter.verify(null); // Dummy call, done for coverage
        final Hello hello = (Hello)componentAdapter.getComponentInstance(null);
        assertNotNull(hello);
        assertEquals("Hello World!", hello.getHelloWorld());
    }

    /**
     * Test for EJBClientComponentAdapter.getComponentKey()
     * @throws ClassNotFoundException
     */
    public final void testGetComponentKey() throws ClassNotFoundException {
        m_initialContextMock.expects(once()).method("lookup").with(eq("Hello")).will(returnValue(new HelloHomeImpl()));
        final ComponentAdapter componentAdapter = new EJBClientComponentAdapter("Hello", Hello.class, m_environment, true);
        assertSame("Hello", componentAdapter.getComponentKey());
    }

    /**
     * Test for EJBClientComponentAdapter.getComponentImplementation()
     * @throws ClassNotFoundException
     */
    public final void testGetComponentImplementation() throws ClassNotFoundException {
        m_initialContextMock.expects(once()).method("lookup").with(eq("Hello")).will(returnValue(new HelloHomeImpl()));
        final ComponentAdapter componentAdapter = new EJBClientComponentAdapter("Hello", Hello.class, m_environment, true);
        assertSame(Hello.class, componentAdapter.getComponentImplementation());
    }
}
