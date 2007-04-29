/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/

package org.nanocontainer.remoting.jmx;

import javax.management.MBeanServer;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.componentadapters.DecoratingComponentAdapterFactory;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.NotConcreteRegistrationException;


/**
 * {@link ComponentAdapterFactory} that instantiates {@link JMXExposingComponentAdapter} instances.
 * @author J&ouml;rg Schaible
 * @since 1.0
 */
public class JMXExposingComponentAdapterFactory extends DecoratingComponentAdapterFactory {

    private final MBeanServer mBeanServer;
    private final DynamicMBeanProvider[] providers;

    /**
     * Construct a JMXExposingComponentAdapterFactory.
     * @param delegate The delegated {@link ComponentAdapterFactory}.
     * @param mBeanServer The {@link MBeanServer} used for registering the MBean.
     * @param providers An array with providers for converting the component instance into a
     *            {@link javax.management.DynamicMBean}.
     * @throws NullPointerException Thrown if the {@link MBeanServer} or the array with the {@link DynamicMBeanProvider}
     *             instances is null.
     * @since 1.0
     */
    public JMXExposingComponentAdapterFactory(
            final ComponentAdapterFactory delegate, final MBeanServer mBeanServer,
            final DynamicMBeanProvider[] providers) throws NullPointerException {
        super(delegate);
        if (mBeanServer == null || providers == null) {
            throw new NullPointerException();
        }
        this.mBeanServer = mBeanServer;
        this.providers = providers;
    }

    /**
     * Construct a JMXExposingComponentAdapterFactory. This instance uses a {@link DynamicMBeanComponentProvider} as
     * default to register any component instance in the {@link MBeanServer}, that is already a
     * {@link javax.management.DynamicMBean}.
     * @param delegate The delegated {@link ComponentAdapterFactory}.
     * @param mBeanServer The {@link MBeanServer} used for registering the MBean.
     * @throws NullPointerException Thrown if the {@link MBeanServer} or the array with the {@link DynamicMBeanProvider}
     *             instances is null.
     * @since 1.0
     */
    public JMXExposingComponentAdapterFactory(final ComponentAdapterFactory delegate, final MBeanServer mBeanServer)
            throws NullPointerException {
        this(delegate, mBeanServer, new DynamicMBeanProvider[]{new DynamicMBeanComponentProvider()});
    }

    /**
     * Retrieve a {@link ComponentAdapter}. Wrap the instance retrieved by the delegate with an instance of a
     * {@link JMXExposingComponentAdapter}.
     * @see org.picocontainer.componentadapters.DecoratingComponentAdapterFactory#createComponentAdapter(java.lang.Object,
     *      java.lang.Class, org.picocontainer.Parameter[])
     */
    public ComponentAdapter createComponentAdapter(
            Object componentKey, Class componentImplementation, Parameter[] parameters)
            throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        final ComponentAdapter componentAdapter = super.createComponentAdapter(
                componentKey, componentImplementation, parameters);
        return new JMXExposingComponentAdapter(componentAdapter, mBeanServer, providers);
    }

}
