/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/

package org.picocontainer;

import org.picocontainer.defaults.ComponentSpecification;

import java.util.Iterator;
import java.util.List;
import java.util.Collection;

public interface ComponentRegistry {

    void registerComponent(ComponentSpecification compSpec);

    Iterator getRegisteredComponentIterator();

    List getOrderedComponents();

    void addOrderedComponent(Object component);

    void putComponent(Object componentKey, Object component);

    boolean contains(Object componentKey);
 // was...
 // boolean contains(ComponentSpecification componentSpecification);

    Object getComponentInstance(Object componentKey);

    Collection getComponentInstanceKeys();

    Collection getComponentInstances();


    ComponentSpecification getComponentSpec(Object componentKey);

}
