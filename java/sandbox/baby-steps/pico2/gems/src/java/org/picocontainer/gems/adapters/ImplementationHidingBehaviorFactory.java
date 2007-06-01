package org.picocontainer.gems.adapters;

import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentCharacteristic;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.adapters.AbstractBehaviorFactory;
import org.picocontainer.adapters.AnyInjectionFactory;

public class ImplementationHidingBehaviorFactory extends AbstractBehaviorFactory {

    public ImplementationHidingBehaviorFactory() {
        forThis(new AnyInjectionFactory());
    }

    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, ComponentCharacteristic componentCharacteristic, Object componentKey, Class componentImplementation, Parameter... parameters)
            throws PicoIntrospectionException, NotConcreteRegistrationException {

        ComponentAdapter componentAdapter = super.createComponentAdapter(componentMonitor, lifecycleStrategy, componentCharacteristic, componentKey, componentImplementation, parameters);
        return new ImplementationHidingBehaviorAdapter(componentAdapter);
    }
}