package org.picocontainer.containers;

import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.injectors.ConstructorInjectionFactory;
import org.picocontainer.behaviors.CachingBehaviorFactory;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;

public class TransientPicoContainer extends DefaultPicoContainer {

    public TransientPicoContainer() {
        super(new CachingBehaviorFactory().forThis(new ConstructorInjectionFactory()), NullLifecycleStrategy.getInstance(), null, NullComponentMonitor.getInstance());
    }
}
