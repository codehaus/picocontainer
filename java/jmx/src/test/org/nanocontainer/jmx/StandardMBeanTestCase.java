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

import javax.management.ObjectName;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.NotCompliantMBeanException;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.nanocontainer.testmodel.WilmaImpl;
import org.nanocontainer.testmodel.Wilma;

/**
 * @author Michael Ward
 * @version $Revision$
 */
public class StandardMBeanTestCase extends TestCase {
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

	public void testAccessViaMBeanServer() throws Exception {
		WilmaImpl wilma = new WilmaImpl();
		ComponentAdapter componentAdapter = new StandardMBeanComponentAdapter(objectName, wilma, Wilma.class);
		pico.registerComponent(componentAdapter);

		// validate registration
		assertFalse(mbeanServer.isRegistered(objectName));
		pico.getComponentInstances();
		assertTrue(mbeanServer.isRegistered(objectName));

		// validate invokable
		assertFalse(wilma.helloCalled());
        mbeanServer.invoke(objectName, "hello", null, null);
		assertTrue(wilma.helloCalled());
	}

	public void testForceInterfaceImplementation() throws Exception {
		FooBar fooBar = new FooBar();
		ComponentAdapter componentAdapter = new StandardMBeanComponentAdapter(objectName, fooBar, FooBarInterface.class);
		pico.registerComponent(componentAdapter);

		// validate registration
		assertFalse(mbeanServer.isRegistered(objectName));
		pico.getComponentInstances();
		assertTrue(mbeanServer.isRegistered(objectName));

		assertFalse("FooBar should NOT be an instance of FooBarInterface", fooBar instanceof FooBarInterface);
        assertEquals(new Integer(1), mbeanServer.getAttribute(objectName, "Count"));
	}

	public void testInvalidMBeanScenario() {
		FooBar fooBar = new FooBar();
		try {
			new StandardMBeanComponentAdapter(objectName, fooBar, ObjectName.class);
			fail("NotCompliantMBeanException should have been thrown");
		} catch (NotCompliantMBeanException e) {
			e.printStackTrace();
		}
	}
}
