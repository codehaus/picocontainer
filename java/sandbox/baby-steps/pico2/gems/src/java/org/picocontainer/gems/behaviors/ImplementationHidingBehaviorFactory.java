package org.picocontainer.gems.behaviors;

import org.picocontainer.LifecycleStrategy;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentCharacteristic;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.ComponentCharacteristics;
import org.picocontainer.behaviors.AbstractBehaviorFactory;

public class ImplementationHidingBehaviorFactory extends AbstractBehaviorFactory {

    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, ComponentCharacteristic componentCharacteristic, Object componentKey, Class componentImplementation, Parameter... parameters)
            throws PicoCompositionException {
        ComponentAdapter componentAdapter = super.createComponentAdapter(componentMonitor, lifecycleStrategy, componentCharacteristic, componentKey, componentImplementation, parameters);
        ImplementationHidingBehavior behavior = new ImplementationHidingBehavior(componentAdapter);
        ComponentCharacteristics.HIDE.setProcessedIn(componentCharacteristic);
        return behavior;
    }
}