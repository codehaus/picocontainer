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
import org.picocontainer.*;
import org.picocontainer.defaults.*;

import javax.management.*;

/**
 * @author Michael Ward
 * @version $Revision$
 */
public class JMXComponentAdapterTestCase extends TestCase {
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

	private ComponentAdapter createJMXComponentAdapter() {
        return new JMXComponentAdapterFactory(new DefaultComponentAdapterFactory())
				.createComponentAdapter(objectName, SampleMBean.class, null);
    }

	private MBeanInfo createMBeanInfo() {
		MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[] {
			new MBeanAttributeInfo("Count", int.class.toString(), "desc", true, false, false)
		};

		return new MBeanInfo(SampleMBean.class.toString(), "desc", attributes, null, null, null);
	}

	public void testMissingMBeanInfo() throws Exception {
		pico.registerComponent(createJMXComponentAdapter());

		try {
			pico.getComponentInstances();
			fail("MBeanInfoMissingException should have been Thrown");
		} catch (MBeanInfoMissingException ignore) {
		}
	}

	public void testSuccessful() throws Exception {
		// register the MBeanInfo
		pico.registerComponentInstance(SampleMBean.class.toString() + "MBeanInfo", createMBeanInfo());
		pico.registerComponent(createJMXComponentAdapter());

		assertFalse(mbeanServer.isRegistered(objectName));
		pico.getComponentInstances(); // does the actual registration to the MBeanServer
		assertTrue(mbeanServer.isRegistered(objectName));
		assertEquals(new Integer(1), mbeanServer.getAttribute(objectName, "Count"));
    }

	public void testMBeanInfoRegisteredAsString() throws Exception {
		// todo
	}

	public void testMBeanInfoRegisteredAsClass() throws Exception {
        // todo
	}
}
