package org.picocontainer.defaults;

import org.picocontainer.PicoException;
import org.picocontainer.PicoContainer;

/**
 * Adds component multicasting capabilities to a pico adapter.
 *
 * @author Aslak Helles&oslash;y
 * @author Chris Stevenson
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Revision$
 */
public interface ComponentMulticasterAdapter {

    /**
     * Returns an object (in fact, a dynamic proxy) that implements the union
     * of all the interfaces of the currently registered components.
     * <p>
     * Casting this object to any of those interfaces and then calling a method
     * on it will result in that call being multicast to all the components implementing
     * that given interface.
     * <p>
     * This is a simple yet extremely powerful way to handle lifecycle of components.
     * Component writers can invent their own lifecycle interfaces, and then use the multicaster
     * to invoke the method in one go.
     *
     * @param picoContainer the container containing the components to multicast to.
     * @return a multicaster object.
     */
    public Object getComponentMulticaster(PicoContainer picoContainer);
}
