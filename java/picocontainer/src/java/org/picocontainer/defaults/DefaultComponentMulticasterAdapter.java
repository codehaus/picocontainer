package org.picocontainer.defaults;

import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.extras.ComponentMulticasterFactory;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/**
 * A default implementation of the {@link ComponentMulticasterAdapter} interface.
 *
 * @author Aslak Helles&oslash;y
 * @author Chris Stevenson
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Revision$
 */
public class DefaultComponentMulticasterAdapter implements ComponentMulticasterAdapter, Serializable {
    private final ComponentMulticasterFactory factory;

    public DefaultComponentMulticasterAdapter() {
        this(new DefaultComponentMulticasterFactory());
    }

    public DefaultComponentMulticasterAdapter(ComponentMulticasterFactory factory) {
        this.factory = factory;
    }

    private Object getComponentMulticaster(PicoContainer picoContainer, boolean callInInstantiationOrder, boolean callUnmanagedComponents) throws PicoException {
        List componentsToMulticast = picoContainer.getComponentInstances();
        return factory.createComponentMulticaster(getClass().getClassLoader(), componentsToMulticast, callInInstantiationOrder, new MulticastInvoker());
    }

    public Object getComponentMulticaster(PicoContainer picoContainer) throws PicoException {
        return getComponentMulticaster(picoContainer,true,false);
    }
}
