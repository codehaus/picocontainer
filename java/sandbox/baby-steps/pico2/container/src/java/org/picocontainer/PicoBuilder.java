package org.picocontainer;

import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.adapters.AnyInjectionComponentAdapterFactory;
import org.picocontainer.adapters.ImplementationHidingComponentAdapterFactory;
import org.picocontainer.adapters.SetterInjectionComponentAdapterFactory;
import org.picocontainer.adapters.AnnotationInjectionComponentAdapterFactory;
import org.picocontainer.adapters.ConstructorInjectionComponentAdapterFactory;
import org.picocontainer.adapters.InstantiatingComponentAdapterFactory;
import org.picocontainer.adapters.CachingComponentAdapterFactory;
import org.picocontainer.adapters.SynchronizedComponentAdapterFactory;
import org.picocontainer.defaults.LifecycleStrategy;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.lifecycle.StartableLifecycleStrategy;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.lifecycle.ReflectionLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.monitors.ConsoleComponentMonitor;
import org.picocontainer.alternatives.EmptyPicoContainer;

import java.util.Stack;

public class PicoBuilder {

    private PicoContainer parentContainer;
    private Class mpcClass = DefaultPicoContainer.class;

    public PicoBuilder(PicoContainer parentContainer) {
        if (parentContainer == null) {
            throw new NullPointerException("parentContainer class cannot be null");
        }
        this.parentContainer = parentContainer;
    }

    public PicoBuilder() {
        parentContainer = new EmptyPicoContainer();
    }

    //private Class headComponentAdapterFactory;
    //private Class componentAdapterFactoryClass = AnyInjectionComponentAdapterFactory.class;

    Stack<Class> cafs = new Stack<Class>();

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

        ComponentAdapterFactory lastCaf = null;
        if (cafs.empty() || !InstantiatingComponentAdapterFactory.class.isAssignableFrom(cafs.peek())) {
            cafs.push(AnyInjectionComponentAdapterFactory.class);
        }
        while (!cafs.empty()) {
            Class caf = cafs.pop();
            DefaultPicoContainer temp2 = new DefaultPicoContainer(new ConstructorInjectionComponentAdapterFactory(), NullLifecycleStrategy.getInstance(), new EmptyPicoContainer());
            temp2.addComponent("caf", caf);
            if (lastCaf != null) {
                temp2.addComponent(ComponentAdapterFactory.class, lastCaf);
            }
            lastCaf = (ComponentAdapterFactory) temp2.getComponent("caf");
            
        }
        temp.addComponent(ComponentAdapterFactory.class, lastCaf);
        temp.addComponent(ComponentMonitor.class, componentMonitorClass);
        temp.addComponent(LifecycleStrategy.class, lifecycleStrategyClass);
        temp.addComponent("mpc", mpcClass);


        return (MutablePicoContainer) temp.getComponent("mpc");
    }

    public PicoBuilder withHiddenImplementations() {
        cafs.push(ImplementationHidingComponentAdapterFactory.class);
        return this;
    }

    public PicoBuilder withSetterInjection() {
        cafs.push(SetterInjectionComponentAdapterFactory.class);
        return this;
    }

    public PicoBuilder withAnnotationInjection() {
        cafs.push(AnnotationInjectionComponentAdapterFactory.class);
        return this;
    }

    public PicoBuilder withConstructorInjection() {
        cafs.push(ConstructorInjectionComponentAdapterFactory.class);
        return this;
    }

    public PicoBuilder withCaching() {
        cafs.push(CachingComponentAdapterFactory.class);
        return this;  
    }

    public PicoBuilder withComponentAdapterFactory(Class cafClass) {
        if (cafClass == null) {
            throw new NullPointerException("CAF class cannot be null");
        }
        if (!ComponentAdapterFactory.class.isAssignableFrom(cafClass)) {
            throw new AssignabilityRegistrationException(ComponentAdapterFactory.class, cafClass);
        }
        cafs.push(cafClass);
        return this;
    }

    public PicoBuilder withThreadSafety() {
        cafs.push(SynchronizedComponentAdapterFactory.class);
        return this;
    }


    public PicoBuilder thisMutablePicoContainer(Class containerClass) {
        mpcClass = containerClass;
        return this;
    }
}
