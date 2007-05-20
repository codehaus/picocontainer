package org.picocontainer;

import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.adapters.AnyInjectionComponentAdapterFactory;
import org.picocontainer.adapters.ImplementationHidingComponentAdapterFactory;
import org.picocontainer.adapters.SetterInjectionComponentAdapterFactory;
import org.picocontainer.adapters.AnnotationInjectionComponentAdapterFactory;
import org.picocontainer.adapters.ConstructorInjectionComponentAdapterFactory;
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

    public PicoBuilder withLifecycle() {
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

        temp.addComponent(PicoContainer.class, parentContainer);
        temp.addComponent(ComponentMonitor.class, componentMonitorClass);
        temp.addComponent(LifecycleStrategy.class, lifecycleStrategyClass);
        if (headComponentAdapterFactory == null) {
            temp.addComponent(ComponentAdapterFactory.class, componentAdapterFactoryClass);
        } else {
            DefaultPicoContainer temp2 = new DefaultPicoContainer(temp);
            temp2.addComponent(ComponentAdapterFactory.class, componentAdapterFactoryClass);
            temp2.addComponent("foo", headComponentAdapterFactory);
            temp.addComponent(ComponentAdapterFactory.class, temp2.getComponent("foo"));
        }
        temp.addComponent(MutablePicoContainer.class, DefaultPicoContainer.class);


        return (MutablePicoContainer) temp.getComponent(MutablePicoContainer.class);
    }

    public PicoBuilder withHiddenImplementations() {
        headComponentAdapterFactory = ImplementationHidingComponentAdapterFactory.class;
        return this;
    }

    public PicoBuilder withSetterInjection() {
        headComponentAdapterFactory = SetterInjectionComponentAdapterFactory.class;
        return this;
    }

    public PicoBuilder withAnnotationInjection() {
        headComponentAdapterFactory = AnnotationInjectionComponentAdapterFactory.class;
        return this;
    }

    public PicoBuilder withConstructorInjection() {
        headComponentAdapterFactory = ConstructorInjectionComponentAdapterFactory.class;
        return this;
    }
}
