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

import java.util.HashSet;
import java.util.Set;

import javax.management.DynamicMBean;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.AbstractPicoVisitor;


/**
 * @author Michael Ward
 * @author J&ouml;rg Schaible
 * @version $Revision$
 * @since 1.0
 */
public class JMXVisitor extends AbstractPicoVisitor {
    private final DynamicMBeanProvider[] mBeanProviders;
    private final MBeanServer mBeanServer;
    private final Set visited;
    private PicoContainer picoContainer;

    /**
     * Construct a JMXVisitor.
     * @param server The {@link MBeanServer}to use for registering the MBeans.
     * @param providers The providers to deliver the DynamicMBeans.
     */
    public JMXVisitor(final MBeanServer server, final DynamicMBeanProvider[] providers) {
        if (server == null) {
            throw new NullPointerException("MBeanServer may not be null");
        }
        if (providers == null) {
            throw new NullPointerException("DynamicMBeanProvider[] may not be null");
        }
        if (providers.length == 0) {
            throw new IllegalArgumentException("DynamicMBeanProvider[] may not be empty");
        }
        mBeanServer = server;
        mBeanProviders = providers;
        visited = new HashSet();
    }

    /**
     * Provides the PicoContainer, that can resolve the components to register as MBean.
     * @see org.picocontainer.PicoVisitor#visitContainer(org.picocontainer.PicoContainer)
     */
    public void visitContainer(final PicoContainer pico) {
        picoContainer = pico;
        visited.clear();
    }

    /**
     * Register the component as MBean. The implementation uses the known DynamicMBeanProvider instances to get the MBean from
     * the component.
     * @see org.picocontainer.PicoVisitor#visitComponentAdapter(org.picocontainer.ComponentAdapter)
     */
    public void visitComponentAdapter(final ComponentAdapter componentAdapter) {
        if (picoContainer == null) {
            throw new JMXRegistrationException("Cannot start JMXVisitor traversal with a ComponentAdapter");
        }
        if (!visited.contains(componentAdapter.getComponentKey())) {
            visited.add(componentAdapter.getComponentKey());
            for (int i = 0; i < mBeanProviders.length; i++) {
                final DynamicMBeanProvider provider = mBeanProviders[i];
                final JMXRegistrationInfo info = provider.provide(picoContainer, componentAdapter);
                if (info != null) {
                    register(info.getMBean(), info.getObjectName());
                    break;
                }
            }
        }
    }

    /**
     * Does nothing.
     * @see org.picocontainer.PicoVisitor#visitParameter(org.picocontainer.Parameter)
     */
    public void visitParameter(final Parameter parameter) {
        // does nothing
    }

    /**
     * Register a MBean in the MBeanServer.
     * @param dynamicMBean the {@link DynamicMBean}to register.
     * @param objectName the {@link ObjectName}of the MBean registered the {@link MBeanServer}.
     * @throws JMXRegistrationException Thrown if MBean cannot be registered.
     */
    protected void register(final DynamicMBean dynamicMBean, final ObjectName objectName) throws JMXRegistrationException {
        try {
            mBeanServer.registerMBean(dynamicMBean, objectName);
        } catch (final JMException e) {
            throw new JMXRegistrationException("Unable to register MBean to MBean Server", e);
        }
    }
}
