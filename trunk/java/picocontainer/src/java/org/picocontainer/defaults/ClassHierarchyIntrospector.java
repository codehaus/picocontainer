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

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Helper class for introspecting interface and class hierarchies.
 *
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @version $Revision$
 */
public class ClassHierarchyIntrospector {
    /** the {@link Object#equals(Object)} method. */ 
    public static Method equals;
    /** the {@link Object#hashCode()} method. */ 
    public static Method hashCode;

    static {
        try {
            equals = Object.class.getMethod("equals", new Class[]{Object.class});
            hashCode = Object.class.getMethod("hashCode", null);
        } catch (NoSuchMethodException e) {
            ///CLOVER:OFF
            throw new InternalError();
            ///CLOVER:ON
        } catch (SecurityException e) {
            ///CLOVER:OFF
            throw new InternalError();
            ///CLOVER:ON
        }
    }
    
    private ClassHierarchyIntrospector() {
    }

    /**
     * Get all the interfaces implemented by a list of objects.
     * @param objects the {@link List} of objects to consider.
     * @return a {@link Set} of interfaces.
     */
    public static Set getAllInterfaces(List objects) {
        Set interfaces = new HashSet();
        for (Iterator iterator = objects.iterator(); iterator.hasNext();) {
            Object o = iterator.next();
            Class clazz = o.getClass();
            getInterfaces(clazz, interfaces);
        }
        return interfaces;
    }

    /**
     * Get all interfaces of the given type.
     * If the type is a {@link Class}, the returned list contains any interface, that is
     * implemented by the class. If the type is an interface, the all 
     * superinterfaces and the interface itself are included.
     * @param clazz type to explore.
     * @return a {@link Set} with all interfaces. The array may be empty.
     */
    public static Set getAllInterfaces(Class clazz) {
        Set interfaces = new HashSet();
        getInterfaces(clazz, interfaces);
        return interfaces;
    }

    private static void getInterfaces(Class clazz, Set interfaces) {
        if(clazz.isInterface()) {
            interfaces.add(clazz);
        }
        // Class.getInterfaces will return only the interfaces that are 
        // implemented by the current class. Therefore we must loop up
        // the hierarchy for the superclasses and the superinterfaces. 
        while (clazz != null) {
            Class[] implemented = clazz.getInterfaces();
            for(int i = 0; i < implemented.length; i++) {
                if (!interfaces.contains(implemented[i])) {
                    getInterfaces(implemented[i], interfaces);
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    /**
     * Get most common superclass for all given objects. 
     * @param objects the array of objects to consider.
     * @return the superclass or <code>{@link Void}.class</code> for an empty array.
     */
    public static Class getMostCommonSuperclass(Object[] objects) {
        Class clazz = Void.class;
        boolean found = false;
        if (objects != null && objects.length > 0) {
            while (!found) {
                for (int i = 0; i < objects.length; i++) {
                    found = true;
                    if (objects[i] != null) {
                        Class currentClazz = objects[i].getClass();
                        if(clazz == Void.class) {
                            clazz = currentClazz;
                        }
                        if(!clazz.isAssignableFrom(currentClazz)) {
                            if(currentClazz.isAssignableFrom(clazz)) {
                                clazz = currentClazz;
                            } else {
                                clazz = clazz.getSuperclass();
                                found = false;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return clazz;
    }
}
