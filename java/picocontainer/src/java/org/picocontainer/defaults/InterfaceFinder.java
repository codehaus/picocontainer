package org.picocontainer.defaults;

import java.util.*;
import java.lang.reflect.Method;

/**
 * Helper class for finding interfaces of objects.
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class InterfaceFinder {
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

    public Class[] getInterfaces(Object object) {
        return getInterfaces(Collections.singletonList(object));
    }

    /**
     * Get all the interfaces implemented by a list of objects.
     * @return an array of interfaces.
     */
    public Class[] getInterfaces(List objects) {
        Set interfaces = new HashSet();
        for (Iterator iterator = objects.iterator(); iterator.hasNext();) {
            Object o = iterator.next();
            Class clazz = o.getClass();
            getInterfaces(clazz, interfaces);
        }

        return (Class[]) interfaces.toArray(new Class[interfaces.size()]);
    }

    public Class[] getInterfaces(Class clazz) {
        Set interfaces = new HashSet();
        getInterfaces(clazz, interfaces);
        return (Class[]) interfaces.toArray(new Class[interfaces.size()]);
    }

    private void getInterfaces(Class clazz, Set interfaces) {
        // Strangely enough Class.getInterfaces() does not include the interfaces
        // implemented by superclasses. So we must loop up the hierarchy.
        while (clazz != null) {
            Class[] implemeted = clazz.getInterfaces();
            List implementedList = Arrays.asList(implemeted);
            interfaces.addAll(implementedList);
            clazz = clazz.getSuperclass();
        }
    }
}
