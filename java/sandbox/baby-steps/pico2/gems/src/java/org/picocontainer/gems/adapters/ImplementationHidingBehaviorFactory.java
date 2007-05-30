package org.picocontainer.gems.adapters;

import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.defaults.LifecycleStrategy;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentCharacteristic;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.adapters.AbstractBehaviorDecoratorFactory;
import org.picocontainer.adapters.AnyInjectionFactory;

public class ImplementationHidingBehaviorFactory extends AbstractBehaviorDecoratorFactory {

    public ImplementationHidingBehaviorFactory() {
        forThis(new AnyInjectionFactory());
    }

    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, ComponentCharacteristic registerationCharacteristic, Object componentKey, Class componentImplementation, Parameter... parameters)
            throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {

        ComponentAdapter componentAdapter = super.createComponentAdapter(componentMonitor, lifecycleStrategy, registerationCharacteristic, componentKey, componentImplementation, parameters);
        return new ImplementationHidingBehaviorAdapter(componentAdapter);
    }
}