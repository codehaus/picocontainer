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

import org.picocontainer.ComponentRegistry;

import java.io.Serializable;
import java.util.*;

/**
 * The default component registry.
 *
 * @author Aslak Hellesoy, Paul Hammant, various
 * @version $Revision: 1.8 $
 */

public class DefaultComponentRegistry implements ComponentRegistry, Serializable {

    protected final List registeredComponents;

    // Keeps track of the instantiation order
    protected final List orderedComponents;

    protected final Map componentKeyToInstanceMap;

    protected final Map componentToSpec;


    public DefaultComponentRegistry() {
        registeredComponents = new ArrayList();
        orderedComponents = new ArrayList();
        componentKeyToInstanceMap = new HashMap();
        componentToSpec = new HashMap();
    }

    public void registerComponent(ComponentSpecification compSpec) {
        componentToSpec.put(compSpec.getComponentImplementation(), compSpec);
        registeredComponents.add(compSpec);
    }

    public Collection getComponentSpecifications() {
        return registeredComponents;
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

    public Set getComponentInstanceKeys() {
        Set types = componentKeyToInstanceMap.keySet();
        return Collections.unmodifiableSet(types);
    }

    public Set getComponentInstances() {
//        ArrayList list = new ArrayList();
//        Set types = componentKeyToInstanceMap.entrySet();
//        for (Iterator iterator = types.iterator(); iterator.hasNext();) {
//            Map.Entry e = (Map.Entry) iterator.next();
//            list.add(e.getValue());
//        }
        Set result = new HashSet();
        result.addAll(componentKeyToInstanceMap.values());
        return Collections.unmodifiableSet(result);
    }

    public boolean hasComponentInstance(Object componentKey) {
        return componentKeyToInstanceMap.containsKey(componentKey);
    }

    public ComponentSpecification getComponentSpec(Object componentKey) {
        return (ComponentSpecification) componentToSpec.get(componentKey);
    }

}
