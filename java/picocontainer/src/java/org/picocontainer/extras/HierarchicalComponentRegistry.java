/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.extras;

import org.picocontainer.PicoInitializationException;
import org.picocontainer.defaults.AmbiguousComponentResolutionException;
import org.picocontainer.defaults.DefaultComponentRegistry;
import org.picocontainer.internals.ComponentAdapter;
import org.picocontainer.internals.ComponentRegistry;

import java.io.Serializable;
import java.util.*;

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

    public void registerComponent(ComponentAdapter compSpec) {
        childRegistry.registerComponent(compSpec);
    }
    
	public void unregisterComponent(Object componentKey) {
		childRegistry.unregisterComponent(componentKey);
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

    public Collection getComponentInstanceKeys() {

        // Get child types
        Set types = new HashSet(childRegistry.getComponentInstanceKeys());

        // Get those from parent.
        types.addAll(parentRegistry.getComponentInstanceKeys());

        return Collections.unmodifiableCollection(types);

    }

    public Collection getComponentInstances() {
        // Get child types
        Set types = new HashSet(childRegistry.getComponentInstances());

        // Get those from parent.
        types.addAll(parentRegistry.getComponentInstances());

        return Collections.unmodifiableCollection(types);
    }

    public boolean hasComponentInstance(Object componentKey) {
        return childRegistry.hasComponentInstance(componentKey)
                | parentRegistry.hasComponentInstance(componentKey);
    }

    public ComponentAdapter getComponentAdapter(Object componentKey) {
        // First look in child
        ComponentAdapter result = childRegistry.getComponentAdapter(componentKey);

        // Then look in parent if we had nothing
        if (result == null) {
            result = parentRegistry.getComponentAdapter(componentKey);
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

    public ComponentAdapter findImplementingComponentAdapter(Class componentType) throws AmbiguousComponentResolutionException {

        // First look in child
        ComponentAdapter result = childRegistry.findImplementingComponentAdapter(componentType);

        // Then look in parent if we had nothing
        if (result == null) {
            result = parentRegistry.findImplementingComponentAdapter(componentType);
        }
        return result;
    }

    public Object createComponent(ComponentAdapter componentAdapter) throws PicoInitializationException {
        if (!contains(componentAdapter.getComponentKey())) {
            Object component = componentAdapter.instantiateComponent(this);
            addOrderedComponent(component);

            putComponent(componentAdapter.getComponentKey(), component);

            return component;
        } else {
            return getComponentInstance(componentAdapter.getComponentKey());
        }
    }

}
