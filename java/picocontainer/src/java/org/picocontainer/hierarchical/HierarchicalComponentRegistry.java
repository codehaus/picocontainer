/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.hierarchical;

import org.picocontainer.ComponentRegistry;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ComponentSpecification;
import org.picocontainer.defaults.DefaultComponentRegistry;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class HierarchicalComponentRegistry implements ComponentRegistry, Serializable {

    protected PicoContainer parentContainer;
    protected ComponentRegistry componentRegistry;

    protected HierarchicalComponentRegistry(PicoContainer parentContainer, ComponentRegistry componentRegistry) {
        if (parentContainer == null) {
            throw new NullPointerException("parentContainer cannot be null");
        }
        if (componentRegistry == null) {
            throw new NullPointerException("componentRegistry cannot be null");
        }
        this.parentContainer = parentContainer;
        this.componentRegistry = componentRegistry;
    }

    public static class Default extends HierarchicalComponentRegistry {
        public Default(PicoContainer parentContainer) {
            super(parentContainer, new DefaultComponentRegistry());
        }
    }

    public static class WithParentContainerAndChildRegistry extends HierarchicalComponentRegistry {
        public WithParentContainerAndChildRegistry(PicoContainer parentContainer, ComponentRegistry childRegistry) {
            super(parentContainer, childRegistry);
        }
    }



    public void registerComponent(ComponentSpecification compSpec) {
        componentRegistry.registerComponent(compSpec);
    }

    public Iterator getRegisteredComponentIterator() {
        return componentRegistry.getRegisteredComponentIterator();
    }

    public List getOrderedComponents() {
        return componentRegistry.getOrderedComponents();
    }

    public void addOrderedComponent(Object component) {
        componentRegistry.addOrderedComponent(component);
    }

    public void putComponent(Object componentKey, Object component) {
        componentRegistry.putComponent(componentKey, component);
    }

    public boolean contains(Object componentKey) {
        return componentRegistry.contains(componentKey);
    }

    public Object getComponentInstance(Object componentKey) {

        // First look in child
        Object result = componentRegistry.getComponentInstance(componentKey);

        // Then look in parent if we had nothing
        if (result == null) {
            result = parentContainer.getComponent(componentKey);
        }
        return result;
    }

    public Set getComponentInstanceKeys() {

        // Get child types
        Set types = new HashSet(componentRegistry.getComponentInstanceKeys());

        // Get those from parent.
        types.addAll(parentContainer.getComponentKeys());

        return Collections.unmodifiableSet(types);

    }

    public Set getComponentInstances() {
        // Get child types
        Set types = new HashSet(componentRegistry.getComponentInstances());

        // Get those from parent.
        types.addAll(parentContainer.getComponents());

        return Collections.unmodifiableSet(types);
    }

    public boolean hasComponentInstance(Object componentKey) {
        return componentRegistry.hasComponentInstance(componentKey)
            | parentContainer.hasComponent(componentKey);
    }

    public ComponentSpecification getComponentSpec(Object componentKey) {
        return componentRegistry.getComponentSpec(componentKey);
    }

}
