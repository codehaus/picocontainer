package org.picocontainer.extras;


import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.NoSatisfiableConstructorsException;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class SynchronizedComponentAdapter implements ComponentAdapter {
    private ComponentAdapter delegate;

    public SynchronizedComponentAdapter(ComponentAdapter delegate) {
        this.delegate = delegate;
    }

    public synchronized Object getComponentKey() {
        return delegate.getComponentKey();
    }

    public synchronized Class getComponentImplementation() {
        return delegate.getComponentImplementation();
    }

    public synchronized Object getComponentInstance(MutablePicoContainer dependencyContainer) throws PicoInitializationException, PicoIntrospectionException {
        if(dependencyContainer != null) {
            dependencyContainer.registerOrderedComponentAdapter(this);
        }
        Object instance = delegate.getComponentInstance(dependencyContainer);
        if(dependencyContainer != null) {
            dependencyContainer.addOrderedComponentAdapter(this);
        }
        return instance;
    }

    public synchronized void verify(PicoContainer picoContainer) throws NoSatisfiableConstructorsException {
        delegate.verify(picoContainer);
    }
}
