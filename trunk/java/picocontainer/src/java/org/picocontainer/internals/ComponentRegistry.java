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

    void registerComponent(ComponentSpecification compSpec);

    Collection getComponentSpecifications();

    List getOrderedComponents();

    void addOrderedComponent(Object component);

    void putComponent(Object componentKey, Object component);

    boolean contains(Object componentKey);

    Object getComponentInstance(Object componentKey);

    Collection getComponentInstanceKeys();

    Collection getComponentInstances();

    boolean hasComponentInstance(Object componentKey);

    ComponentSpecification getComponentSpec(Object componentKey);

    Object findImplementingComponent(Class componentType) throws AmbiguousComponentResolutionException;

    ComponentSpecification findImplementingComponentSpecification(Class componentType) throws AmbiguousComponentResolutionException;

    Object createComponent(ComponentSpecification componentSpecification) throws PicoInitializationException;

	void unregisterComponent(Object componentKey);
}
