/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/

package org.nanocontainer.jmx;

import javax.management.DynamicMBean;
import javax.management.MBeanInfo;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;


/**
 * A DynamicMBeanProvider that constructs StandardMBean instances that follow the JMX naming conventions.
 * @author J&ouml;rg Schaible
 * @since 1.0
 */
public class NamingConventionConstructingProvider implements DynamicMBeanProvider {

    private final StandardMBeanFactory mBeanFactory;
    private final ObjectNameFactory objectNameFactory;

    /**
     * Construct a NamingConventionConstructingProvider.
     * @param factory The ObjectNameFactory used to name the created MBeans.
     * @since 1.0
     */
    public NamingConventionConstructingProvider(final ObjectNameFactory factory) {
        if (factory == null) {
            throw new NullPointerException("ObjectFactoryName is null");
        }
        objectNameFactory = factory;
        mBeanFactory = new StandardMBeanFactory();
    }

    /**
     * Create a StandardMBean from the component provided by the ComponentAdapter. The implementation will try to find the
     * {@link MBeanInfo} for the {@link javax.management.StandardMBean} by lookup in the {@link PicoContainer} using
     * <ul>
     * <li>the string representation of the component's key with an appended <em>MBeanInfo</em></li>
     * <li>if the component key is a type, then this type's name with an appended <em>MBeanInfo</em></li>
     * <li>if the component key is a type, then this type's name with an appended <em>Info</em></li>
     * <li>the component's implementation class name with an appended <em>MBeanInfo</em></li>
     * <li>a class type named like the component's implementation class with an appended <em>MBeanInfo</em></li>
     * </ul>
     * The name of the management interface must follow the naming conventions with an <em>MBean</em> appended to the MBean's
     * type. If a {@link MBeanInfo} was found, the MBean's type is used from the MBeanInfo otherwise the type is the
     * implementation class of the component.
     * <p>
     * Note: An instance of the component is only created, if a management interface is available.
     * </p>
     * @see org.nanocontainer.jmx.DynamicMBeanProvider#provide(org.picocontainer.PicoContainer,
     *           org.picocontainer.ComponentAdapter)
     */
    public JMXRegistrationInfo provide(final PicoContainer picoContainer, final ComponentAdapter componentAdapter) {
        final Object key = componentAdapter.getComponentKey();
        final String prefix = key instanceof Class ? ((Class)key).getName() : key.toString();
        final String mBeanInfoName = prefix + (prefix.endsWith("MBean") ? "Info" : "MBeanInfo");
        final Class mBeanType = componentAdapter.getComponentImplementation();

        // locate MBeanInfo
        MBeanInfo mBeanInfo = instantiateMBeanInfo(mBeanInfoName, picoContainer, mBeanType.getClassLoader());
        if (mBeanInfo == null && !mBeanInfoName.equals(mBeanType.getName() + "MBeanInfo")) {
            mBeanInfo = instantiateMBeanInfo(mBeanType.getName() + "MBeanInfo", picoContainer, mBeanType.getClassLoader());
        }

        // create MBean
        try {
            final Class management = mBeanFactory.getDefaultManagementInterface(mBeanType, mBeanInfo);
            final DynamicMBean mBean = mBeanFactory.create(
                    componentAdapter.getComponentInstance(picoContainer), management, mBeanInfo);
            final ObjectName objectName = objectNameFactory.create(key, mBean);
            if (objectName != null) {
                return new JMXRegistrationInfo(objectName, mBean);
            }
        } catch (final MalformedObjectNameException e) {
            throw new JMXRegistrationException("Cannot create ObjectName for component '" + key + "'", e);
        } catch (final ClassNotFoundException e) {
            // No management interface available
        }
        return null;
    }

    private MBeanInfo instantiateMBeanInfo(
            final String mBeanInfoName, final PicoContainer picoContainer, final ClassLoader classLoader) {
        MBeanInfo mBeanInfo = null;
        try {
            mBeanInfo = (MBeanInfo)picoContainer.getComponentInstance(mBeanInfoName);
        } catch (final ClassCastException e) {
            // wrong type, search goes on
        }
        if (mBeanInfo == null) {
            try {
                final Class mBeanInfoType = classLoader.loadClass(mBeanInfoName);
                if (MBeanInfo.class.isAssignableFrom(mBeanInfoType)) {
                    mBeanInfo = (MBeanInfo)picoContainer.getComponentInstanceOfType(mBeanInfoType);
                }
            } catch (final ClassNotFoundException e) {
                // no such class
            }
        }
        return mBeanInfo;
    }
}
