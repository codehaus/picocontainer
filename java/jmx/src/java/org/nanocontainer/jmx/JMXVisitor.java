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

import java.util.HashMap;
import java.util.Map;

import javax.management.DynamicMBean;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.defaults.AbstractPicoVisitor;

/**
 * @author Michael Ward
 * @author J&ouml,rg Schaible
 * @version $Revision$
 */
public class JMXVisitor extends AbstractPicoVisitor {
	private Map map = new HashMap(20);
	private PicoContainer picoContainer;
    private DynamicMBeanFactory factory;

	/**
	 * Register for a DynamicMBean which maps an MBeanInfo to an ObjectName
	 */
	public void register(ObjectName objectName, MBeanInfo mBeanInfo) {
		MBeanInfoWrapper wrapper = new MBeanInfoWrapper(mBeanInfo, objectName);
		map.put(mBeanInfo.getClassName(), wrapper);
	}

	/**
	 * Register for StandardMBean's which maps a Classes name to an ObjectName
	 */
	public void register(ObjectName objectName, Class management) {
		map.put(management.getName(), objectName);
	}

	public void visitContainer(PicoContainer picoContainer) {
		this.picoContainer = picoContainer;
	}

	public void visitComponentAdapter(ComponentAdapter componentAdapter) {
		String className = getComponentKeyAsString(componentAdapter);

		if (map.containsKey(className)) {
			Object obj = map.get(className);

			if (obj instanceof MBeanInfoWrapper) {
				handleMBeanInfo(componentAdapter, (MBeanInfoWrapper) obj);
			} else { // StandardMBean
				handleStandardMBean(componentAdapter, (ObjectName) obj);
			}
		}
	}

	public void visitParameter(Parameter parameter) {
		// does nothing
	}

	protected String getComponentKeyAsString(ComponentAdapter componentAdapter) {
		Object componentKey = componentAdapter.getComponentKey();

		if (componentKey instanceof Class) {
			return ((Class) componentKey).getName();
		}

		return componentKey.toString();
	}

	/**
	 * Create a DynamicMBean from the MBeanInfoWrapper being passed in.
	 */
	protected void handleMBeanInfo(ComponentAdapter componentAdapter, MBeanInfoWrapper mBeanInfoWrapper) {
		MBeanInfo mBeanInfo = mBeanInfoWrapper.getmBeanInfo();
		ObjectName objectName = mBeanInfoWrapper.getObjectName();
		Object instance = componentAdapter.getComponentInstance(picoContainer);
		DynamicMBean mbean = getDynamicMBeanFactory(picoContainer).create(instance, mBeanInfo);
		registerWithMBeanServer(mbean, objectName);
	}

	/**
     * Retrieve the DynamicMBeanFactory to use. The implementation will lookup such an instance in the picoContainer. If none is
     * found, a default {@link DynamicMBeanFactory} is used, that may not be capable of handling all cases.
     * @param picoContainer used to lookup the DynamicMBeanFactory.
     * @return the DynamicMBeanFactory to use.
     */
	protected DynamicMBeanFactory getDynamicMBeanFactory(PicoContainer picoContainer) {
        if (factory == null) {
            factory = (DynamicMBeanFactory)picoContainer.getComponentInstanceOfType(DynamicMBeanFactory.class);
            if (factory == null) {
                factory = createDynamicMBeanFactory();
            }
        }
        return factory;
	}

    /**
     * Instantiate a new StandardMBeanFactory as default DynamicMBeanFactory.
     * @return the new instance.
     */
    protected DynamicMBeanFactory createDynamicMBeanFactory() {
        return new StandardMBeanFactory();
    }

	/**
	 * Create a StandardMBean and register it to the MBeanServer.
	 */
	protected void handleStandardMBean(ComponentAdapter componentAdapter, ObjectName objectName) {
        Object key = componentAdapter.getComponentKey();
        if (key instanceof Class) {
            Object instance = componentAdapter.getComponentInstance(picoContainer);
            DynamicMBean mbean = getDynamicMBeanFactory(picoContainer).create(instance, (Class)key);
            registerWithMBeanServer(mbean, objectName);
        }
    }

	/**
	 * Register a MBean in the MBeanServer. The {@link MBeanServer} must be available fro mthe visited {@link PicoContainer}.
	 * @param dynamicMBean the MBean to register.
	 * @param objectName the {@link ObjectName} of the MBean registered the MBeanServer.
	 */
	protected void registerWithMBeanServer(DynamicMBean dynamicMBean, ObjectName objectName) {
		MBeanServer mBeanServer = (MBeanServer) picoContainer.getComponentInstanceOfType(MBeanServer.class);

		if (mBeanServer == null) {
			throw new JMXRegistrationException("A MBeanServer instance MUST be registered with the container");
		}
		// Can only register an ObjectName to the MBeanServer once
		if (mBeanServer.isRegistered(objectName) == false) {
			try {
				mBeanServer.registerMBean(dynamicMBean, objectName);
			} catch (Exception e) {
				throw new PicoRegistrationException("Unable to register MBean to MBean Server", e);
			}
		}
	}

	/**
	 * Simple wrapper to tie a MBeanInfo to an ObjectName
	 */
	private class MBeanInfoWrapper {
		private MBeanInfo mBeanInfo;
		private ObjectName objectName;

		public MBeanInfoWrapper(MBeanInfo mBeanInfo, ObjectName objectName) {
			this.mBeanInfo = mBeanInfo;
			this.objectName = objectName;
		}

		public MBeanInfo getmBeanInfo() {
			return mBeanInfo;
		}

		public ObjectName getObjectName() {
			return objectName;
		}
	}

}
