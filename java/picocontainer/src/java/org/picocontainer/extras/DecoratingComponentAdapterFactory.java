package org.picocontainer.extras;

import org.picocontainer.internals.ComponentAdapterFactory;
import org.picocontainer.internals.ComponentAdapter;
import org.picocontainer.internals.Parameter;
import org.picocontainer.PicoIntrospectionException;

public class DecoratingComponentAdapterFactory implements ComponentAdapterFactory {
    private final ComponentAdapterFactory delegate;

    public DecoratingComponentAdapterFactory(ComponentAdapterFactory delegate) {
        this.delegate = delegate;
    }

    public ComponentAdapter createComponentAdapter(Object componentKey,
                                                   Class componentImplementation,
                                                   Parameter[] parameters) throws PicoIntrospectionException {
        return delegate.createComponentAdapter(componentKey, componentImplementation, parameters);
    }
}
