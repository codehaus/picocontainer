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

import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

import org.nanocontainer.jmx.mx4j.MX4JDynamicMBeanFactory;
import org.nanocontainer.jmx.testmodel.Person;
import org.nanocontainer.jmx.testmodel.PersonMBean;
import org.nanocontainer.proxytoys.AssimilatingComponentAdapter;
import org.nanocontainer.testmodel.Wilma;
import org.nanocontainer.testmodel.WilmaImpl;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.InstanceComponentAdapter;
import org.picocontainer.testmodel.PersonBean;

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
		objectName = new ObjectName("domain-name:hello=world");

		picoContainer.registerComponentInstance(mBeanServer);
	}

	public void testVisitComponentAdapterForMBeanInfo() throws Exception {
		JMXVisitor jmxVisitor = new JMXVisitor();
		MBeanInfo mBeanInfo = Person.createMBeanInfo();
		jmxVisitor.register(objectName, mBeanInfo);

        picoContainer.registerComponentImplementation(MX4JDynamicMBeanFactory.class);
		picoContainer.registerComponentImplementation(Person.class);

		assertFalse(mBeanServer.isRegistered(objectName));
		picoContainer.accept(jmxVisitor);
		assertTrue(mBeanServer.isRegistered(objectName));
		assertEquals("John Doe", mBeanServer.getAttribute(objectName, "Name"));
	}

	public void testVisitComponentAdapterForStandardMBean() throws Exception {
		WilmaImpl wilma = new WilmaImpl();
		ComponentAdapter componentAdapter = new InstanceComponentAdapter(Wilma.class, wilma);
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
		PersonBean person = new PersonBean();
        person.setName("John Doe");
		ComponentAdapter componentAdapter = new AssimilatingComponentAdapter(PersonMBean.class, person);
		picoContainer.registerComponent(componentAdapter);

		JMXVisitor jmxVisitor = new JMXVisitor();
		jmxVisitor.register(objectName, PersonMBean.class);

		// validate registration
		assertFalse(mBeanServer.isRegistered(objectName));
		picoContainer.accept(jmxVisitor);
		assertTrue(mBeanServer.isRegistered(objectName));

		assertFalse("person should NOT be an instance of PersonMBean", person instanceof PersonMBean);
        assertEquals("John Doe", mBeanServer.getAttribute(objectName, "Name"));
	}

    /**
     * Proposed test of using Non Class keys for registering components
     * and MBeanInfo implementations.
     * @throws Exception
     */
    public void XXXtestVisitComponentAdapterUsingNonClassKeyForMBeanInfo() throws Exception {
            JMXVisitor jmxVisitor = new JMXVisitor();
            MBeanInfo mBeanInfo = Person.createMBeanInfo();
            jmxVisitor.register(objectName, mBeanInfo);//fails test
//            jmxVisitor.registerKeyString("foo",objectName, mBeanInfo);//passes test

            picoContainer.registerComponentInstance("foo", new Person());

            assertFalse(mBeanServer.isRegistered(objectName));
            picoContainer.accept(jmxVisitor);
            assertTrue(mBeanServer.isRegistered(objectName));
            assertEquals("John Doe", mBeanServer.getAttribute(objectName, "Name"));
    }

}
