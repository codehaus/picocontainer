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

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.management.DynamicMBean;
import javax.management.JMException;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.modelmbean.ModelMBeanInfoSupport;
import javax.management.modelmbean.ModelMBeanOperationInfo;

import org.nanocontainer.remoting.jmx.DynamicMBeanFactory;
import org.nanocontainer.remoting.jmx.JMXRegistrationException;
import org.nanocontainer.remoting.jmx.StandardMBeanFactory;
import org.nanocontainer.script.NanoContainerMarkupException;
import org.nanocontainer.script.NodeBuilderDecorationDelegate;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;

/**
 * @author Michael Ward
 * @author J&ouml;rg Schaible
 * @version $Revision$
 */
public class JmxDecorationDelegate implements NodeBuilderDecorationDelegate {

	protected MBeanServer mBeanServer;
	protected Object currentKey; // the key the parent component is mapped too
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
		currentKey = attributes.get("key");
	}

	protected Object createJmxNode(Map attributes) throws MalformedObjectNameException {
        MBeanServer server = getMBeanServer();
        DynamicMBeanFactory factory = getDynamicMBeanFactory();
        String description = (String)attributes.get("description");
		ObjectName objectName = new ObjectName((String) attributes.remove("key"));
		List operations = (List) attributes.remove("operations");
		Class componentImplementation = getComponentImplementation(currentClass);
        Class management = (Class)attributes.get("management");

		// Build MBeanInfo
		List methods = getMatchingMethods(operations, componentImplementation);

		// todo need to handle attributes and constructors and notifications
		ModelMBeanOperationInfo[] mBeanOperationInfos = buildMBeanOperationInfoArray(methods);
		MBeanInfo mBeanInfo = new ModelMBeanInfoSupport(componentImplementation.getName(), description, null, null, mBeanOperationInfos, null);

        // Register MBean
        Object componentInstance = picoContainer.getComponentInstance(currentKey);
        DynamicMBean mBean = null;
        if (management == null && !(currentKey instanceof Class)) {
            mBean = factory.create(componentInstance, null, mBeanInfo);
        } else {
            mBean = factory.create(componentInstance, management != null ? management : (Class)currentKey, mBeanInfo);
        }
        try {
            server.registerMBean(mBean, objectName);
        } catch(JMException e) {
            throw new JMXRegistrationException("Failed to register MBean '" + objectName + "'", e);
        }
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

	protected ModelMBeanOperationInfo[] buildMBeanOperationInfoArray(List methods) {
		ModelMBeanOperationInfo[] mBeanOperationInfos = new ModelMBeanOperationInfo[methods.size()];

		for (int i = 0; i < methods.size(); i++) {
			Method method = (Method) methods.get(i);
			mBeanOperationInfos[i] = new ModelMBeanOperationInfo("No description provided", method);
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

	protected MBeanServer getMBeanServer() {
		MBeanServer server = (MBeanServer) picoContainer.getComponentInstanceOfType(MBeanServer.class);
        if (server == null) {
            throw new JMXRegistrationException("A MBeanServer must be registered within the PicoContainer");
        }
        return server;
	}

    protected DynamicMBeanFactory getDynamicMBeanFactory() {
        DynamicMBeanFactory factory = (DynamicMBeanFactory) picoContainer.getComponentInstanceOfType(DynamicMBeanFactory.class);
        if (factory == null) {
            factory = new StandardMBeanFactory();
        }
        return factory;
    }

}
