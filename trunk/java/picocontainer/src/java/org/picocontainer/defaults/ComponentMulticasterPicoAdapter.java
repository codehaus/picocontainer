package org.picocontainer.defaults;

import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;

/**
 * Adds component multicasting capabilities to a pico adapter.
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public interface ComponentMulticasterPicoAdapter {
    /**
     * Returns the PicoContainer.
     * 
     * @return the container.
     */
    PicoContainer getPicoContainer();

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
     * @param callInInstantiationOrder whether or not to call the method in the order of instantiation,
     *    which depends on the components' inter-dependencies.
     * @param callUnmanagedComponents whether or not to multicast to components that are not managed
     *    by this container.
     * @return a multicaster object.
     */
    public Object getComponentMulticaster(boolean callInInstantiationOrder, boolean callUnmanagedComponents) throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException;
}
