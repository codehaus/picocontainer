package org.picocontainer;

import org.picocontainer.injectors.ConstructorInjectionFactory;
import static org.picocontainer.behaviors.Behaviors.caching;
import static org.picocontainer.behaviors.Behaviors.implHiding;
import org.picocontainer.behaviors.SynchronizedBehaviorFactory;
import org.picocontainer.containers.EmptyPicoContainer;
import org.picocontainer.containers.TransientPicoContainer;
import org.picocontainer.DefaultPicoContainer;
import static org.picocontainer.injectors.Injectors.ADI;
import static org.picocontainer.injectors.Injectors.CDI;
import static org.picocontainer.injectors.Injectors.SDI;
import static org.picocontainer.injectors.Injectors.anyDI;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.lifecycle.ReflectionLifecycleStrategy;
import org.picocontainer.lifecycle.StartableLifecycleStrategy;
import org.picocontainer.monitors.ConsoleComponentMonitor;
import org.picocontainer.monitors.NullComponentMonitor;

import java.util.Stack;

public class PicoBuilder {

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
        this(parentContainer, anyDI());
    }

    public PicoBuilder(InjectionFactory injectionType) {
        this(new EmptyPicoContainer(), injectionType);
    }

    public PicoBuilder() {
        this(new EmptyPicoContainer(), anyDI());
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
            throw new ClassCastException(cmClass.getName() + " is not a " + ComponentMonitor.class.getName());

        }
        componentMonitorClass = cmClass;
        componentMonitor = null;
        return this;
    }


    public MutablePicoContainer build() {

        DefaultPicoContainer temp = new TransientPicoContainer();
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
        cafs.push(implHiding());
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
        cafs.push(caching());
        return this;  
    }

    public PicoBuilder withComponentAdapterFactory(Class cafClass) {
        if (cafClass == null) {
            throw new NullPointerException("CAF class cannot be null");
        }
        if (!ComponentFactory.class.isAssignableFrom(cafClass)) {
            throw new ClassCastException(cafClass.getName() + " is not a " + ComponentFactory.class.getName());

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
