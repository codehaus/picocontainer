package org.picocontainer.extras;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.NotConcreteRegistrationException;

public class DecoratingComponentAdapterFactory implements ComponentAdapterFactory {
    private final ComponentAdapterFactory delegate;

    public DecoratingComponentAdapterFactory(ComponentAdapterFactory delegate) {
        this.delegate = delegate;
    }

    public ComponentAdapter createComponentAdapter(Object componentKey,
                                                   Class componentImplementation,
                                                   Parameter[] parameters) throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        return delegate.createComponentAdapter(componentKey, componentImplementation, parameters);
    }
}
