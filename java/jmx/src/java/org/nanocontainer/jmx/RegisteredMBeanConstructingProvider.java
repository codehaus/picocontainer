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
import javax.management.ObjectName;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;


/**
 * A DynamicMBeanProvider, that creates DynamicMBeans for registered Pico components on the fly.
 * @author Michael Ward
 * @author J&ouml;rg Schaible
 * @since 1.0
 */
public class RegisteredMBeanConstructingProvider implements DynamicMBeanProvider {

    private final DynamicMBeanFactory factory;
    private final Map registry;

    /**
     * Construct a RegisteredMBeanConstructingProvider with a {@link StandardMBeanFactory} as default.
     */
    public RegisteredMBeanConstructingProvider() {
        this(new StandardMBeanFactory());
    }

    /**
     * Construct a RegisteredMBeanConstructingProvider, that uses a specific {@link DynamicMBeanFactory}.
     * @param factory
     */
    public RegisteredMBeanConstructingProvider(final DynamicMBeanFactory factory) {
        this.factory = factory;
        this.registry = new HashMap();
    }

    /**
     * Provide a DynamicMBean for the given Pico component. The implementation will lookup the component's key in the internal
     * registry. Only components that were registered with additional information will be considered and a {@link DynamicMBean}
     * will be created for them using the {@link DynamicMBeanFactory}. If the component key is of type class, it is used as
     * management interface.
     * @see org.nanocontainer.jmx.DynamicMBeanProvider#provide(PicoContainer, ComponentAdapter)
     */
    public JMXRegistrationInfo provide(final PicoContainer picoContainer, final ComponentAdapter componentAdapter) {
        final Object key = componentAdapter.getComponentKey();
        final MBeanInfoWrapper wrapper = (MBeanInfoWrapper)registry.get(key);
        if (wrapper != null) {
            final DynamicMBean mBean;
            final Object instance = componentAdapter.getComponentInstance(picoContainer);
            final Class management = wrapper.getManagementInterface() != null
                                                                             ? wrapper.getManagementInterface()
                                                                             : key instanceof Class ? (Class)key : instance
                                                                                     .getClass();
            if (wrapper.getMBeanInfo() != null) {
                mBean = factory.create(instance, management, wrapper.getMBeanInfo());
            } else {
                mBean = factory.create(instance, management, wrapper.getDescription());
            }
            return new JMXRegistrationInfo(wrapper.getObjectName(), mBean);
        }
        return null;
    }

    /**
     * Register a specific Pico component by key with an MBeanInfo and an ObjectName.
     * @param componentKey The key of the Pico component.
     * @param objectName The {@link ObjectName} of the MBean.
     * @param management The management interface.
     * @param mBeanInfo The {@link MBeanInfo} of the MBean.
     */
    public void register(final Object componentKey, final ObjectName objectName, final Class management, final MBeanInfo mBeanInfo) {
        registry.put(componentKey, new MBeanInfoWrapper(mBeanInfo, objectName, management, null));
    }

    /**
     * Register a specific Pico component by key with an MBeanInfo and an ObjectName.
     * @param componentKey The key of the Pico component.
     * @param objectName The {@link ObjectName} of the MBean.
     * @param mBeanInfo The {@link MBeanInfo} of the MBean.
     */
    public void register(final Object componentKey, final ObjectName objectName, final MBeanInfo mBeanInfo) {
        register(componentKey, objectName, null, mBeanInfo);
    }

    /**
     * Register a specific Pico component with an MBeanInfo and an ObjectName. The implementation class of the
     * {@link DynamicMBean} must be the key of the Pico component.
     * @param objectName The {@link ObjectName} of the MBean.
     * @param mBeanInfo The {@link MBeanInfo} of the MBean.
     */
    public void register(final ObjectName objectName, final MBeanInfo mBeanInfo) {
        try {
            register(getClass().getClassLoader().loadClass(mBeanInfo.getClassName()), objectName, mBeanInfo);
        } catch (final ClassNotFoundException e) {
            throw new JMXRegistrationException("Cannot access class " + mBeanInfo.getClassName() + " of MBean", e);
        }
    }

    /**
     * Register a specific Pico component by key with an ObjectName.
     * @param componentKey The key of the Pico component.
     * @param objectName The {@link ObjectName} of the MBean.
     */
    public void register(final Object componentKey, final ObjectName objectName) {
        register(componentKey, objectName, (String)null);
    }

    /**
     * Register a specific Pico component by key with an ObjectName and a description for the MBean.
     * @param componentKey The key of the Pico component.
     * @param objectName The {@link ObjectName} of the MBean.
     * @param description The derscription of the MBean.
     */
    public void register(final Object componentKey, final ObjectName objectName, final String description) {
        registry.put(componentKey, new MBeanInfoWrapper(null, objectName, null, description));
    }

    /**
     * Simple wrapper to tie a MBeanInfo to an ObjectName
     */
    private static class MBeanInfoWrapper {
        private final MBeanInfo mBeanInfo;
        private final ObjectName objectName;
        private final String description;
        private final Class managementInterface;

        MBeanInfoWrapper(final MBeanInfo mBeanInfo, final ObjectName objectName, final Class management, final String description) {
            this.mBeanInfo = mBeanInfo;
            this.objectName = objectName;
            this.managementInterface = management;
            this.description = description;
        }

        MBeanInfo getMBeanInfo() {
            return mBeanInfo;
        }

        ObjectName getObjectName() {
            return objectName;
        }

        Class getManagementInterface() {
            return managementInterface;
        }

        String getDescription() {
            return description;
        }
    }

}
