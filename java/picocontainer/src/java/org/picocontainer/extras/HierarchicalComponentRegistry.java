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

    public List getComponentAdapters() {
        List result = new ArrayList(childRegistry.getComponentAdapters());
        for (Iterator iterator = parentRegistry.getComponentAdapters().iterator(); iterator.hasNext();) {
            ComponentAdapter parentAdapter = (ComponentAdapter) iterator.next();
            if(!result.contains(parentAdapter)) {
                result.add(parentAdapter);
            }
        }
        return result;
    }

    public ComponentAdapter findComponentAdapter(Object componentKey) throws AmbiguousComponentResolutionException {
        // First look in child
        ComponentAdapter result = childRegistry.findComponentAdapter(componentKey);

        // Then look in parent if we had nothing
        if (result == null) {
            result = parentRegistry.findComponentAdapter(componentKey);
        }
        return result;

    }

    public List getOrderedComponents() {
        // Get child types
        List types = new ArrayList(childRegistry.getOrderedComponents());

        // Get those from parent.
        types.addAll(parentRegistry.getOrderedComponents());

        return Collections.unmodifiableList(types);
    }

    public void addOrderedComponentInstance(Object component) {
        childRegistry.addOrderedComponentInstance(component);
    }

    public Object getComponentInstance(Object componentKey) throws PicoInitializationException {
        ComponentAdapter componentAdapter = findComponentAdapter(componentKey);
        if(componentAdapter != null) {
            return componentAdapter.instantiateComponent(this);
        } else {
            return null;
        }
    }

    public Collection getComponentInstances() throws PicoInitializationException {
        Collection componentKeys = getComponentKeys();
        Collection result = new ArrayList(componentKeys.size());
        for (Iterator iterator = componentKeys.iterator(); iterator.hasNext();) {
            result.add(getComponentInstance(iterator.next()));
        }
        return Collections.unmodifiableCollection(result);
    }

    public Collection getComponentKeys() {

        // Get child types
        Set types = new HashSet(childRegistry.getComponentKeys());

        // Get those from parent.
        types.addAll(parentRegistry.getComponentKeys());

        return Collections.unmodifiableCollection(types);

    }

    public boolean hasComponentInstance(Object componentKey) {
        return childRegistry.hasComponentInstance(componentKey)
                | parentRegistry.hasComponentInstance(componentKey);
    }

    public Object findComponentInstance(Class componentType) throws PicoInitializationException {
        Object result = null;

        // First look in child
        ComponentAdapter childAdapter = childRegistry.findComponentAdapter(componentType);
        if( childAdapter != null ) {
            result = childAdapter.instantiateComponent(this);
            if( result == null ) {
                ComponentAdapter parentAdapter = parentRegistry.findComponentAdapter(componentType);
                if( parentAdapter != null ) {
                    result = parentAdapter.instantiateComponent(this);
                }

            }
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
}
