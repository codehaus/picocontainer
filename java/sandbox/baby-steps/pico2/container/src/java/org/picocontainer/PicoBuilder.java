package org.picocontainer;

import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ComponentFactory;
import org.picocontainer.adapters.AnyInjectionFactory;
import org.picocontainer.adapters.ImplementationHidingBehaviorFactory;
import org.picocontainer.adapters.SetterInjectionFactory;
import org.picocontainer.adapters.AnnotationInjectionFactory;
import org.picocontainer.adapters.ConstructorInjectionFactory;
import org.picocontainer.adapters.InjectionFactory;
import org.picocontainer.adapters.CachingBehaviorFactory;
import org.picocontainer.adapters.SynchronizedBehaviorFactory;
import org.picocontainer.adapters.BehaviorFactory;
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

    public static InjectionFactory ANY_DI() {
        return new AnyInjectionFactory();
    }

    public static InjectionFactory SDI() {
        return new SetterInjectionFactory();
    }

    public static InjectionFactory CDI() {
        return new ConstructorInjectionFactory();
    }

    public static InjectionFactory ADI() {
        return new AnnotationInjectionFactory();
    }

    public static BehaviorFactory IMPL_HIDING() {
        return new ImplementationHidingBehaviorFactory();
    }
    
    public static BehaviorFactory CACHING() {
        return new CachingBehaviorFactory();
    }


    private PicoContainer parentContainer;
    private Class mpcClass = DefaultPicoContainer.class;
    private ComponentMonitor componentMonitor;

    public PicoBuilder(PicoContainer parentContainer, InjectionFactory injectionType) {
        this.injectionType = injectionType;
        if (parentContainer == null) {
            throw new NullPointerException("parentContainer class cannot be null");
        }
        this.parentContainer = parentContainer;
    }

    public PicoBuilder(PicoContainer parentContainer) {
        this(parentContainer, ANY_DI());
    }

    public PicoBuilder(InjectionFactory injectionType) {
        this(new EmptyPicoContainer(), injectionType);
    }

    public PicoBuilder() {
        this(new EmptyPicoContainer(), ANY_DI());
    }

    private final Stack cafs = new Stack();

    private InjectionFactory injectionType;

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

        ComponentFactory lastCaf = injectionType;
        while (!cafs.empty()) {
            Object caf = cafs.pop();
            DefaultPicoContainer temp2 = new DefaultPicoContainer(new ConstructorInjectionFactory(), NullLifecycleStrategy.getInstance(), new EmptyPicoContainer());
            temp2.addComponent("caf", caf);
            if (lastCaf != null) {
                temp2.addComponent(ComponentFactory.class, lastCaf);
            }
            ComponentFactory penultimateCaf = lastCaf;
            lastCaf = (ComponentFactory) temp2.getComponent("caf");
            if (lastCaf instanceof BehaviorFactory) {
                ((BehaviorFactory) lastCaf).forThis(penultimateCaf);
            }
        }

        temp.addComponent(ComponentFactory.class, lastCaf);
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
        if (!ComponentFactory.class.isAssignableFrom(cafClass)) {
            throw new AssignabilityRegistrationException(ComponentFactory.class, cafClass);
        }
        cafs.push(cafClass);
        return this;
    }

    public PicoBuilder withComponentAdapterFactory(ComponentFactory caf) {
        if (caf == null) {
            throw new NullPointerException("CAF cannot be null");
        }
        cafs.push(caf);
        return this;
    }

    public PicoBuilder withThreadSafety() {
        cafs.push(SynchronizedBehaviorFactory.class);
        return this;
    }


    public PicoBuilder withBehaviors(BehaviorFactory... factories) {
        for (ComponentFactory caf : factories) {
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
