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
    private final MBeanInfoProvider[] mBeanInfoProviders;

    private static MBeanInfoProvider[] defaultMBeanInfoProvider = new MBeanInfoProvider[]{
            new ComponentKeyConventionMBeanInfoProvider(), new ComponentTypeConventionMBeanInfoProvider()};

    /**
     * Construct a NamingConventionConstructingProvider that uses default MBeanInfoProvider instances. Following
     * MBeaninfoProviders are registered with this constructor:
     * <ul>
     * <li>{@link ComponentKeyConventionMBeanInfoProvider}</li>
     * <li>{@link ComponentTypeConventionMBeanInfoProvider}</li>
     * </ul>
     * @param factory The ObjectNameFactory used to name the created MBeans.
     * @since 1.0
     */
    public NamingConventionConstructingProvider(final ObjectNameFactory factory) {
        this(factory, defaultMBeanInfoProvider);
    }

    /**
     * Construct a NamingConventionConstructingProvider.
     * @param factory The ObjectNameFactory used to name the created MBeans.
     * @param mBeanInfoProviders The providers for a matching MBeanInfo for a certain component.
     * @since 1.0
     */
    public NamingConventionConstructingProvider(final ObjectNameFactory factory, final MBeanInfoProvider[] mBeanInfoProviders) {
        if (factory == null) {
            throw new NullPointerException("ObjectFactoryName is null");
        }
        objectNameFactory = factory;
        mBeanFactory = new StandardMBeanFactory();
        this.mBeanInfoProviders = mBeanInfoProviders != null ? mBeanInfoProviders : new MBeanInfoProvider[0];
    }

    /**
     * Create a StandardMBean from the component provided by the ComponentAdapter. The name of the management interface must
     * follow the naming conventions with an <em>MBean</em> appended to the MBean's type. The implementation will use the
     * registered MBeanInfoProvider instances to provide a {@link MBeanInfo} for the component's MBean. If a {@link MBeanInfo}
     * was found, the MBean's type is used from the MBeanInfo otherwise the type is the implementation class of the component.
     * <p>
     * Note: An instance of the component is only created, if a management interface is available.
     * </p>
     * @see org.nanocontainer.jmx.DynamicMBeanProvider#provide(org.picocontainer.PicoContainer,
     *           org.picocontainer.ComponentAdapter)
     */
    public JMXRegistrationInfo provide(final PicoContainer picoContainer, final ComponentAdapter componentAdapter) {

        // locate MBeanInfo
        MBeanInfo mBeanInfo = null;
        for (int i = 0; i < mBeanInfoProviders.length && mBeanInfo == null; ++i) {
            mBeanInfo = mBeanInfoProviders[i].provide(picoContainer, componentAdapter);
        }

        // create MBean
        try {
            // thows CNF if not successful
            final Class management = mBeanFactory.getDefaultManagementInterface(
                    componentAdapter.getComponentImplementation(), mBeanInfo);
            final DynamicMBean mBean = mBeanFactory.create(
                    componentAdapter.getComponentInstance(picoContainer), management, mBeanInfo);
            final ObjectName objectName = objectNameFactory.create(componentAdapter.getComponentKey(), mBean);
            if (objectName != null) {
                return new JMXRegistrationInfo(objectName, mBean);
            }
        } catch (final MalformedObjectNameException e) {
            throw new JMXRegistrationException("Cannot create ObjectName for component '"
                    + componentAdapter.getComponentKey()
                    + "'", e);
        } catch (final ClassNotFoundException e) {
            // No management interface available
        }
        return null;
    }
}
