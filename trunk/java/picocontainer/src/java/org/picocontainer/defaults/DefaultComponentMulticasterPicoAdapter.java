package org.picocontainer.defaults;

import java.util.Iterator;
import java.util.List;

import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.extras.ComponentMulticasterFactory;
import org.picocontainer.extras.ComponentMulticasterPicoAdapter;

/**
 * A default implementation of the {@link ComponentMulticasterPicoAdapter} interface.
 *
 * <p>Created on Dec 18, 2003</p>
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id$
 */
public class DefaultComponentMulticasterPicoAdapter implements ComponentMulticasterPicoAdapter
{
    private PicoContainer container;
    
    private ComponentMulticasterFactory factory;

    /**
     * Creates an adapter
     * 
     * @param container the container.
     * @param factory the multicaster factory.
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
     * {@inheritDoc}
     */
    public Object getComponentMulticaster(boolean callInInstantiationOrder, boolean callUnmanagedComponents)
        throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {

        List componentsToMulticast = container.getComponentInstances();
        if (!callUnmanagedComponents) {
           for (Iterator iterator = container.getUnmanagedComponentInstances().iterator(); iterator.hasNext();) {
               componentsToMulticast.remove(iterator.next());
           }
        }
        return factory.createComponentMulticaster(getClass().getClassLoader(), componentsToMulticast, callInInstantiationOrder);        
    }

    /**
     * {@inheritDoc}
     */
    public PicoContainer getPicoContainer() {
        return container;
    }
}
