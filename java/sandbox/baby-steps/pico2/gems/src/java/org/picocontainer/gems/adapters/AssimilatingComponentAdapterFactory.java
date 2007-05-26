/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaibe                                            *
 *****************************************************************************/

package org.picocontainer.gems.adapters;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.ComponentCharacteristic;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.adapters.AbstractDecoratingComponentAdapterFactory;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.defaults.LifecycleStrategy;


/**
 * Factory for the AssimilatingComponentAdapter. This factory will create {@link AssimilatingComponentAdapter} instances for all
 * {@link ComponentAdapter} instances created by the delegate. This will assimilate every addComponent for a specific type.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.2
 */
public class AssimilatingComponentAdapterFactory extends AbstractDecoratingComponentAdapterFactory {

    private final ProxyFactory proxyFactory;
    private final Class assimilationType;

    /**
     * Construct an AssimilatingComponentAdapterFactory. The instance will use the {@link StandardProxyFactory} using the JDK
     * implementation.
     * 
     * @param type The assimilated type.
     */
    public AssimilatingComponentAdapterFactory(final Class type) {
        this(type, new StandardProxyFactory());
    }

    /**
     * Construct an AssimilatingComponentAdapterFactory using a special {@link ProxyFactory}.
     * 
     * @param type The assimilated type.
     * @param proxyFactory The proxy factory to use.
     */
    public AssimilatingComponentAdapterFactory(final Class type, final ProxyFactory proxyFactory) {
        this.assimilationType = type;
        this.proxyFactory = proxyFactory;
    }

    /**
     * Create a {@link AssimilatingComponentAdapter}. This addAdapter will wrap the returned {@link ComponentAdapter} of the
     * deleated {@link ComponentAdapterFactory}.
     * 
     * @see org.picocontainer.defaults.ComponentAdapterFactory#createComponentAdapter(org.picocontainer.ComponentMonitor,org.picocontainer.defaults.LifecycleStrategy,org.picocontainer.ComponentCharacteristic,Object,Class,org.picocontainer.Parameter...)
     */
    public ComponentAdapter createComponentAdapter(
            ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, ComponentCharacteristic registerationCharacteristic, final Object componentKey, final Class componentImplementation, final Parameter... parameters)
            throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        return new AssimilatingComponentAdapter(assimilationType, super.createComponentAdapter(
                componentMonitor, lifecycleStrategy, null, componentKey, componentImplementation, parameters), proxyFactory);
    }
}
