/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/

package org.picocontainer.internals;

import org.picocontainer.defaults.AmbiguousComponentResolutionException;
import org.picocontainer.PicoInitializationException;

import java.util.Collection;
import java.util.List;

public interface ComponentRegistry {

    void registerComponent(ComponentAdapter compSpec);

    List getComponentAdapters();

    List getOrderedComponents();

    void addOrderedComponentInstance(Object component);

    /**
     * @param componentKey key to look up instance
     *
     * @return the component instance, or null if key is not registered
     * @throws PicoInitializationException if the key is registered, but the associated
     * component can't be instantiated.
     */
    Object getComponentInstance(Object componentKey) throws PicoInitializationException;

    Collection getComponentInstances() throws PicoInitializationException;

    Collection getComponentKeys();

    boolean hasComponentInstance(Object componentKey);

    ComponentAdapter findComponentAdapter(Object componentKey) throws AmbiguousComponentResolutionException;

    Object findComponentInstance(Class componentType) throws PicoInitializationException;

    ComponentAdapter findImplementingComponentAdapter(Class componentType) throws AmbiguousComponentResolutionException;

	void unregisterComponent(Object componentKey);
}
