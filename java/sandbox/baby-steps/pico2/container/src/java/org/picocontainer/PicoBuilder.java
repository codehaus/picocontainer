package org.picocontainer;

import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.componentadapters.CachingAndConstructorComponentAdapterFactory;
import org.picocontainer.componentadapters.ImplementationHidingComponentAdapterFactory;
import org.picocontainer.defaults.LifecycleStrategy;
import org.picocontainer.lifecycle.StartableLifecycleStrategy;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.lifecycle.ReflectionLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.monitors.ConsoleComponentMonitor;
import org.picocontainer.alternatives.EmptyPicoContainer;

public class PicoBuilder {

    private Class parent = EmptyPicoContainer.class;
    private Class headCaf;
    private Class caf = CachingAndConstructorComponentAdapterFactory.class;
    private Class componentMonitor = NullComponentMonitor.class;
    private Class lifecycleStrategy = NullLifecycleStrategy.class;

    public PicoBuilder withStartableLifecycle() {
        lifecycleStrategy = StartableLifecycleStrategy.class;
        return this;
    }

    public PicoBuilder withReflectionLifecycle() {
        lifecycleStrategy = ReflectionLifecycleStrategy.class;
        return this;
    }

    public PicoBuilder withConsoleMonitor() {
        componentMonitor =  ConsoleComponentMonitor.class;
        return this;
    }

    public MutablePicoContainer build() {

        DefaultPicoContainer temp = new DefaultPicoContainer();

        temp.registerComponent(PicoContainer.class, parent);        
        temp.registerComponent(ComponentMonitor.class, componentMonitor);
        temp.registerComponent(LifecycleStrategy.class, lifecycleStrategy);
        if (headCaf == null) {
            temp.registerComponent(ComponentAdapterFactory.class, caf);
        } else {
            DefaultPicoContainer temp2 = new DefaultPicoContainer(temp);
            temp2.registerComponent(ComponentAdapterFactory.class, caf);
            temp2.registerComponent("foo", headCaf);
            temp.registerComponent(ComponentAdapterFactory.class, temp2.getComponent("foo"));
        }
        temp.registerComponent(MutablePicoContainer.class, DefaultPicoContainer.class);


        return (MutablePicoContainer) temp.getComponent(MutablePicoContainer.class);
    }

    public PicoBuilder withImplementationHiding() {
        headCaf = ImplementationHidingComponentAdapterFactory.class;
        return this;
    }
}
