/*****************************************************************************
 * Copyright (c) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by James Strachan                                           *
 *****************************************************************************/

package org.picoextras.jmx;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picoextras.testmodel.WilmaImpl;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

public class NanoMXContainerTestCase extends AbstractNanoMXTestCase {

    public void testKeyRegistration() throws Exception {
        NanoMXContainer pico = new NanoMXContainer();

        WilmaImpl one = new WilmaImpl();
        WilmaImpl two = new WilmaImpl();

        pico.registerComponentInstance("nano:name=one", one);
        pico.registerComponentInstance("nano:name=two", two);

        assertEquals("Wrong number of comps in the internals", 2, pico.getComponentInstances().size());

        assertEquals("Looking up one Wilma", one, pico.getComponentInstance("nano:name=one"));
        assertEquals("Looking up two Wilma", two, pico.getComponentInstance("nano:name=two"));

        assertTrue("Object one the same", one == pico.getComponentInstance("nano:name=one"));
        assertTrue("Object two the same", two == pico.getComponentInstance("nano:name=two"));

        assertEquals("Lookup of unknown key should return null", null, pico.getComponentInstance("unknown"));

        assertExistsInJMX(pico.getMBeanServer(), "nano:name=one");
        assertExistsInJMX(pico.getMBeanServer(), "nano:name=two");
    }

    public void testObjectNameRegistration() throws Exception {
        NanoMXContainer pico = new NanoMXContainer();

        ObjectName nameOne = new ObjectName("nano:name=one;type=full");
        ObjectName nameTwo = new ObjectName("nano:name=two;type=full");
        ObjectName nameUnknown = new ObjectName("nano:name=unknown;type=full");

        WilmaImpl one = new WilmaImpl();
        WilmaImpl two = new WilmaImpl();

        pico.registerComponentInstance(nameOne, one);
        pico.registerComponentInstance(nameTwo, two);

        assertEquals("Wrong number of comps in the internals", 2, pico.getComponentInstances().size());

        assertEquals("Looking up one Wilma", one, pico.getComponentInstance(nameOne));
        assertEquals("Looking up two Wilma", two, pico.getComponentInstance(nameTwo));

        assertTrue("Object one the same", one == pico.getComponentInstance(nameOne));
        assertTrue("Object two the same", two == pico.getComponentInstance(nameTwo));

        assertEquals("Lookup of unknown key should return null", null, pico.getComponentInstance(nameUnknown));

        assertExistsInJMX(pico.getMBeanServer(), "nano:name=one;type=full");
        assertExistsInJMX(pico.getMBeanServer(), "nano:name=two;type=full");
    }

    public void testQueryMBeanServerAfterRegistration() throws Exception {
        NanoMXContainer pico = new NanoMXContainer();

        ObjectName nameOne = new ObjectName("nano:name=one;type=full");
        ObjectName nameTwo = new ObjectName("nano:name=two;type=full");

        WilmaImpl one = new WilmaImpl();
        WilmaImpl two = new WilmaImpl();

        pico.registerComponentInstance(nameOne, one);
        pico.registerComponentInstance(nameTwo, two);

        assertEquals("Wrong number of comps in the internals", 2, pico.getComponentInstances().size());

        ObjectInstance instance = pico.getMBeanServer().getObjectInstance(nameOne);

        assertTrue("Found one in MBeanServer", instance != null);

        assertEquals("ObjectInstance has correct class name", NanoMBean.class.getName(), instance.getClassName());
    }

    public void testDuplicateRegistration() throws Exception {
        NanoMXContainer pico = new NanoMXContainer();

        pico.registerComponentInstance("nano:name=one", new WilmaImpl());
        try {
            pico.registerComponentInstance("nano:name=one", new WilmaImpl());
            fail("Should have barfed with dupe registration");
        } catch (Exception e) {
            // expected
//            assertTrue("Wrong key", e.getKey() == "nano:name=one");
//            assertTrue("Wrong component", e.findComponentInstance() instanceof WilmaImpl);
//            assertTrue(
//                "Wrong message: " + e.getMessage(),
//                e.getMessage().startsWith("Key: one duplicated, cannot register:"));
        }
    }

    public void testHasComponentByKey() throws Exception {
        MBeanServer mbeanServer = MBeanServerFactory.newMBeanServer();
        NanoMXComponentAdapterFactory factory = new NanoMXComponentAdapterFactory(mbeanServer, new DefaultComponentAdapterFactory());
        MutablePicoContainer pico = new DefaultPicoContainer(factory);

        assertTrue("should not have non existent component", !pico.hasComponent("nano:name=doesNotExist"));

        pico.registerComponentInstance("nano:name=foo", new WilmaImpl());

        assertTrue("has component", pico.hasComponent("nano:name=foo"));
    }

//    public void testRemoveComponent() throws Exception {
//        NanoMXContainer pico = createJmxContainer();
//
//        // remove non existent doesn't throw exception
//        pico.removeComponent("nano:name=doesNotExist");
//
//        pico.registerComponentInstance("nano:name=foo", new WilmaImpl());
//
//        assertTrue("has component", pico.findComponentInstance("nano:name=foo") != null);
//
//        pico.removeComponent("nano:name=foo");
//
//        assertEquals("hasComponent(foo)", false, pico.hasComponent("nano:name=foo"));
//        assertEquals("findComponentInstance(foo)", null, pico.findComponentInstance("nano:name=foo"));
//    }

}
