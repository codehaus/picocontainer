/*****************************************************************************
 * Copyright (c) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Michael Ward                                             *
 *****************************************************************************/

package org.nanocontainer.jmx;

import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.MBeanServerFactory;


/**
 * @author Michael Ward
 * @version $Revision$
 */
public class MBeanComponentAdapterTestCase extends TestCase {
	private MutablePicoContainer pico;
    private MBeanServer mbeanServer;
	private ObjectName objectName;

    protected void setUp() throws Exception {
        super.setUp();
        mbeanServer = MBeanServerFactory.newMBeanServer();
        pico = new DefaultPicoContainer();
		pico.registerComponentInstance(MBeanServer.class, mbeanServer);
		objectName = new ObjectName("pico:name=one");
    }

	public void testMissingMBeanInfo() throws Exception {
		pico.registerComponent(JMXTestFixture.createJMXComponentAdapter(objectName));

		try {
			pico.getComponentInstances();
			fail("MBeanInfoMissingException should have been Thrown");
		} catch (MBeanInfoMissingException ignore) {
		}
	}

	public void testMBeanInfoRegisteredAsString() throws Exception {
		pico.registerComponentInstance(FooBarMBeanInfo.class.getName(), JMXTestFixture.createMBeanInfo());
		validateAgainstMBeanServer();
	}

	public void testMBeanInfoRegisteredAsClass() throws Exception {
        pico.registerComponentInstance(FooBarMBeanInfo.class, JMXTestFixture.createFooBarMBeanInfo());
		validateAgainstMBeanServer();
	}

	protected void validateAgainstMBeanServer() throws Exception {
		pico.registerComponent(JMXTestFixture.createJMXComponentAdapter(objectName));

		assertFalse(mbeanServer.isRegistered(objectName));
		pico.getComponentInstances(); // does the actual registration to the MBeanServer
		assertTrue(mbeanServer.isRegistered(objectName));
		assertEquals(new Integer(1), mbeanServer.getAttribute(objectName, "Count"));
    }
}
