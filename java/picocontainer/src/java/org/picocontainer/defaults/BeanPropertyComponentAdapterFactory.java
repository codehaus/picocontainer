/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.defaults;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class BeanPropertyComponentAdapterFactory extends DecoratingComponentAdapterFactory {
    private Map adapterCache = new HashMap();

    public BeanPropertyComponentAdapterFactory(ComponentAdapterFactory delegate) {
        super(delegate);
    }

    public ComponentAdapter createComponentAdapter(Object componentKey, Class componentImplementation, Parameter[] parameters) throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        ComponentAdapter decoratedAdapter = super.createComponentAdapter(componentKey, componentImplementation, parameters);
        BeanPropertyComponentAdapter propertyAdapter = new BeanPropertyComponentAdapter(decoratedAdapter);
        adapterCache.put(componentKey, propertyAdapter);
        return propertyAdapter;
    }

    public BeanPropertyComponentAdapter getComponentAdapter(Object key) {
        return (BeanPropertyComponentAdapter) adapterCache.get(key);
    }
}
