package org.nanocontainer.jmx;

import javax.management.*;

/**
 * 
 */
public class FooBarMBeanInfo extends MBeanInfo {
	public FooBarMBeanInfo(MBeanAttributeInfo[] attributes, MBeanConstructorInfo[] constructors, MBeanOperationInfo[] operations, MBeanNotificationInfo[] notifications) {
		super(FooBar.class.getName(), "description", attributes, constructors, operations, notifications);
	}

	public FooBarMBeanInfo(String className, String description, MBeanAttributeInfo[] attributes, MBeanConstructorInfo[] constructors, MBeanOperationInfo[] operations, MBeanNotificationInfo[] notifications) {
		super(className, description, attributes, constructors, operations, notifications);
	}
}
