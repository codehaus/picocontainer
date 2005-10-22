/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.gems.util;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.InstanceComponentAdapter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
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
        return mutablePicoContainer.getComponentAdapters().size();
    }

    public boolean isEmpty() {
        return mutablePicoContainer.getComponentAdapters().size() == 0;
    }

    public boolean containsKey(Object o) {
        if (o instanceof Class) {
            return mutablePicoContainer.getComponentInstanceOfType((Class)o) != null;
        } else {
            return mutablePicoContainer.getComponentInstance(o) != null;
        }
    }

    public boolean containsValue(Object o) {
        return false;
    }

    public Object get(Object o) {
        if (o instanceof Class) {
            return mutablePicoContainer.getComponentInstanceOfType((Class)o);
        } else {
            return mutablePicoContainer.getComponentInstance(o);
        }
    }

    public Object put(Object o, Object o1) {
        Object object = remove(o);
        if (o1 instanceof Class) {
            mutablePicoContainer.registerComponentImplementation(o, (Class)o1);
        } else {
            mutablePicoContainer.registerComponentInstance(o, o1);
        }
        return object;
    }

    public Object remove(Object o) {
        ComponentAdapter adapter = mutablePicoContainer.unregisterComponent(o);
        if (adapter != null) {
            // if previously an instance was registered, return it, otherwise return the type
            return adapter instanceof InstanceComponentAdapter ? adapter
                    .getComponentInstance(mutablePicoContainer) : adapter
                    .getComponentImplementation();
        } else {
            return null;
        }
    }

    public void putAll(Map map) {
        for (final Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
            final Map.Entry entry = (Map.Entry)iter.next();
            put(entry.getKey(), entry.getValue());
        }
    }

    public void clear() {
        Set adapters = keySet();
        for (final Iterator iter = adapters.iterator(); iter.hasNext();) {
            mutablePicoContainer.unregisterComponent(iter.next());
        }
    }

    public Set keySet() {
        Set set = new HashSet();
        Collection adapters = mutablePicoContainer.getComponentAdapters();
        for (final Iterator iter = adapters.iterator(); iter.hasNext();) {
            final ComponentAdapter adapter = (ComponentAdapter)iter.next();
            set.add(adapter.getComponentKey());
        }
        return Collections.unmodifiableSet(set);
    }

    public Collection values() {
        return Collections.unmodifiableCollection(mutablePicoContainer.getComponentInstances());
    }

    public Set entrySet() {
        Set set = new HashSet();
        Collection adapters = mutablePicoContainer.getComponentAdapters();
        for (final Iterator iter = adapters.iterator(); iter.hasNext();) {
            final Object key = ((ComponentAdapter)iter.next()).getComponentKey();
            final Object component = mutablePicoContainer.getComponentInstance(key);
            set.add(new Map.Entry() {
                public Object getKey() {
                    return key;
                }

                public Object getValue() {
                    return component;
                }

                public Object setValue(Object value) {
                    throw new UnsupportedOperationException("Cannot set component");
                }
            });
        }
        return Collections.unmodifiableSet(set);
    }
}
