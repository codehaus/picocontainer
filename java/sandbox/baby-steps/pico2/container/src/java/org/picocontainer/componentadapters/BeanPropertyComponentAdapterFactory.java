/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.componentadapters;

import java.util.HashMap;
import java.util.Map;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.ComponentCharacteristic;
import org.picocontainer.componentadapters.DecoratingComponentAdapterFactory;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.NotConcreteRegistrationException;

/**
 * A {@link org.picocontainer.defaults.ComponentAdapterFactory} that creates
 * {@link BeanPropertyComponentAdapter} instances.
 * 
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 * @since 1.0
 */
public class BeanPropertyComponentAdapterFactory extends DecoratingComponentAdapterFactory {
    private Map<Object, BeanPropertyComponentAdapter> adapterCache = new HashMap<Object, BeanPropertyComponentAdapter>();

    /**
     * Construct a BeanPropertyComponentAdapterFactory. 
     * 
     * @param delegate the wrapped factory.
     */
    public BeanPropertyComponentAdapterFactory(ComponentAdapterFactory delegate) {
        super(delegate);
    }

    /**
     * {@inheritDoc}
     */
    public ComponentAdapter createComponentAdapter(ComponentCharacteristic registerationCharacteristic, Object componentKey, Class componentImplementation, Parameter... parameters) throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        ComponentAdapter decoratedAdapter = super.createComponentAdapter(registerationCharacteristic, componentKey, componentImplementation, parameters);
        BeanPropertyComponentAdapter propertyAdapter = new BeanPropertyComponentAdapter(decoratedAdapter);
        adapterCache.put(componentKey, propertyAdapter);
        return propertyAdapter;
    }

}
