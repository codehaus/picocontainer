package org.picocontainer.extras;


import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.NoSatisfiableConstructorsException;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class SynchronizedComponentAdapter extends DecoratingComponentAdapter {
    public SynchronizedComponentAdapter(ComponentAdapter delegate) {
        super(delegate);
    }

    public synchronized Object getComponentKey() {
        return super.getComponentKey();
    }

    public synchronized Class getComponentImplementation() {
        return super.getComponentImplementation();
    }

    public synchronized Object getComponentInstance(MutablePicoContainer dependencyContainer) throws PicoInitializationException, PicoIntrospectionException {
        return super.getComponentInstance(dependencyContainer);
    }

    public synchronized void verify(PicoContainer picoContainer) throws NoSatisfiableConstructorsException {
        super.verify(picoContainer);
    }
}
