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

import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.nanocontainer.testmodel.WilmaImpl;
import org.nanocontainer.jmx.AbstractNanoMXTestCase;

public class NanoMXContainerTestCase extends AbstractNanoMXTestCase {

    public void testKeyRegistration() throws Exception {
        NanoMXContainer pico = createNanoMXContainer();

        WilmaImpl one = new WilmaImpl();
        WilmaImpl two = new WilmaImpl();

        pico.registerComponent("nano:name=one", one);
        pico.registerComponent("nano:name=two", two);

        pico.instantiateComponents();

        assertEquals("Wrong number of comps in the container", 2, pico.getComponents().length);

        assertEquals("Looking up one Wilma", one, pico.getComponent("nano:name=one"));
        assertEquals("Looking up two Wilma", two, pico.getComponent("nano:name=two"));

        assertTrue("Object one the same", one == pico.getComponent("nano:name=one"));
        assertTrue("Object two the same", two == pico.getComponent("nano:name=two"));

        assertEquals("Lookup of unknown key should return null", null, pico.getComponent("unknown"));

        assertExistsInJMX(pico, "nano:name=one");
        assertExistsInJMX(pico, "nano:name=two");
    }

    public void testObjectNameRegistration() throws Exception {
        NanoMXContainer pico = createNanoMXContainer();

        ObjectName nameOne = new ObjectName("nano:name=one;type=full");
        ObjectName nameTwo = new ObjectName("nano:name=two;type=full");
        ObjectName nameUnknown = new ObjectName("nano:name=unknown;type=full");

        WilmaImpl one = new WilmaImpl();
        WilmaImpl two = new WilmaImpl();

        pico.registerComponent(nameOne, one);
        pico.registerComponent(nameTwo, two);

        pico.instantiateComponents();

        assertEquals("Wrong number of comps in the container", 2, pico.getComponents().length);

        assertEquals("Looking up one Wilma", one, pico.getComponent(nameOne));
        assertEquals("Looking up two Wilma", two, pico.getComponent(nameTwo));

        assertTrue("Object one the same", one == pico.getComponent(nameOne));
        assertTrue("Object two the same", two == pico.getComponent(nameTwo));

        assertEquals("Lookup of unknown key should return null", null, pico.getComponent(nameUnknown));

        assertExistsInJMX(pico, "nano:name=one;type=full");
        assertExistsInJMX(pico, "nano:name=two;type=full");
    }

    public void testQueryMBeanServerAfterRegistration() throws Exception {
        NanoMXContainer pico = createNanoMXContainer();

        ObjectName nameOne = new ObjectName("nano:name=one;type=full");
        ObjectName nameTwo = new ObjectName("nano:name=two;type=full");

        WilmaImpl one = new WilmaImpl();
        WilmaImpl two = new WilmaImpl();

        pico.registerComponent(nameOne, one);
        pico.registerComponent(nameTwo, two);

        pico.instantiateComponents();

        assertEquals("Wrong number of comps in the container", 2, pico.getComponents().length);

        MBeanServer server = pico.getMBeanServer();

        assertTrue("MBeanServer is not null", server != null);

        ObjectInstance instance = server.getObjectInstance(nameOne);

        assertTrue("Found one in MBeanServer", instance != null);

        assertEquals("ObjectInstance has correct class name", NanoMBean.class.getName(), instance.getClassName());
    }

    public void testDuplicateRegistration() throws Exception {
        NanoMXContainer pico = createNanoMXContainer();

        pico.registerComponent("nano:name=one", new WilmaImpl());
        try {
            pico.registerComponent("nano:name=one", new WilmaImpl());
            fail("Should have barfed with dupe registration");
        }
        catch (Exception e) {
            // expected
//            assertTrue("Wrong key", e.getKey() == "nano:name=one");
//            assertTrue("Wrong component", e.getComponent() instanceof WilmaImpl);
//            assertTrue(
//                "Wrong message: " + e.getMessage(),
//                e.getMessage().startsWith("Key: one duplicated, cannot register:"));
        }
    }

    public void testHasComponentByKey() throws Exception {
        NanoMXContainer pico = createNanoMXContainer();

        assertTrue("should not have non existent component", !pico.hasComponent("nano:name=doesNotExist"));

        pico.registerComponent("nano:name=foo", new WilmaImpl());

        assertTrue("has component", pico.hasComponent("nano:name=foo"));
    }

//    public void testRemoveComponent() throws Exception {
//        NanoMXContainer pico = createJmxContainer();
//
//        // remove non existent doesn't throw exception
//        pico.removeComponent("nano:name=doesNotExist");
//
//        pico.registerComponent("nano:name=foo", new WilmaImpl());
//
//        assertTrue("has component", pico.getComponent("nano:name=foo") != null);
//
//        pico.removeComponent("nano:name=foo");
//
//        assertEquals("hasComponent(foo)", false, pico.hasComponent("nano:name=foo"));
//        assertEquals("getComponent(foo)", null, pico.getComponent("nano:name=foo"));
//    }

}
