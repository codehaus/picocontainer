package org.picocontainer.defaults;


import org.picocontainer.*;
import org.picocontainer.defaults.UnsatisfiableDependenciesException;
import org.picocontainer.defaults.DecoratingComponentAdapter;

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

    public synchronized Object getComponentInstance() throws PicoInitializationException, PicoIntrospectionException {
        return super.getComponentInstance();
    }

    public synchronized void verify() throws UnsatisfiableDependenciesException {
        super.verify();
    }
}
