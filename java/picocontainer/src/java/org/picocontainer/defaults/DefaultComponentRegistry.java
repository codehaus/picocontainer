/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.picocontainer.defaults;

import org.picocontainer.PicoInitializationException;
import org.picocontainer.internals.ComponentAdapter;
import org.picocontainer.internals.ComponentRegistry;

import java.io.Serializable;
import java.util.*;

/**
 * The default component registry.
 *
 * @author Aslak Hellesoy, Paul Hammant, various
 * @version $Revision: 1.8 $
 */

public class DefaultComponentRegistry implements ComponentRegistry, Serializable {

    protected final List registeredComponentSpecifications;

    // Keeps track of the instantiation order
    protected final List orderedComponents;

    protected final Map componentKeyToInstanceMap;

    protected final Map componentToSpec;


    public DefaultComponentRegistry() {
        registeredComponentSpecifications = new ArrayList();
        orderedComponents = new ArrayList();
        componentKeyToInstanceMap = new HashMap();
        componentToSpec = new HashMap();
    }

    public void registerComponent(ComponentAdapter compSpec) {
        componentToSpec.put(compSpec.getComponentImplementation(), compSpec);
        registeredComponentSpecifications.add(compSpec);
    }

    public void unregisterComponent(Object componentKey) {
        for (Iterator iterator = registeredComponentSpecifications.iterator(); iterator.hasNext();) {
            ComponentAdapter currentCompSpec = (ComponentAdapter) iterator.next();

            if (currentCompSpec.getComponentKey().equals(componentKey)) {
                registeredComponentSpecifications.remove(currentCompSpec);
                componentKeyToInstanceMap.remove(componentKey);
                break;
            }
        }
    }

    public Collection getComponentSpecifications() {
        return registeredComponentSpecifications;
    }

    public List getOrderedComponents() {
        return new ArrayList(orderedComponents);
    }

    public void addOrderedComponent(Object component) {
        orderedComponents.add(component);
    }

    public void putComponent(Object componentKey, Object component) {
        componentKeyToInstanceMap.put(componentKey, component);
    }

    public boolean contains(Object componentKey) {
        return componentKeyToInstanceMap.containsKey(componentKey);
    }

    public Object getComponentInstance(Object componentKey) {
        return componentKeyToInstanceMap.get(componentKey);
    }

    public Collection getComponentInstanceKeys() {
        return Collections.unmodifiableCollection(componentKeyToInstanceMap.keySet());
    }

    public Collection getComponentInstances() {
        return Collections.unmodifiableCollection(componentKeyToInstanceMap.values());
    }

    public boolean hasComponentInstance(Object componentKey) {
        return componentKeyToInstanceMap.containsKey(componentKey);
    }

    public ComponentAdapter getComponentAdapter(Object componentKey) {
        return (ComponentAdapter) componentToSpec.get(componentKey);
    }

    public Object findImplementingComponent(Class componentType) throws AmbiguousComponentResolutionException {
        List found = new ArrayList();

        for (Iterator iterator = getComponentInstanceKeys().iterator(); iterator.hasNext();) {
            Object key = iterator.next();
            Object component = getComponentInstance(key);
            if (componentType.isInstance(component)) {
                found.add(key);
            }
        }

        if (found.size() > 1) {
            Object[] ambiguousKeys = found.toArray();
            throw new AmbiguousComponentResolutionException(componentType, ambiguousKeys);
        }

        return found.isEmpty() ? null : getComponentInstance(found.get(0));
    }

    public ComponentAdapter findImplementingComponentAdapter(Class componentType) throws AmbiguousComponentResolutionException {
        List found = new ArrayList();
        for (Iterator iterator = getComponentSpecifications().iterator(); iterator.hasNext();) {
            ComponentAdapter componentSpecification = (ComponentAdapter) iterator.next();

            if (componentType.isAssignableFrom(componentSpecification.getComponentImplementation())) {
                found.add(componentSpecification);
            }
        }

        if (found.size() > 1) {
            Class[] foundClasses = new Class[found.size()];
            for (int i = 0; i < foundClasses.length; i++) {
                foundClasses[i] = ((ComponentAdapter) found.get(i)).getComponentImplementation();
            }
            throw new AmbiguousComponentResolutionException(componentType, foundClasses);
        }

        return found.isEmpty() ? null : ((ComponentAdapter) found.get(0));
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
