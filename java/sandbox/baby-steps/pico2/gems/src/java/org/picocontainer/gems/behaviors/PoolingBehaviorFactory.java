package org.picocontainer.gems.behaviors;

import org.picocontainer.behaviors.AbstractBehaviorFactory;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.ComponentCharacteristic;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;

public class PoolingBehaviorFactory extends AbstractBehaviorFactory {

    private final PoolingBehavior.Context poolContext;

    public PoolingBehaviorFactory(PoolingBehavior.Context poolContext) {
        this.poolContext = poolContext;
    }

    public PoolingBehaviorFactory() {
        poolContext = new PoolingBehavior.DefaultContext();
    }

    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, ComponentCharacteristic componentCharacteristic, Object componentKey, Class componentImplementation, Parameter... parameters)
            throws PicoCompositionException {
        ComponentAdapter componentAdapter = super.createComponentAdapter(componentMonitor, lifecycleStrategy, componentCharacteristic, componentKey, componentImplementation, parameters);
        PoolingBehavior behavior = new PoolingBehavior(componentAdapter, poolContext);
        //TODO
        //ComponentCharacteristics.HIDE.setProcessedIn(componentCharacteristic);
        return behavior;
    }
}
