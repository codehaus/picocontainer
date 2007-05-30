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
import org.picocontainer.adapters.DecoratingComponentAdapterFactory;
import org.picocontainer.defaults.LifecycleStrategy;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.lifecycle.StartableLifecycleStrategy;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.lifecycle.ReflectionLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.monitors.ConsoleComponentMonitor;
import org.picocontainer.alternatives.EmptyPicoContainer;

import java.util.Stack;

import com.sun.tools.doclets.internal.toolkit.builders.AbstractBuilder;

public class PicoBuilder {

    public static InstantiatingComponentAdapterFactory ANY_DI() {
        return new AnyInjectionComponentAdapterFactory();
    }

    public static InstantiatingComponentAdapterFactory SDI() {
        return new SetterInjectionComponentAdapterFactory();
    }

    public static InstantiatingComponentAdapterFactory CDI() {
        return new ConstructorInjectionComponentAdapterFactory();
    }

    public static InstantiatingComponentAdapterFactory ADI() {
        return new AnnotationInjectionComponentAdapterFactory();
    }

    public static DecoratingComponentAdapterFactory IMPL_HIDING() {
        return new ImplementationHidingComponentAdapterFactory();
    }
    
    public static DecoratingComponentAdapterFactory CACHING() {
        return new CachingComponentAdapterFactory();
    }


    private PicoContainer parentContainer;
    private Class mpcClass = DefaultPicoContainer.class;
    private ComponentMonitor componentMonitor;

    public PicoBuilder(PicoContainer parentContainer, InstantiatingComponentAdapterFactory injectionType) {
        this.injectionType = injectionType;
        if (parentContainer == null) {
            throw new NullPointerException("parentContainer class cannot be null");
        }
        this.parentContainer = parentContainer;
    }

    public PicoBuilder(PicoContainer parentContainer) {
        this(parentContainer, ANY_DI());
    }

    public PicoBuilder(InstantiatingComponentAdapterFactory injectionType) {
        this(new EmptyPicoContainer(), injectionType);
    }

    public PicoBuilder() {
        this(new EmptyPicoContainer(), ANY_DI());
    }

    private final Stack cafs = new Stack();

    private InstantiatingComponentAdapterFactory injectionType;

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
        componentMonitor = null;
        return this;
    }


    public MutablePicoContainer build() {

        DefaultPicoContainer temp = new DefaultPicoContainer();
        temp.addComponent(PicoContainer.class, parentContainer);

        ComponentAdapterFactory lastCaf = injectionType;
        while (!cafs.empty()) {
            Object caf = cafs.pop();
            DefaultPicoContainer temp2 = new DefaultPicoContainer(new ConstructorInjectionComponentAdapterFactory(), NullLifecycleStrategy.getInstance(), new EmptyPicoContainer());
            temp2.addComponent("caf", caf);
            if (lastCaf != null) {
                temp2.addComponent(ComponentAdapterFactory.class, lastCaf);
            }
            ComponentAdapterFactory penultimateCaf = lastCaf;
            lastCaf = (ComponentAdapterFactory) temp2.getComponent("caf");
            if (lastCaf instanceof DecoratingComponentAdapterFactory) {
                ((DecoratingComponentAdapterFactory) lastCaf).forThis(penultimateCaf);
            }
        }

        temp.addComponent(ComponentAdapterFactory.class, lastCaf);
        if (componentMonitorClass == null) {
            temp.addComponent(ComponentMonitor.class, componentMonitor);
        } else {
            temp.addComponent(ComponentMonitor.class, componentMonitorClass);
        }
        temp.addComponent(LifecycleStrategy.class, lifecycleStrategyClass);
        temp.addComponent("mpc", mpcClass);


        return (MutablePicoContainer) temp.getComponent("mpc");
    }

    public PicoBuilder withHiddenImplementations() {
        cafs.push(IMPL_HIDING());
        return this;
    }

    public PicoBuilder withSetterInjection() {
        injectionType = SDI();
        return this;
    }

    public PicoBuilder withAnnotationInjection() {
        injectionType = ADI();
        return this;
    }

    public PicoBuilder withConstructorInjection() {
        injectionType = CDI();
        return this;
    }

    public PicoBuilder withCaching() {
        cafs.push(CACHING());
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

    public PicoBuilder withComponentAdapterFactory(ComponentAdapterFactory caf) {
        if (caf == null) {
            throw new NullPointerException("CAF cannot be null");
        }
        cafs.push(caf);
        return this;
    }

    public PicoBuilder withThreadSafety() {
        cafs.push(SynchronizedComponentAdapterFactory.class);
        return this;
    }


    public PicoBuilder withBehaviors(DecoratingComponentAdapterFactory... factories) {
        for (ComponentAdapterFactory caf : factories) {
            cafs.push(caf);
        }
        return this;
    }


    public PicoBuilder thisMutablePicoContainer(Class containerClass) {
        mpcClass = containerClass;
        return this;
    }

    public PicoBuilder withMonitor(ComponentMonitor componentMonitor) {
        this.componentMonitor = componentMonitor;
        componentMonitorClass = null;
        return this;
    }
}
