package org.picocontainer.gems.adapters;

import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentCharacteristic;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.adapters.DecoratingComponentAdapterFactory;
import org.picocontainer.adapters.AnyInjectionComponentAdapterFactory;

public class ImplementationHidingComponentAdapterFactory extends DecoratingComponentAdapterFactory {

    public ImplementationHidingComponentAdapterFactory() {
        this(new AnyInjectionComponentAdapterFactory());
    }

    public ImplementationHidingComponentAdapterFactory(ComponentAdapterFactory delegate) {
        super(delegate);
    }

    public ComponentAdapter createComponentAdapter(ComponentCharacteristic registerationCharacteristic, Object componentKey, Class componentImplementation, Parameter... parameters)
            throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {

        ComponentAdapter componentAdapter = super.createComponentAdapter(registerationCharacteristic, componentKey, componentImplementation, parameters);
        return new ImplementationHidingComponentAdapter(componentAdapter);
    }
}