package org.picocontainer.defaults;

import java.util.*;

/**
 * Helper class for finding interfaces of objects.
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class InterfaceFinder {
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
            // Strangely enough Class.getInterfaces() does not include the interfaces
            // implemented by superclasses. So we must loop up the hierarchy.
            while (clazz != null) {
                Class[] implemeted = clazz.getInterfaces();
                List implementedList = Arrays.asList(implemeted);
                interfaces.addAll(implementedList);
                clazz = clazz.getSuperclass();
            }
        }

        Class[] result = (Class[]) interfaces.toArray(new Class[interfaces.size()]);
        return result;
    }
}
