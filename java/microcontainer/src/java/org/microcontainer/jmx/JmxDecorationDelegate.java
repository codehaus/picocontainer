/*****************************************************************************
 * Copyright (C) MicroContainer Organization. All rights reserved.           *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Michael Ward                                             *
 *****************************************************************************/

package org.microcontainer.jmx;

import org.nanocontainer.script.NanoContainerBuilderDecorationDelegate;
import org.nanocontainer.script.NanoContainerMarkupException;
import org.nanocontainer.jmx.JMXVisitor;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.MutablePicoContainer;

import javax.management.MBeanOperationInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.MalformedObjectNameException;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;
import java.lang.reflect.Method;

/**
 * @author Michael Ward
 * @version $Revision$
 */
public class JmxDecorationDelegate implements NanoContainerBuilderDecorationDelegate {

	protected MBeanServer mBeanServer;
	protected String currentKey; // the key the parent component is mapped too
	protected Object currentClass; // the implementation class registered to pico
	protected MutablePicoContainer picoContainer;

	public ComponentAdapterFactory decorate(ComponentAdapterFactory componentAdapterFactory, Map attributes) {
		return componentAdapterFactory;
	}

	public MutablePicoContainer decorate(MutablePicoContainer picoContainer) {
		this.picoContainer = picoContainer;
		return picoContainer;
	}

	public Object createNode(Object name, Map attributes, Object parentElement) {
		if (name.equals("jmx")) {
			try {
				return createJmxNode(attributes);
			} catch (MalformedObjectNameException e) {
				new NanoContainerMarkupException(e);
			}
		}

		throw new NanoContainerMarkupException("can't handle " + name);
	}

	public void rememberComponentKey(Map attributes) {
        currentClass = attributes.get("class");

		Object key = attributes.get("key");

		if (key instanceof Class) {
			currentKey = ((Class) key).getName();
		} else {
			currentKey = (String) key;
		}

	}

	protected Object createJmxNode(Map attributes) throws MalformedObjectNameException {
		String description = (String)attributes.get("description");
		JMXVisitor jmxVisitor = new JMXVisitor();
		ObjectName objectName = new ObjectName((String) attributes.remove("key"));
		List operations = (List) attributes.remove("operations");
		Class componentImplementation = getComponentImplementation(currentClass);

		// Build MBeanInfo
		List methods = getMatchingMethods(operations, componentImplementation);

		// todo need to handle attributes and constructors and notifications  
		MBeanOperationInfo[] mBeanOperationInfos = buildMBeanOperationInfoArray(methods);
		MBeanInfo mBeanInfo = new MBeanInfo(currentKey, description, null, null, mBeanOperationInfos, null);

		jmxVisitor.register(objectName, mBeanInfo);
		picoContainer.accept(jmxVisitor);
		return picoContainer;
	}

	/**
	 * get the class regardless of whether it was defined as a class of the Class name from the groovy script
	 */
	protected Class getComponentImplementation(Object clazz) {
		if (clazz instanceof Class) {
			return (Class) clazz;
		} else {
			try {
				return Class.forName((String) clazz);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
	}

	protected MBeanOperationInfo[] buildMBeanOperationInfoArray(List methods) {
		MBeanOperationInfo[] mBeanOperationInfos = new MBeanOperationInfo[methods.size()];

		for (int i = 0; i < methods.size(); i++) {
			Method method = (Method) methods.get(i);
			mBeanOperationInfos[i] = new MBeanOperationInfo("No description provided", method);
		}

		return mBeanOperationInfos;
	}

	/**
	 * Find all methods with the matching name
	 */
	protected List getMatchingMethods(List operations, Class clazz) {
		Method[] allMethods = clazz.getMethods();
		List matching = new LinkedList();

		for (int i = 0; i < allMethods.length; i++) {
			Method method = allMethods[i];

			// iterate the operations to find a match
			for (int j = 0; j < operations.size(); j++) {
				String methodName = (String) operations.get(j);

				if (method.getName().equals(methodName)) {
					matching.add(method);
				}
			}
		}

		return matching;
	}

	protected MBeanServer getMBeanServer(MutablePicoContainer picoContainer) {
		return (MBeanServer) picoContainer.getComponentInstance(MBeanServer.class);
	}

}
