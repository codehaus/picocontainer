/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Michael Ward                                    		 *
 *****************************************************************************/

package org.nanocontainer.jmx;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.nanocontainer.testmodel.Wilma;
import org.nanocontainer.testmodel.WilmaImpl;

import javax.management.ObjectName;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;

import junit.framework.TestCase;

/**
 * @author Michael Ward
 * @version $Revision$
 */
public class JMXVisitorTestCase extends TestCase {

	private MutablePicoContainer picoContainer;
	private MBeanServer mBeanServer;
	private ObjectName objectName;

	protected void setUp() throws Exception {
		super.setUp();

		picoContainer = new DefaultPicoContainer();
		mBeanServer = MBeanServerFactory.createMBeanServer();
		objectName = new ObjectName("domian-name:hello=world");

		picoContainer.registerComponentInstance(mBeanServer);
	}

	public void testVisitComponentAdapterForMBeanInfo() throws Exception {
		JMXVisitor jmxVisitor = new JMXVisitor();
		MBeanInfo mBeanInfo = JMXTestFixture.createMBeanInfo();
		jmxVisitor.register(objectName, mBeanInfo);

		picoContainer.registerComponentInstance(FooBar.class, new FooBar());

		assertFalse(mBeanServer.isRegistered(objectName));
		picoContainer.accept(jmxVisitor);
		assertTrue(mBeanServer.isRegistered(objectName));
		assertEquals(new Integer(1), mBeanServer.getAttribute(objectName, "Count"));
	}

	public void testVisitComponentAdapterForStandardMBean() throws Exception {
		WilmaImpl wilma = new WilmaImpl();
		ComponentAdapter componentAdapter = new StandardMBeanComponentAdapter(wilma, Wilma.class);
		picoContainer.registerComponent(componentAdapter);

		JMXVisitor jmxVisitor = new JMXVisitor();
		jmxVisitor.register(objectName, Wilma.class);

		assertFalse(mBeanServer.isRegistered(objectName));
		picoContainer.accept(jmxVisitor);
		assertTrue(mBeanServer.isRegistered(objectName));

		// validate invokable
		assertFalse(wilma.helloCalled());
        mBeanServer.invoke(objectName, "hello", null, null);
		assertTrue(wilma.helloCalled());
	}

	public void testProxyingInterfaceImplementation() throws Exception {
		FooBar fooBar = new FooBar();
		ComponentAdapter componentAdapter = new StandardMBeanComponentAdapter(fooBar, FooBarInterface.class);
		picoContainer.registerComponent(componentAdapter);

		JMXVisitor jmxVisitor = new JMXVisitor();
		jmxVisitor.register(objectName, FooBarInterface.class);

		// validate registration
		assertFalse(mBeanServer.isRegistered(objectName));
		picoContainer.accept(jmxVisitor);
		assertTrue(mBeanServer.isRegistered(objectName));

		assertFalse("FooBar should NOT be an instance of FooBarInterface", fooBar instanceof FooBarInterface);
        assertEquals(new Integer(1), mBeanServer.getAttribute(objectName, "Count"));
	}

}
