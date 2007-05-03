package org.picocontainer;

import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.componentadapters.AnyInjectionComponentAdapterFactory;
import org.picocontainer.componentadapters.ImplementationHidingComponentAdapterFactory;
import org.picocontainer.defaults.LifecycleStrategy;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.lifecycle.StartableLifecycleStrategy;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.lifecycle.ReflectionLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.monitors.ConsoleComponentMonitor;
import org.picocontainer.alternatives.EmptyPicoContainer;

public class PicoBuilder {

    private PicoContainer parentContainer;

    public PicoBuilder(PicoContainer parentContainer) {
        if (parentContainer == null) {
            throw new NullPointerException("parentContainer class cannot be null");
        }
        this.parentContainer = parentContainer;
    }

    public PicoBuilder() {
        parentContainer = new EmptyPicoContainer();
    }

    private Class headComponentAdapterFactory;
    private Class componentAdapterFactoryClass = AnyInjectionComponentAdapterFactory.class;
    private Class componentMonitorClass = NullComponentMonitor.class;
    private Class lifecycleStrategyClass = NullLifecycleStrategy.class;

    public PicoBuilder withStartableLifecycle() {
        lifecycleStrategyClass = StartableLifecycleStrategy.class;
        return this;
    }

    public PicoBuilder withReflectionLifecycle() {
        lifecycleStrategyClass = ReflectionLifecycleStrategy.class;
        return this;
    }

    public PicoBuilder withConsoleMonitor() {
        componentMonitorClass =  ConsoleComponentMonitor.class;
        return this;
    }

    public PicoBuilder withMonitor(Class cmClass) {
        if (cmClass == null) {
            throw new NullPointerException("monitor class cannot be null");
        }
        if (!ComponentMonitor.class.isAssignableFrom(cmClass)) {
            throw new AssignabilityRegistrationException(ComponentMonitor.class, cmClass);
        }
        componentMonitorClass = cmClass;
        return this;
    }


    public MutablePicoContainer build() {

        DefaultPicoContainer temp = new DefaultPicoContainer();

        temp.component(PicoContainer.class, parentContainer);
        temp.component(ComponentMonitor.class, componentMonitorClass);
        temp.component(LifecycleStrategy.class, lifecycleStrategyClass);
        if (headComponentAdapterFactory == null) {
            temp.component(ComponentAdapterFactory.class, componentAdapterFactoryClass);
        } else {
            DefaultPicoContainer temp2 = new DefaultPicoContainer(temp);
            temp2.component(ComponentAdapterFactory.class, componentAdapterFactoryClass);
            temp2.component("foo", headComponentAdapterFactory);
            temp.component(ComponentAdapterFactory.class, temp2.getComponent("foo"));
        }
        temp.component(MutablePicoContainer.class, DefaultPicoContainer.class);


        return (MutablePicoContainer) temp.getComponent(MutablePicoContainer.class);
    }

    public PicoBuilder withImplementationHiding() {
        headComponentAdapterFactory = ImplementationHidingComponentAdapterFactory.class;
        return this;
    }

}
