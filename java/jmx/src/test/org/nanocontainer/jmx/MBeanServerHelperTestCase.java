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

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * @author Michael Ward
 * @version $Revision$
 */
public class MBeanServerHelperTestCase extends MockObjectTestCase {

	private MutablePicoContainer pico;
	private ObjectName objectName;

    protected void setUp() throws Exception {
        super.setUp();
		pico = new DefaultPicoContainer();
        objectName = new ObjectName("pico:name=one");
    }

	public void testMBeanOnlyRegisteredOnce() throws Exception {
		ComponentAdapter adapter = JMXTestFixture.createJMXComponentAdapter(objectName);
		pico.registerComponent(adapter);
		pico.registerComponentInstance(FooBarMBeanInfo.class.getName(), JMXTestFixture.createMBeanInfo());

		// Mock the MBeanServer
		Mock mockMBeanServer = new Mock(MBeanServer.class);
		mockMBeanServer.expects(atLeastOnce()).method("isRegistered").withAnyArguments().will(onConsecutiveCalls(returnValue(false), returnValue(true)));
		mockMBeanServer.expects(once()).method("registerMBean").withAnyArguments().isVoid();

		// register Mock with pico
		pico.registerComponentInstance(MBeanServer.class, mockMBeanServer.proxy());

		// call twice make to ensure MBean NOT registered to the MBeanServer twice
		pico.getComponentInstances();
		pico.getComponentInstances();
	}

	public void testRegisterFailed() throws Exception {
		ComponentAdapter adapter = JMXTestFixture.createJMXComponentAdapter(objectName);
		pico.registerComponent(adapter);
		try {
			MBeanServerHelper.register(pico, adapter, null);
			fail("JMXRegistrationException should have been thrown");
		} catch (JMXRegistrationException ignore) {
		}
	}
}
