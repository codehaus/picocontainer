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

import org.picocontainer.ComponentAdapter;

import javax.management.ObjectName;
import javax.management.MBeanInfo;
import javax.management.MBeanAttributeInfo;

/**
 * @author Michael Ward
 * @version
 */
public class JMXTestFixture {

	private static MBeanAttributeInfo[] attributes;

	static {
		attributes = new MBeanAttributeInfo[] {
			new MBeanAttributeInfo("Count", int.class.toString(), "desc", true, false, false)
		};
	}

	public static ComponentAdapter createJMXComponentAdapter(ObjectName objectName) {
        return new JMXComponentAdapterFactory().createComponentAdapter(objectName, FooBar.class, null);
    }

	public static MBeanInfo createMBeanInfo() {
		return new MBeanInfo(FooBar.class.getName(), "description", attributes, null, null, null);
	}

	public static FooBarMBeanInfo createFooBarMBeanInfo() {
		return new FooBarMBeanInfo(attributes, null, null, null);
	}
}
