/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.defaults;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoInstantiationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoVerificationException;
import org.picocontainer.PicoVisitor;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;


/**
 * A CollectionComponentParameter should be used to support inject an {@link Array}, a
 * {@link Collection}or {@link Map}of components automatically. The collection will contain
 * all components of a special type and additionally the type of the key may be specified. In
 * case of a map, the map's keys are the one of the component adapter.
 * 
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @since 1.1
 */
public class CollectionComponentParameter
        implements Parameter, Serializable {

    private final boolean emptyCollection;
    private final Class componentKeyType;
    private final Class componentValueType;

    /**
     * Expect an {@link Array}of an appropriate type as parameter. At least one component of
     * the array's component type must exist.
     */
    public CollectionComponentParameter() {
        this(false);
    }

    /**
     * Expect an {@link Array}of an appropriate type as parameter.
     * 
     * @param emptyCollection <code>true</code> if an empty array also is a valid dependency
     *                   resolution.
     */
    public CollectionComponentParameter(boolean emptyCollection) {
        this(Void.TYPE, emptyCollection);
    }

    /**
     * Expect any of the collection types {@link Array},{@link Collection}or {@link Map}as
     * parameter.
     * 
     * @param componentValueType the type of the components (ignored in case of an Array)
     * @param emptyCollection <code>true</code> if an empty collection resolves the
     *                   dependency.
     */
    public CollectionComponentParameter(Class componentValueType, boolean emptyCollection) {
        this(Object.class, componentValueType, emptyCollection);
    }

    /**
     * Expect any of the collection types {@link Array},{@link Collection}or {@link Map}as
     * parameter.
     * 
     * @param componentKeyType the type of the component's key
     * @param componentValueType the type of the components (ignored in case of an Array)
     * @param emptyCollection <code>true</code> if an empty collection resolves the
     *                   dependency.
     */
    public CollectionComponentParameter(Class componentKeyType, Class componentValueType, boolean emptyCollection) {
        this.emptyCollection = emptyCollection;
        this.componentKeyType = componentKeyType;
        this.componentValueType = componentValueType;
    }

    /**
     * Resolve the parameter for the expected type. The method will return <code>null</code>
     * If the expected type is not one of the collection types {@link Array},
     * {@link Collection}or {@link Map}. An empty collection is only a valid resolution, if
     * the <code>emptyCollection</code> flag was set.
     * 
     * @param container {@inheritDoc}
     * @param adapter {@inheritDoc}
     * @param expectedType {@inheritDoc}
     * @return the instance of the collection type or <code>null</code>
     * @throws PicoInstantiationException {@inheritDoc}
     */
    public Object resolveInstance(PicoContainer container, ComponentAdapter adapter, Class expectedType)
            throws PicoInstantiationException {
        // type check is done in isResolvable
        Object result = null;
        final Class collectionType = getCollectionType(expectedType);
        if (collectionType != null) {
            final Map adapterMap = getMatchingComponentAdapters(container, adapter, componentKeyType, getValueType(expectedType));
            if (Array.class.isAssignableFrom(collectionType)) {
                result = getArrayInstance(container, expectedType, adapterMap);
            } else if (Map.class.isAssignableFrom(collectionType)) {
                result = getMapInstance(container, expectedType, adapterMap);
            } else {
                result = getCollectionInstance(container, expectedType, adapterMap);
            }
        }
        return result;
    }

    /**
     * Test for dependency resolution of the parameter for the expected type. The method will
     * return <code>false</code> If the expected type is not one of the collection types
     * {@link Array},{@link Collection}or {@link Map}. An empty collection is only a valid
     * resolution, if the <code>emptyCollection</code> flag was set.
     * 
     * @param container {@inheritDoc}
     * @param adapter {@inheritDoc}
     * @param expectedType {@inheritDoc}
     * @return <code>true</code> the instance of the collection type or <code>null</code>
     */
    public boolean isResolvable(PicoContainer container, ComponentAdapter adapter, Class expectedType) {
        final Class collectionType = getCollectionType(expectedType);
        if (collectionType != null) {
            return emptyCollection
                    || !getMatchingComponentAdapters(container, adapter, componentKeyType, getValueType(expectedType)).isEmpty();
        }
        return false;
    }

    /**
     * Verify a successful dependency resolution of the parameter for the expected type. The
     * method will only return if the expected type is one of the collection types {@link Array},
     * {@link Collection}or {@link Map}. An empty collection is only a valid resolution, if
     * the <code>emptyCollection</code> flag was set.
     * 
     * @param container {@inheritDoc}
     * @param adapter {@inheritDoc}
     * @param expectedType {@inheritDoc}
     * @throws PicoIntrospectionException {@inheritDoc}
     */
    public void verify(PicoContainer container, ComponentAdapter adapter, Class expectedType) throws PicoIntrospectionException {
        final Class collectionType = getCollectionType(expectedType);
        if (collectionType != null) {
            final Class valueType = getValueType(expectedType);
            final Map adapterMap = getMatchingComponentAdapters(container, adapter, componentKeyType, valueType);
            if (adapterMap.isEmpty()) {
                if (!emptyCollection) {
                    final List list = new LinkedList();
                    list.add(new PicoIntrospectionException(expectedType.getName() + " not resolvable"));
                    throw new PicoVerificationException(list);
                }
            } else {
                for (final Iterator iter = adapterMap.entrySet().iterator(); iter.hasNext();) {
                    final Map.Entry entry = (Map.Entry) iter.next();
                    final ComponentAdapter componentAdapter = (ComponentAdapter) entry.getValue();
                    componentAdapter.verify(container);
                }
            }
        }
        return;
    }

    /**
     * {@inheritDoc}
     */
    public void accept(PicoVisitor visitor) {
        visitor.visitParameter(this);
    }

    private Map getMatchingComponentAdapters(PicoContainer container, ComponentAdapter adapter, Class keyType, Class valueType) {
        final Map adapterMap = new HashMap();
        final PicoContainer parent = container.getParent();
        if (parent != null) {
            adapterMap.putAll(getMatchingComponentAdapters(parent, adapter, keyType, valueType));
        }
        final Collection allAdapters = container.getComponentAdapters();
        for (final Iterator iter = allAdapters.iterator(); iter.hasNext();) {
            final ComponentAdapter componentAdapter = (ComponentAdapter) iter.next();
            adapterMap.remove(componentAdapter.getComponentKey());
        }
        final List adapterList = container.getComponentAdaptersOfType(valueType);
        for (final Iterator iter = adapterList.iterator(); iter.hasNext();) {
            final ComponentAdapter componentAdapter = (ComponentAdapter) iter.next();
            final Object key = componentAdapter.getComponentKey();
            if (key.equals(adapter.getComponentKey())) {
                continue;
            }
            if (keyType.isAssignableFrom(key.getClass())) {
                adapterMap.put(key, componentAdapter);
            }
        }
        return adapterMap;
    }

    private Class getCollectionType(final Class collectionType) {
        Class collectionClass = null;
        if (collectionType.isArray()) {
            collectionClass = Array.class;
        } else if (Map.class.isAssignableFrom(collectionType)) {
            collectionClass = Map.class;
        } else if (Collection.class.isAssignableFrom(collectionType)) {
            collectionClass = Collection.class;
        }
        return collectionClass;
    }

    private Class getValueType(final Class collectionType) {
        Class valueType = componentValueType;
        if (collectionType.isArray()) {
            valueType = collectionType.getComponentType();
        }
        return valueType;
    }

    private Object[] getArrayInstance(final PicoContainer container, final Class expectedType, final Map adapterList) {
        final Object[] result = (Object[]) Array.newInstance(expectedType.getComponentType(), adapterList.size());
        int i = 0;
        for (final Iterator iterator = adapterList.entrySet().iterator(); iterator.hasNext();) {
            final Map.Entry entry = (Map.Entry) iterator.next();
            final ComponentAdapter componentAdapter = (ComponentAdapter) entry.getValue();
            result[i] = componentAdapter.getComponentInstance(container);
            i++;
        }
        return result;
    }

    private Collection getCollectionInstance(final PicoContainer container, final Class expectedType, final Map adapterList) {
        Class collectionType = expectedType;
        if (collectionType.isInterface()) {
            // The order of tests are significant. The least generic types last.
            if (List.class.isAssignableFrom(collectionType)) {
                collectionType = ArrayList.class;
//            } else if (BlockingQueue.class.isAssignableFrom(collectionType)) {
//                collectionType = ArrayBlockingQueue.class;
//            } else if (Queue.class.isAssignableFrom(collectionType)) {
//                collectionType = LinkedList.class;
            } else if (SortedSet.class.isAssignableFrom(collectionType)) {
                collectionType = TreeSet.class;
            } else if (Set.class.isAssignableFrom(collectionType)) {
                collectionType = HashSet.class;
            } else if (Collection.class.isAssignableFrom(collectionType)) {
                collectionType = ArrayList.class;
            }
        }
        try {
            Collection result = (Collection) collectionType.newInstance();
            for (final Iterator iterator = adapterList.entrySet().iterator(); iterator.hasNext();) {
                final Map.Entry entry = (Map.Entry) iterator.next();
                final ComponentAdapter componentAdapter = (ComponentAdapter) entry.getValue();
                result.add(componentAdapter.getComponentInstance(container));
            }
            return result;
        } catch (InstantiationException e) {
            ///CLOVER:OFF
            throw new PicoInitializationException(e);
            ///CLOVER:ON
        } catch (IllegalAccessException e) {
            ///CLOVER:OFF
            throw new PicoInitializationException(e);
            ///CLOVER:ON
        }
    }

    private Map getMapInstance(final PicoContainer container, final Class expectedType, final Map adapterList) {
        Class collectionType = expectedType;
        if (collectionType.isInterface()) {
            // The order of tests are significant. The least generic types last.
            if (SortedMap.class.isAssignableFrom(collectionType)) {
                collectionType = TreeMap.class;
//            } else if (ConcurrentMap.class.isAssignableFrom(collectionType)) {
//                collectionType = ConcurrentHashMap.class;
            } else if (Map.class.isAssignableFrom(collectionType)) {
                collectionType = HashMap.class;
            }
        }
        try {
            Map result = (Map) collectionType.newInstance();
            for (final Iterator iterator = adapterList.entrySet().iterator(); iterator.hasNext();) {
                final Map.Entry entry = (Map.Entry) iterator.next();
                final ComponentAdapter componentAdapter = (ComponentAdapter) entry.getValue();
                result.put(componentAdapter.getComponentKey(), componentAdapter.getComponentInstance(container));
            }
            return result;
        } catch (InstantiationException e) {
            ///CLOVER:OFF
            throw new PicoInitializationException(e);
            ///CLOVER:ON
        } catch (IllegalAccessException e) {
            ///CLOVER:OFF
            throw new PicoInitializationException(e);
            ///CLOVER:ON
        }
    }

}
