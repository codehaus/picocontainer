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

    // Keeps track of the instantiation order
    private final List orderedComponents;

    private final Map componentKeyToAdapterMap;


    public DefaultComponentRegistry() {
        orderedComponents = new ArrayList();
        componentKeyToAdapterMap = new HashMap();
    }

    public void registerComponent(ComponentAdapter componentAdapter) {
        componentKeyToAdapterMap.put(componentAdapter.getComponentKey(), componentAdapter);
    }

    public void unregisterComponent(Object componentKey) {
        componentKeyToAdapterMap.remove(componentKey);
    }

    public List getComponentAdapters() {
        // TODO this might break the ordering Jon introduced. --Aslak
        return new ArrayList(componentKeyToAdapterMap.values());
    }

    public List getOrderedComponents() {
        return new ArrayList(orderedComponents);
    }

    public void addOrderedComponentInstance(Object componentInstance) {
        orderedComponents.add(componentInstance);
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
        ArrayList componentInstances = new ArrayList(componentKeyToAdapterMap.size());
        for (Iterator iterator = componentKeyToAdapterMap.keySet().iterator(); iterator.hasNext();) {
            componentInstances.add(getComponentInstance(iterator.next()));
        }
        return Collections.unmodifiableCollection(componentInstances);
    }

    public Collection getComponentKeys() {
        return Collections.unmodifiableCollection(componentKeyToAdapterMap.keySet());
    }

    public Object findComponentInstance(Class componentType) throws PicoInitializationException {
        List foundKeys = new ArrayList();
        Object result = null;
        for (Iterator iterator = getComponentKeys().iterator(); iterator.hasNext();) {
            Object key = iterator.next();
            Object componentInstance = getComponentInstance(key);
            if (componentType.isInstance(componentInstance)) {
                result = componentInstance;
                foundKeys.add(key);
            }
        }

        if (foundKeys.size() == 0) {
            return null;
        } else if(foundKeys.size() >= 2) {
            throw new AmbiguousComponentResolutionException(componentType, foundKeys.toArray());
        }

        return result;
    }

    public boolean hasComponentInstance(Object componentKey) {
        return componentKeyToAdapterMap.containsKey(componentKey);
    }

    public ComponentAdapter findComponentAdapter(Object componentKey) throws AmbiguousComponentResolutionException {
        ComponentAdapter result = (ComponentAdapter) componentKeyToAdapterMap.get(componentKey);
        if(result == null && componentKey instanceof Class) {
            // see if we find a matching one if the key is a class
            Class classKey = (Class) componentKey;
            result = findImplementingComponentAdapter(classKey);
        }
        return result;
    }

    public ComponentAdapter findImplementingComponentAdapter(Class componentType) throws AmbiguousComponentResolutionException {
        List found = new ArrayList();
        for (Iterator iterator = getComponentAdapters().iterator(); iterator.hasNext();) {
            ComponentAdapter componentAdapter = (ComponentAdapter) iterator.next();

            if (componentType.isAssignableFrom(componentAdapter.getComponentImplementation())) {
                found.add(componentAdapter);
            }
        }

        if (found.size() == 0) {
            return null;
        } else if (found.size() >= 2) {
            Class[] foundClasses = new Class[found.size()];
            for (int i = 0; i < foundClasses.length; i++) {
                foundClasses[i] = ((ComponentAdapter) found.get(i)).getComponentImplementation();
            }
            throw new AmbiguousComponentResolutionException(componentType, foundClasses);
        }

        return found.isEmpty() ? null : ((ComponentAdapter) found.get(0));
    }

}
