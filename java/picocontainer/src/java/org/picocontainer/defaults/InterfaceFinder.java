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

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Helper class for finding implemented interfaces of classes and objects.
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class InterfaceFinder implements Serializable {
    public static Method equals;
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

    /**
     * Get all the interfaces implemented by a list of objects.
     * @return an array of interfaces.
     */
    public Class[] getAllInterfaces(List objects) {
        Set interfaces = new HashSet();
        for (Iterator iterator = objects.iterator(); iterator.hasNext();) {
            Object o = iterator.next();
            Class clazz = o.getClass();
            getInterfaces(clazz, interfaces);
        }

        return (Class[]) interfaces.toArray(new Class[interfaces.size()]);
    }

    public Class[] getAllInterfaces(Class clazz) {
        Set interfaces = new HashSet();
        getInterfaces(clazz, interfaces);
        return (Class[]) interfaces.toArray(new Class[interfaces.size()]);
    }

    private static void getInterfaces(Class clazz, Set interfaces) {
        if(clazz.isInterface()) {
            interfaces.add(clazz);
        }
        // Strangely enough Class.getInterfaces() does not include the interfaces
        // implemented by superclasses. So we must loop up the hierarchy.
        while (clazz != null) {
            Class[] implemeted = clazz.getInterfaces();
            List implementedList = Arrays.asList(implemeted);
            interfaces.addAll(implementedList);
            clazz = clazz.getSuperclass();
        }
    }

    /**
     * Get most common superclass for all given objects. 
     * @param objects The array of objects to consider.
     * @return Returns the superclass or <code>{@link Void}.class</code> for an empty array.
     */
    public Class getClass(Object[] objects) {
        Class clazz = null;
        boolean found = false;
        if (objects == null || objects.length == 0) {
            clazz = Void.class;
        } else {
            while (!found) {
                for (int i = 0; i < objects.length; i++) {
                    found = true;
                    if (objects[i] != null) {
                        Class currentClazz = objects[i].getClass();
                        if(clazz == null) {
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
