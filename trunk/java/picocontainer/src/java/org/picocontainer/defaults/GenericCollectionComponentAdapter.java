package org.picocontainer.defaults;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Collection;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Set;
import java.util.HashSet;
import java.lang.reflect.Array;

/**
 * This component adapter is capable of instantiating Arrays, Maps and Collections of certain types.
 * The contents of these instances depends on the keyType and valueType arguments.
 * <p/>
 * This makes it possible to support depenencies on arrays and generic collections.
 * <p/>
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
class GenericCollectionComponentAdapter extends AbstractComponentAdapter {
    private final Class keyType;
    private final Class valueType;
    private final Class collectionType;
    private final Class collectionClass;

    public GenericCollectionComponentAdapter(Object componentKey, Class keyType, Class valueType, Class collectionType) {
        super(componentKey, HashMap.class);
        this.keyType = keyType;
        this.valueType = valueType;
        this.collectionType = collectionType;

        // The order of tests are significant. The least generic types last.
        if (Array.class.isAssignableFrom(collectionType)) {
            collectionClass = Array.class;
        } else if (List.class.isAssignableFrom(collectionType)) {
            collectionClass = ArrayList.class;
        } else if (SortedSet.class.isAssignableFrom(collectionType)) {
            collectionClass = TreeSet.class;
        } else if (Set.class.isAssignableFrom(collectionType)) {
            collectionClass = HashSet.class;
        } else if (Collection.class.isAssignableFrom(collectionType)) {
            collectionClass = ArrayList.class;
        } else if (SortedMap.class.isAssignableFrom(collectionType)) {
            collectionClass = TreeMap.class;
        } else if (Map.class.isAssignableFrom(collectionType)) {
            collectionClass = HashMap.class;
        } else {
            throw new PicoIntrospectionException("Unsupported collection type: " + collectionType.getName());
        }
    }

    public Object getComponentInstance() throws PicoInitializationException, PicoIntrospectionException {
        List adaptersOfType = getContainer().getComponentAdaptersOfType(valueType);
        if (Array.class.isAssignableFrom(collectionType)) {
            return getArrayInstance(adaptersOfType);
        } else if (Map.class.isAssignableFrom(collectionType)) {
                return getMapInstance(adaptersOfType);
        } else {
            return getCollectionInstance(adaptersOfType);
        }
    }

    private Object[] getArrayInstance(List adaptersOfType) {
        Object[] result = (Object[]) Array.newInstance(valueType, adaptersOfType.size());
        int i = 0;
        for (Iterator iterator = adaptersOfType.iterator(); iterator.hasNext();) {
            ComponentAdapter componentAdapter = (ComponentAdapter) iterator.next();
            result[i] = componentAdapter.getComponentInstance();
            i++;
        }
        return result;
    }

    private Collection getCollectionInstance(List adaptersOfType) {
        try {
            Collection result = (Collection) collectionClass.newInstance();
            for (Iterator iterator = adaptersOfType.iterator(); iterator.hasNext();) {
                ComponentAdapter componentAdapter = (ComponentAdapter) iterator.next();
                result.add(componentAdapter.getComponentInstance());
            }
            return result;
        } catch (InstantiationException e) {
            throw new PicoInitializationException(e);
        } catch (IllegalAccessException e) {
            throw new PicoInitializationException(e);
        }
    }

    private Map getMapInstance(List adaptersOfType) {
        try {
            Map result = (Map) collectionClass.newInstance();
            for (Iterator iterator = adaptersOfType.iterator(); iterator.hasNext();) {
                ComponentAdapter componentAdapter = (ComponentAdapter) iterator.next();
                Object componentKey = componentAdapter.getComponentKey();
                if (keyType.isAssignableFrom(componentKey.getClass())) {
                    result.put(componentKey, componentAdapter.getComponentInstance());
                }
            }
            return result;
        } catch (InstantiationException e) {
            throw new PicoInitializationException(e);
        } catch (IllegalAccessException e) {
            throw new PicoInitializationException(e);
        }
    }

    public void verify() throws UnsatisfiableDependenciesException {

    }
}