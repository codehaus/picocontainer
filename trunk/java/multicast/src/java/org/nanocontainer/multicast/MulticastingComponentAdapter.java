package org.picoextras.multicast;

import org.picocontainer.*;
import org.picocontainer.defaults.DefaultComponentMulticasterFactory;
import org.picocontainer.defaults.Invoker;
import org.picocontainer.defaults.AbstractComponentAdapter;
import org.picocontainer.defaults.InvocationInterceptor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class MulticastingComponentAdapter extends AbstractComponentAdapter {
    private final List componentInstances = new ArrayList();

    private final Invoker invoker;
    private InvocationInterceptor invocationInterceptor;

    public MulticastingComponentAdapter(Object key, Class componentImplementation, InvocationInterceptor invocationInterceptor, Invoker invoker) {
        super(key, componentImplementation);
        this.invocationInterceptor = invocationInterceptor;
        this.invoker = invoker;
    }

    public Object getComponentInstance() throws PicoInitializationException, PicoIntrospectionException {
        return new DefaultComponentMulticasterFactory().createComponentMulticaster(
                getClass().getClassLoader(),
                componentInstances,
                true,
                invocationInterceptor,
                invoker
        );
    }

    public void verify() throws PicoIntrospectionException {
    }

    public void addComponentInstance(Object componentInstance) {
        componentInstances.add(componentInstance);
    }
}
