/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.adapters;

import java.util.HashMap;
import java.util.Map;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.ComponentCharacteristic;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.adapters.AbstractBehaviorFactory;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.LifecycleStrategy;

/**
 * A {@link org.picocontainer.ComponentFactory} that creates
 * {@link BeanPropertyComponentAdapter} instances.
 * 
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 * @since 1.0
 */
public final class BeanPropertyComponentAdapterFactory extends AbstractBehaviorFactory {
    // var never used ? needed ?
    private final Map<Object, BeanPropertyComponentAdapter> adapterCache = new HashMap<Object, BeanPropertyComponentAdapter>();

    /**
     * {@inheritDoc}
     */
    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, ComponentCharacteristic componentCharacteristic, Object componentKey, Class componentImplementation, Parameter... parameters) throws PicoIntrospectionException, NotConcreteRegistrationException {
        ComponentAdapter decoratedAdapter = super.createComponentAdapter(componentMonitor, lifecycleStrategy, componentCharacteristic, componentKey, componentImplementation, parameters);
        BeanPropertyComponentAdapter propertyAdapter = new BeanPropertyComponentAdapter(decoratedAdapter);
        adapterCache.put(componentKey, propertyAdapter);
        return propertyAdapter;
    }

}
