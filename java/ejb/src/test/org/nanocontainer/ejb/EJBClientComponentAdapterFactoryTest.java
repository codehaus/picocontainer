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

import junit.framework.TestCase;
import org.nanocontainer.ejb.rmi.mock.PortableRemoteObjectDelegateMock;
import org.nanocontainer.ejb.testmodel.BarHomeImpl;
import org.nanocontainer.ejb.testmodel.FooHomeImpl;
import org.nanocontainer.ejb.testmodel.Hello;
import org.nanocontainer.ejb.testmodel.HelloHome;
import org.nanocontainer.ejb.testmodel.HelloHomeImpl;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.defaults.ComponentAdapterFactory;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

/**
 * Unit test for EJBClientComponentAdapterFactory.
 *
 * @author J&ouml;rg Schaible
 */
public class EJBClientComponentAdapterFactoryTest
        extends TestCase {

    private Properties m_systemProperties;

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
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        System.setProperties(m_systemProperties);
        super.tearDown();
    }

    /**
     * Test for standard constructor using system InitialContext
     *
     * @throws NamingException
     */
    public final void testSystemInitialContext() throws NamingException {
        System.setProperty("java.naming.factory.initial", "org.osjava.jndi.PropertiesFactory");
        System.setProperty("org.osjava.jndi.root", "file://src/test-conf/jndi");
        System.setProperty("org.osjava.jndi.delimiter", "/");
        final ComponentAdapterFactory caf = new EJBClientComponentAdapterFactory();
        final ComponentAdapter componentAdapter =
                caf.createComponentAdapter("Hello", HelloHome.class, null);
        assertNotNull(componentAdapter);
        final Object hello1 = componentAdapter.getComponentInstance(null);
        final Object hello2 = componentAdapter.getComponentInstance(null);
        assertNotNull(hello1);
        assertTrue(Hello.class.isAssignableFrom(hello1.getClass()));
        assertSame(hello1, hello2);
    }

    /**
     * Test for constructor using a prepared environment for the InitialContext
     *
     * @throws NamingException
     */
    public final void testPreparedInitialContext() throws NamingException {
        final Hashtable env = new Hashtable();
        env.put("java.naming.factory.initial", "org.osjava.jndi.PropertiesFactory");
        env.put("org.osjava.jndi.root", "file://src/test-conf/jndi");
        env.put("org.osjava.jndi.delimiter", "/");
        final InitialContext initialContext = new InitialContext(env);
        final ComponentAdapterFactory caf = new EJBClientComponentAdapterFactory(initialContext);
        final ComponentAdapter componentAdapter =
                caf.createComponentAdapter("Hello", HelloHome.class, null);
        assertNotNull(componentAdapter);
        final Object hello1 = componentAdapter.getComponentInstance(null);
        final Object hello2 = componentAdapter.getComponentInstance(null);
        assertNotNull(hello1);
        assertTrue(Hello.class.isAssignableFrom(hello1.getClass()));
        assertSame(hello1, hello2);
    }
}

