package org.picocontainer.extras;

import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class SynchronizedComponentAdapterFactory extends DecoratingComponentAdapterFactory {
    public SynchronizedComponentAdapterFactory(ComponentAdapterFactory delegate) {
        super(delegate);
    }

    public ComponentAdapter createComponentAdapter(Object componentKey, Class componentImplementation, Parameter[] parameters) {
        return new SynchronizedComponentAdapter(super.createComponentAdapter(componentKey, componentImplementation, parameters));
    }
}
