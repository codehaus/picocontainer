/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/

package org.nanocontainer.jmx.mx4j;

import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

import org.nanocontainer.jmx.JMXVisitor;
import org.nanocontainer.jmx.testmodel.Person;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

import junit.framework.TestCase;

/**
 * Test MX4JVisitor.
 * @author J&ouml;rg Schaible
 */
public class MX4JVisitorTest extends TestCase {

    private MutablePicoContainer picoContainer;
    private MBeanServer mBeanServer;
    private ObjectName objectName;

    protected void setUp() throws Exception {
        super.setUp();

        picoContainer = new DefaultPicoContainer();
        mBeanServer = MBeanServerFactory.createMBeanServer();
        objectName = new ObjectName("domain-name:hello=world");

        picoContainer.registerComponentInstance(mBeanServer);
    }

    public void testVisitComponentAdapterForMBeanInfo() throws Exception {
        JMXVisitor jmxVisitor = new MX4JVisitor();
        MBeanInfo mBeanInfo = Person.createMBeanInfo();
        jmxVisitor.register(objectName, mBeanInfo);

        picoContainer.registerComponentInstance(Person.class, new Person());

        assertFalse(mBeanServer.isRegistered(objectName));
        picoContainer.accept(jmxVisitor);
        assertTrue(mBeanServer.isRegistered(objectName));
        assertEquals("John Doe", mBeanServer.getAttribute(objectName, "Name"));
    }

}
