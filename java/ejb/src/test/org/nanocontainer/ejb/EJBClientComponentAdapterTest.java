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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import javax.ejb.EJBHome;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.nanocontainer.ejb.EJBClientComponentAdapter;
import org.nanocontainer.ejb.rmi.mock.PortableRemoteObjectDelegateMock;
import org.nanocontainer.ejb.testmodel.BarHomeImpl;
import org.nanocontainer.ejb.testmodel.FooBarHome;
import org.nanocontainer.ejb.testmodel.FooHomeImpl;
import org.nanocontainer.ejb.testmodel.Hello;
import org.nanocontainer.ejb.testmodel.HelloHome;
import org.nanocontainer.ejb.testmodel.HelloHomeImpl;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.DefaultPicoContainer;


import junit.framework.TestCase;

/**
 * Unit test for EJBClientComponentAdapter.
 * @author J&ouml;rg Schaible
 */
public class EJBClientComponentAdapterTest
        extends TestCase {
    
    private Properties m_systemProperties;
    private ComponentAdapter m_componentAdapter;
    private InitialContext m_initialContext;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        m_systemProperties = System.getProperties();
        m_systemProperties.setProperty("javax.rmi.CORBA.PortableRemoteObjectClass", PortableRemoteObjectDelegateMock.class.getName());
        
        final Map narrowMap = new HashMap();
        narrowMap.put("HelloToNarrow", HelloHomeImpl.class.getConstructor(null));
        narrowMap.put("FooToNarrow", FooHomeImpl.class.getConstructor(null));
        narrowMap.put("BarToNarrow", BarHomeImpl.class.getConstructor(null));
         PortableRemoteObjectDelegateMock.setNarrowMap(narrowMap);
        final Hashtable env = new Hashtable();
        env.put("java.naming.factory.initial", "org.osjava.jndi.PropertiesFactory");
        env.put("org.osjava.jndi.root", "file://src/test-conf/jndi");
        env.put("org.osjava.jndi.delimiter", "/");
        m_initialContext = new InitialContext(env);
        m_componentAdapter = new EJBClientComponentAdapter("Hello", HelloHome.class, m_initialContext);
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        System.setProperties(m_systemProperties);
        super.tearDown();
    }

    /**
     * Test for constructor of EJBClientComponentAdapter
     */
    public final void testEJBComponentAdapter() {
        assertNotNull(m_componentAdapter);
        try {
            new EJBClientComponentAdapter("Foo", FooBarHome.class, m_initialContext);
            fail("Should have thrown a PicoIntrospectionException");
        } catch (PicoIntrospectionException e) {
            assertTrue(e.getCause() instanceof NoSuchMethodException);
        }
        try {
            new EJBClientComponentAdapter("Bar", BarHomeImpl.class, m_initialContext);
            fail("Should have thrown a PicoIntrospectionException");
        } catch (PicoIntrospectionException e) {
            assertTrue(e.getCause() instanceof ClassCastException);
            assertTrue(e.getCause().getCause() instanceof IllegalArgumentException);
        }
        try {
            new EJBClientComponentAdapter("NoEntry", EJBHome.class, m_initialContext);
            fail("Should have thrown a PicoIntrospectionException");
        } catch (PicoIntrospectionException e) {
            assertTrue(e.getCause() instanceof NamingException);
        }
        try {
            new EJBClientComponentAdapter("Hello", FooBarHome.class, m_initialContext);
            fail("Should have thrown a PicoIntrospectionException");
        } catch (PicoIntrospectionException e) {
            assertTrue(e.getCause() instanceof ClassCastException);
        }
        try {
            new EJBClientComponentAdapter("Hello", StringBuffer.class, m_initialContext);
            fail("Should have thrown a AssignabilityRegistrationException");
        } catch (AssignabilityRegistrationException e) {
            assertTrue(e.getMessage().indexOf(EJBHome.class.getName()) > 0);
        }
    }

    /**
     * Test for EJBClientComponentAdapter.getComponentInstance()
     */
    public final void testGetComponentInstance() {
        final Hello hello = (Hello)m_componentAdapter.getComponentInstance();
        assertNotNull(hello);
        assertEquals("Hello World!", hello.getHelloWorld());
    }

    /**
     * Test for EJBClientComponentAdapter.verify()
     */
    public final void testVerify() {
        m_componentAdapter.verify(); // Dummy test, done for coverage
    }

    /**
     * Test for EJBClientComponentAdapter.getComponentKey()
     */
    public final void testGetComponentKey() {
        assertSame("Hello", m_componentAdapter.getComponentKey());
    }

    /**
     * Test for EJBClientComponentAdapter.getComponentImplementation()
     */
    public final void testGetComponentImplementation() {
        assertSame(Hello.class, m_componentAdapter.getComponentImplementation());
    }

    /**
     * Test for getting and setting the Container
     */
    public final void testGetAndSetContainer() {
        final PicoContainer picoContainer = new DefaultPicoContainer();
        m_componentAdapter.setContainer(picoContainer);
        assertSame(picoContainer, m_componentAdapter.getContainer());
    }

    /**
     * Test for EJBClientComponentAdapter.toString()
     */
    public final void testToString() {
        final String stringRep = m_componentAdapter.toString(); 
        assertEquals(EJBClientComponentAdapter.class.getName() + "[Hello]", stringRep);
    }
}
