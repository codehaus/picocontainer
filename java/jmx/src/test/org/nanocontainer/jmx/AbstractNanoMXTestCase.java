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

import junit.framework.TestCase;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

/**
 * Abstract base class with some helper methods for testing NanoMX
 * 
 * @author James Strachan
 * @version $Revision$
 */
public abstract class AbstractNanoMXTestCase extends TestCase {

    /**
     * Helper method to check there exists an mbean at the given name
     * @param name
     */
    protected void assertExistsInJMX(MBeanServer server, String name) throws InstanceNotFoundException, MalformedObjectNameException {
        assertNotNull("MBeanServer is not null", server);

        ObjectInstance instance = server.getObjectInstance(new ObjectName(name));

        assertNotNull("Found one in MBeanServer", instance);

        assertEquals("ObjectInstance has correct class name", NanoMBean.class.getName(), instance.getClassName());
    }
}
