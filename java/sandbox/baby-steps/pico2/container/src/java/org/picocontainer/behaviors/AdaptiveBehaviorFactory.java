package org.picocontainer.behaviors;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.ComponentCharacteristic;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.ComponentCharacteristics;
import org.picocontainer.ComponentFactory;
import org.picocontainer.BehaviorFactory;
import org.picocontainer.injectors.AdaptiveInjectionFactory;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class AdaptiveBehaviorFactory implements ComponentFactory, Serializable {

    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor,
                                                   LifecycleStrategy lifecycleStrategy,
                                                   ComponentCharacteristic componentCharacteristic,
                                                   Object componentKey,
                                                   Class componentImplementation,
                                                   Parameter... parameters) throws PicoCompositionException {
        List<ComponentFactory> list = new ArrayList<ComponentFactory>();
        list.add(new AdaptiveInjectionFactory());
        if (ComponentCharacteristics.THREAD_SAFE.characterizes(componentCharacteristic)) {
            list.add(new SynchronizedBehaviorFactory());
        }
        if (ComponentCharacteristics.HIDE.characterizes(componentCharacteristic)) {
            list.add(new ImplementationHidingBehaviorFactory());
        }
        if (ComponentCharacteristics.CACHE.characterizes(componentCharacteristic)) {
            list.add(new CachingBehaviorFactory());
        }
        ComponentFactory lastFactory = null;
        for (ComponentFactory componentFactory : list) {
            if (lastFactory != null && componentFactory instanceof BehaviorFactory) {
                ((BehaviorFactory)componentFactory).forThis(lastFactory);
            }
            lastFactory = componentFactory;
        }

        return lastFactory.createComponentAdapter(componentMonitor,
                                                  lifecycleStrategy,
                                                  componentCharacteristic,
                                                  componentKey,
                                                  componentImplementation,
                                                  parameters);
    }
}
