/*****************************************************************************
 * Copyright (c) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by James Strachan                                           *
 *****************************************************************************/

package org.nanocontainer.jmx;

import junit.framework.TestCase;
import org.nanocontainer.testmodel.Wilma;
import org.nanocontainer.testmodel.WilmaImpl;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

public class MX4JComponentAdapterTestCase extends TestCase {
    private MutablePicoContainer pico;
    private MBeanServer mbeanServer;

    protected void setUp() throws Exception {
        super.setUp();
        mbeanServer = MBeanServerFactory.newMBeanServer();
        pico = new DefaultPicoContainer(new MX4JComponentAdapterFactory(mbeanServer, new DefaultComponentAdapterFactory()));
    }

    private void assertExistsInJMX(String name) throws InstanceNotFoundException, MalformedObjectNameException {
        assertExistsInJMX(new ObjectName(name));
    }

    private void assertExistsInJMX(ObjectName name) throws InstanceNotFoundException{
        ObjectInstance instance = mbeanServer.getObjectInstance(name);
        assertNotNull("Found one in MBeanServer", instance);
        assertEquals("ObjectInstance has correct class name", PicoContainerMBean.class.getName(), instance.getClassName());
    }

    public void testClassAndKeyRegistration() throws Exception {
        pico.registerComponentImplementation("pico:name=one", WilmaImpl.class);
        pico.registerComponentImplementation("pico:name=two", WilmaImpl.class);
        pico.getComponentInstances();

        assertExistsInJMX("pico:name=one");
        assertExistsInJMX("pico:name=two");
    }

    public void testClassOnlyRegistration() throws Exception {
        pico.registerComponentImplementation(WilmaImpl.class);
        pico.getComponentInstances();
        assertExistsInJMX("picomx:type=" + WilmaImpl.class.getName());
    }

    public void testObjectAndNullKeyRegistration() throws Exception {
        try {
            pico.registerComponentInstance(null, new WilmaImpl());
            fail("should have thrown NullPointerException");
        } catch (NullPointerException e) {
            // worked
        }
    }

    public void testKeyRegistration() throws Exception {
        String nameOne = "pico:name=one";
        ObjectName nameTwo = new ObjectName("pico:name=two;type=full");

        pico.registerComponentImplementation(nameOne, WilmaImpl.class);
        pico.registerComponentImplementation(nameTwo, WilmaImpl.class);

        Wilma w1 = (Wilma) pico.getComponentInstance(nameOne);
        Wilma w2 = (Wilma) pico.getComponentInstance(nameTwo);

        assertNotSame(w1, w2);

        assertExistsInJMX(nameOne);
        assertExistsInJMX(nameTwo);

        ObjectInstance instance1 = mbeanServer.getObjectInstance(new ObjectName(nameOne));
        assertNotNull("Found one in MBeanServer", instance1);
        assertEquals("ObjectInstance has correct class name", PicoContainerMBean.class.getName(), instance1.getClassName());

        ObjectInstance instance2 = mbeanServer.getObjectInstance(nameTwo);
        assertNotNull("Found one in MBeanServer", instance2);
        assertEquals("ObjectInstance has correct class name", PicoContainerMBean.class.getName(), instance2.getClassName());
    }
}
