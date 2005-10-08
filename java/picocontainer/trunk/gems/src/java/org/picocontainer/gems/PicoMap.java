package org.picocontainer.gems;

/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class PicoMap implements Map {

    private final MutablePicoContainer mutablePicoContainer;


    public PicoMap(MutablePicoContainer mutablePicoContainer) {
        this.mutablePicoContainer = mutablePicoContainer;
    }

    public PicoMap() {
        mutablePicoContainer = new DefaultPicoContainer();
    }


    public int size() {
        return mutablePicoContainer.getComponentInstances().size();
    }

    public boolean isEmpty() {
        return mutablePicoContainer.getComponentInstances().size() == 0;
    }

    public boolean containsKey(Object o) {
        if (o instanceof Class) {
            return mutablePicoContainer.getComponentInstanceOfType((Class) o) != null;
        } else {
            return mutablePicoContainer.getComponentInstance(o) != null;
        }
    }

    public boolean containsValue(Object o) {
        return false;
    }

    public Object get(Object o) {
        if (o instanceof Class) {
            return mutablePicoContainer.getComponentInstanceOfType((Class) o);
        } else {
            return mutablePicoContainer.getComponentInstance(o);
        }
    }

    public Object put(Object o, Object o1) {
        if (o instanceof Class) {
            Class key = (Class) o;
            if (o1 instanceof Class) {
                return mutablePicoContainer.registerComponentImplementation(key, (Class) o1);
            } else {
                return mutablePicoContainer.registerComponentInstance(key, o1);
            }

        } else {
            return mutablePicoContainer.registerComponentImplementation(o, (Class) o1);
        }
    }

    public Object remove(Object o) {
        return null;
    }

    public void putAll(Map map) {
        // consume in another Pico's Adapters ?
    }

    public void clear() {

    }

    public Set keySet() {
        return null;
    }

    public Collection values() {
        return null;
    }

    public Set entrySet() {
        return null;
    }
}
