package org.picocontainer.defaults;

import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.extras.ComponentMulticasterFactory;

import java.util.Iterator;
import java.util.List;

/**
 * A default implementation of the {@link ComponentMulticasterPicoAdapter} interface.
 *
 * @author Aslak Helles&oslash;y
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Revision$
 */
public class DefaultComponentMulticasterPicoAdapter implements ComponentMulticasterPicoAdapter {
    private PicoContainer container;

    private ComponentMulticasterFactory factory;

    /**
     * Creates an adapter
     * 
     * @param container the container.
     */
    public DefaultComponentMulticasterPicoAdapter(PicoContainer container) {
        this(container, new DefaultComponentMulticasterFactory());
    }

    /**
     * Creates an adapter
     * 
     * @param container the container.
     * @param factory the multicaster factory.
     */
    public DefaultComponentMulticasterPicoAdapter(PicoContainer container, ComponentMulticasterFactory factory) {
        this.container = container;
        this.factory = factory;
    }

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
     */
    public Object getComponentMulticaster(boolean callInInstantiationOrder, boolean callUnmanagedComponents)
            throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {

        List componentsToMulticast = container.getComponentInstances();
        if (!callUnmanagedComponents) {
            for (Iterator iterator = container.getUnmanagedComponentInstances().iterator(); iterator.hasNext();) {
                componentsToMulticast.remove(iterator.next());
            }
        }
        return factory.createComponentMulticaster(getClass().getClassLoader(), componentsToMulticast, callInInstantiationOrder, new MulticastInvoker());
    }

    /**
     * {@inheritDoc}
     */
    public PicoContainer getPicoContainer() {
        return container;
    }
}
