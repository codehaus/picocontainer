package org.picoextras.multicast;

import org.picocontainer.*;
import org.picocontainer.defaults.DefaultComponentMulticasterFactory;
import org.picocontainer.defaults.Invoker;
import org.picocontainer.defaults.AbstractComponentAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class MulticastingComponentAdapter extends AbstractComponentAdapter {
    private final List componentInstances = new ArrayList();

    private final Invoker invoker;

    public MulticastingComponentAdapter(Object key, Class componentImplementation, Invoker invoker) {
        super(key, componentImplementation);
        this.invoker = invoker;
    }

    public Object getComponentInstance() throws PicoInitializationException, PicoIntrospectionException {
        return new DefaultComponentMulticasterFactory().createComponentMulticaster(
                getClass().getClassLoader(),
                componentInstances,
                true,
                invoker
        );
    }

    public void verify() throws PicoIntrospectionException {
    }

    public void addComponentInstance(Object componentInstance) {
        componentInstances.add(componentInstance);
    }
}
