package org.picocontainer.defaults;

import org.picocontainer.PicoContainer;

import java.util.Comparator;

/**
 * This comparator makes sure containers are always stacked at the end of the collection,
 * leaving the order of the others unchanged. This is needed in order to have proper
 * breadth-first traversal when calling lifecycle methods on container hierarchies.
 * 
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
class StackContainersAtEndComparator implements Comparator {
    public int compare(Object o1, Object o2) {
        Class c1 = o1.getClass();
        Class c2 = o2.getClass();
        if (PicoContainer.class.isAssignableFrom(c1)) {
            return 1;
        }
        if (PicoContainer.class.isAssignableFrom(c2)) {
            return -1;
        }
        return 0;
    }
}
