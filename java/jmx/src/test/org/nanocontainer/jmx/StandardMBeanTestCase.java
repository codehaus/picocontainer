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

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

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
		ComponentAdapter componentAdapter = new StandardMBeanComponentAdapter(objectName, new SampleMBean(), SampleInterface.class);
		pico.registerComponent(componentAdapter);

		assertFalse(mbeanServer.isRegistered(objectName));
		pico.getComponentInstances();
		assertTrue(mbeanServer.isRegistered(objectName));
        Object object = mbeanServer.getAttribute(objectName, "Count");
		assertEquals(new Integer(1), object);
	}

}
