package org.picoextras.multicast;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.DefaultComponentMulticasterFactory;
import org.picocontainer.defaults.Invoker;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class MulticastingComponentAdapter implements ComponentAdapter {
    private final List componentInstances = new ArrayList();

    private final Object key;
    private final Class componentImplementation;
    private final Invoker invoker;

    public MulticastingComponentAdapter(Object key, Class componentImplementation, Invoker invoker) {
        this.key = key;
        this.componentImplementation = componentImplementation;
        this.invoker = invoker;
    }

    public Object getComponentKey() {
        return key;
    }

    public Class getComponentImplementation() {
        return componentImplementation;
    }

    public Object getComponentInstance(MutablePicoContainer dependencyContainer) throws PicoInitializationException, PicoIntrospectionException {
        return new DefaultComponentMulticasterFactory().createComponentMulticaster(
                getClass().getClassLoader(),
                componentInstances,
                true,
                invoker
        );
    }

    public void verify(PicoContainer picoContainer) throws PicoIntrospectionException {

    }

    public void addComponentInstance(Object componentInstance) {
        componentInstances.add(componentInstance);
    }
}
