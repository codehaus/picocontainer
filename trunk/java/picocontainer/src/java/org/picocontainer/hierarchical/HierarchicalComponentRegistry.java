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
import org.picocontainer.PicoInitializationException;
import org.picocontainer.defaults.AmbiguousComponentResolutionException;
import org.picocontainer.defaults.ComponentSpecification;
import org.picocontainer.defaults.DefaultComponentRegistry;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

public class HierarchicalComponentRegistry implements ComponentRegistry, Serializable {

    protected final ComponentRegistry parentRegistry;
    protected final ComponentRegistry childRegistry;

    protected HierarchicalComponentRegistry(ComponentRegistry parentRegistry, ComponentRegistry childRegistry) {
        if (parentRegistry == null) {
            throw new NullPointerException("parentRegistry cannot be null");
        }
        if (childRegistry == null) {
            throw new NullPointerException("childRegistry cannot be null");
        }
        this.parentRegistry = parentRegistry;
        this.childRegistry = childRegistry;
    }

    public static class Default extends HierarchicalComponentRegistry {
        public Default(ComponentRegistry parentRegistry) {
            super(parentRegistry, new DefaultComponentRegistry());
        }
    }

    public static class WithChildRegistry extends HierarchicalComponentRegistry {
        public WithChildRegistry(ComponentRegistry parentRegistry, ComponentRegistry childRegistry) {
            super(parentRegistry, childRegistry);
        }
    }

    public void registerComponent(ComponentSpecification compSpec) {
        childRegistry.registerComponent(compSpec);
    }

    public Collection getComponentSpecifications() {
        return childRegistry.getComponentSpecifications();
    }

    public List getOrderedComponents() {
        // Get child types
        List types = new ArrayList(childRegistry.getOrderedComponents());

        // Get those from parent.
        types.addAll(parentRegistry.getOrderedComponents());

        return Collections.unmodifiableList(types);
    }

    public void addOrderedComponent(Object component) {
        childRegistry.addOrderedComponent(component);
    }

    public void putComponent(Object componentKey, Object component) {
        childRegistry.putComponent(componentKey, component);
    }

    public boolean contains(Object componentKey) {
        return childRegistry.contains(componentKey);
    }

    public Object getComponentInstance(Object componentKey) {

        // First look in child
        Object result = childRegistry.getComponentInstance(componentKey);

        // Then look in parent if we had nothing
        if (result == null) {
            result = parentRegistry.getComponentInstance(componentKey);
        }
        return result;
    }

    public Set getComponentInstanceKeys() {

        // Get child types
        Set types = new HashSet(childRegistry.getComponentInstanceKeys());

        // Get those from parent.
        types.addAll(parentRegistry.getComponentInstanceKeys());

        return Collections.unmodifiableSet(types);

    }

    public Set getComponentInstances() {
        // Get child types
        Set types = new HashSet(childRegistry.getComponentInstances());

        // Get those from parent.
        types.addAll(parentRegistry.getComponentInstances());

        return Collections.unmodifiableSet(types);
    }

    public boolean hasComponentInstance(Object componentKey) {
        return childRegistry.hasComponentInstance(componentKey)
            | parentRegistry.hasComponentInstance(componentKey);
    }

    public ComponentSpecification getComponentSpec(Object componentKey) {
        // First look in child
        ComponentSpecification result = childRegistry.getComponentSpec(componentKey);

        // Then look in parent if we had nothing
        if (result == null) {
            result = parentRegistry.getComponentSpec(componentKey);
        }
        return result;

    }

    public Object findImplementingComponent(Class componentType) throws AmbiguousComponentResolutionException {

        // First look in child
        Object result = childRegistry.findImplementingComponent(componentType);

        // Then look in parent if we had nothing
        if (result == null) {
            result = parentRegistry.findImplementingComponent(componentType);
        }
        return result;

    }

    public ComponentSpecification findImplementingComponentSpecification(Class componentType) throws AmbiguousComponentResolutionException {

        // First look in child
        ComponentSpecification result = childRegistry.findImplementingComponentSpecification(componentType);

        // Then look in parent if we had nothing
        if (result == null) {
            result = parentRegistry.findImplementingComponentSpecification(componentType);
        }
        return result;
    }

    public Object createComponent(ComponentSpecification componentSpecification) throws PicoInitializationException {
        if (!contains(componentSpecification.getComponentKey())) {
            Object component = componentSpecification.instantiateComponent(this);
            addOrderedComponent(component);

            putComponent(componentSpecification.getComponentKey(), component);

            return component;
        } else {
            return getComponentInstance(componentSpecification.getComponentKey());
        }
    }

}
