/*****************************************************************************
 * Copyright (C) OldPicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.picocontainer.defaults;

import java.util.*;

/**
 * The default component registry.
 *
 * @author Aslak Hellesoy, Paul Hammant, various
 * @version $Revision: 1.8 $
 */

public class DefaultPicoContainer extends AbstractPicoContainer {
    private final Map componentKeyToAdapterMap = new HashMap();

    public DefaultPicoContainer() {
        this(new DefaultComponentAdapterFactory());
    }

    public DefaultPicoContainer(ComponentAdapterFactory componentAdapterFactory) {
        this.componentAdapterFactory = componentAdapterFactory;
    }


    public Collection getComponentKeys() {
        return Collections.unmodifiableCollection(componentKeyToAdapterMap.keySet());
    }

    public List getComponentAdapters() {
        return new ArrayList(componentKeyToAdapterMap.values());
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

    public void registerComponent(ComponentAdapter componentAdapter) throws DuplicateComponentKeyRegistrationException {
        if(getComponentKeys().contains(componentAdapter.getComponentKey())) {
            throw new DuplicateComponentKeyRegistrationException(componentAdapter.getComponentKey());
        }
        componentKeyToAdapterMap.put(componentAdapter.getComponentKey(), componentAdapter);
    }

    public void unregisterComponent(Object componentKey) {
        componentKeyToAdapterMap.remove(componentKey);
    }
}
