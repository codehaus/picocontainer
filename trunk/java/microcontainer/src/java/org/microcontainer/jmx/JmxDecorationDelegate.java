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
import org.nanocontainer.jmx.MBeanComponentAdapterFactory;
import org.nanocontainer.NanoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.ComponentAdapter;
import org.microcontainer.impl.AliasComponentAdapter;

import javax.management.MBeanOperationInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
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

	public ComponentAdapterFactory decorate(ComponentAdapterFactory componentAdapterFactory, Map attributes) {
		return componentAdapterFactory;
	}

	public MutablePicoContainer decorate(MutablePicoContainer picoContainer) {
		return picoContainer;
	}

	public Object createNode(Object name, Map attributes, Object parentElement) {
        if(name.equals("jmx")) {
			return createJmxNode(attributes, (NanoContainer)parentElement);
		}
		else if(name.equals("component")) {
            return createComponent(attributes, (JmxDefinition)parentElement);
		}

		throw new NanoContainerMarkupException("can't handle " + name );
	}

	public void rememberComponentKey(Map attributes) {
	}

	/**
	 * Create a component which is a child of the jmx node
	 */
	protected Object createComponent(Map attributes, JmxDefinition jmxDefinition) {
		MutablePicoContainer picoContainer = jmxDefinition.getPicoContainer();
		Object key = attributes.remove("key"); // interface
		Class componentImplementation = getComponentImplementation(attributes.remove("class"));

		// Now MBean stuff...
		List methods = getMatchingMethods(jmxDefinition.getOperations(), componentImplementation);
		MBeanOperationInfo[] mBeanOperationInfos = buildMBeanOperationInfoArray(methods);

		// register the MBeanInfo
		String className = componentImplementation.getName();
		MBeanInfo mBeanInfo = new MBeanInfo(className, "description", null, null, mBeanOperationInfos, null);
		picoContainer.registerComponentInstance(className.concat("MBeanInfo"), mBeanInfo);

		// register the MBean to the objectName (as a key)
		ComponentAdapter ca = new MBeanComponentAdapterFactory().createComponentAdapter(jmxDefinition.getKey(), componentImplementation, null);
		picoContainer.registerComponent(ca);

		// also register the implementation as a key to pico
		picoContainer.registerComponent(new AliasComponentAdapter(key, ca));

		return picoContainer;
	}

	/**
	 * get the class regardless of whether it was defined as a class of the Class name from the groovy script
	 */
	protected Class getComponentImplementation(Object clazz) {
		if(clazz instanceof Class) {
			return (Class)clazz;
		}
		else {
			try {
				return Class.forName((String)clazz);
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

				if(method.getName().equals(methodName)) {
					matching.add(method);
				}
			}
		}

		return matching;
	}

	protected MBeanServer getMBeanServer(MutablePicoContainer picoContainer) {
		return (MBeanServer)picoContainer.getComponentInstance(MBeanServer.class);
	}

	protected Object createJmxNode(Map attributes, NanoContainer nanoContainer) {
		String key = (String)attributes.remove("key");
		List operations = (List)attributes.remove("operations");

		// build the bean
		JmxDefinition jmxDefinition = new JmxDefinition(nanoContainer.getPico());
		jmxDefinition.setKey(key);
		jmxDefinition.setOperations(operations);

		return jmxDefinition;
	}
}
