/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer;

import org.picocontainer.behaviors.Automating;
import org.picocontainer.behaviors.Locking;
import org.picocontainer.behaviors.PropertyApplying;
import org.picocontainer.behaviors.Synchronizing;
import org.picocontainer.containers.EmptyPicoContainer;
import org.picocontainer.containers.TransientPicoContainer;
import org.picocontainer.injectors.MethodInjection;
import org.picocontainer.lifecycle.JavaEE5LifecycleStrategy;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.lifecycle.ReflectionLifecycleStrategy;
import org.picocontainer.lifecycle.StartableLifecycleStrategy;
import org.picocontainer.monitors.ConsoleComponentMonitor;
import org.picocontainer.monitors.NullComponentMonitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static org.picocontainer.behaviors.Behaviors.caching;
import static org.picocontainer.behaviors.Behaviors.implementationHiding;
import static org.picocontainer.injectors.Injectors.CDI;
import static org.picocontainer.injectors.Injectors.SDI;
import static org.picocontainer.injectors.Injectors.adaptiveDI;
import static org.picocontainer.injectors.Injectors.annotatedFieldDI;
import static org.picocontainer.injectors.Injectors.annotatedMethodDI;
import static org.picocontainer.injectors.Injectors.namedMethod;

/**
 * Helps assembles the myriad items available to a picocontainer.
 * <p>Simple Example:</p>
 * <pre>
 * MutablePicoContainer mpc = new PicoBuilder()
 * &nbsp;&nbsp;.withCaching()
 * &nbsp;&nbsp;.withLifecycle()
 * &nbsp;&nbsp;.build();
 * </pre>
 * @author Paul Hammant
 */
public class PicoBuilder {

    private PicoContainer parentContainer;
    private Class<? extends MutablePicoContainer> mpcClass = DefaultPicoContainer.class;
    private ComponentMonitor componentMonitor;
    private List<Object> containerComps = new ArrayList<Object>();
    private boolean addChildToParent;
    private LifecycleStrategy lifecycleStrategy;
    private final Stack<Object> componentFactories = new Stack<Object>();
    private InjectionFactory injectionType;
    private Class<? extends ComponentMonitor> componentMonitorClass = NullComponentMonitor.class;
    private Class<? extends LifecycleStrategy> lifecycleStrategyClass = NullLifecycleStrategy.class;


    public PicoBuilder(PicoContainer parentContainer, InjectionFactory injectionType) {
        this.injectionType = injectionType;
        if (parentContainer != null) {
            this.parentContainer = parentContainer;
        } else {
            this.parentContainer = new EmptyPicoContainer();
        }
    }

    public PicoBuilder(PicoContainer parentContainer) {
        this(parentContainer, adaptiveDI());
    }

    public PicoBuilder(InjectionFactory injectionType) {
        this(new EmptyPicoContainer(), injectionType);
    }

    public PicoBuilder() {
        this(new EmptyPicoContainer(), adaptiveDI());
    }

    public PicoBuilder withLifecycle() {
        lifecycleStrategyClass = StartableLifecycleStrategy.class;
        lifecycleStrategy = null;
        return this;
    }

    public PicoBuilder withReflectionLifecycle() {
        lifecycleStrategyClass = ReflectionLifecycleStrategy.class;
        lifecycleStrategy = null;
        return this;
    }

    public PicoBuilder withLifecycle(Class<? extends LifecycleStrategy> lifecycleStrategyClass) {
        this.lifecycleStrategyClass = lifecycleStrategyClass;
        lifecycleStrategy = null;
        return this;
    }

    public PicoBuilder withJavaEE5Lifecycle() {
        this.lifecycleStrategyClass = JavaEE5LifecycleStrategy.class;
        lifecycleStrategy = null;
        return this;
    }

    public PicoBuilder withLifecycle(LifecycleStrategy lifecycleStrategy) {
        this.lifecycleStrategy = lifecycleStrategy;
        lifecycleStrategyClass = null;
        return this;
    }


    public PicoBuilder withConsoleMonitor() {
        componentMonitorClass =  ConsoleComponentMonitor.class;
        return this;
    }

    public PicoBuilder withMonitor(Class<? extends ComponentMonitor> cmClass) {
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

        DefaultPicoContainer tempContainer = new TransientPicoContainer();
        tempContainer.addComponent(PicoContainer.class, parentContainer);

        addContainerComponents(tempContainer);

        ComponentFactory lastCaf = injectionType;
        while (!componentFactories.empty()) {
            lastCaf = buildComponentFactory(tempContainer, lastCaf);
        }

        tempContainer.addComponent(ComponentFactory.class, lastCaf);

        buildComponentMonitor(tempContainer);

        if (lifecycleStrategy == null) {
            tempContainer.addComponent(LifecycleStrategy.class, lifecycleStrategyClass);
        } else {
            tempContainer.addComponent(LifecycleStrategy.class, lifecycleStrategy);

        }
        tempContainer.addComponent("mpc", mpcClass);

        MutablePicoContainer newContainer = (MutablePicoContainer) tempContainer.getComponent("mpc");

        addChildToParent(newContainer);
        return newContainer;
    }

    private void buildComponentMonitor(DefaultPicoContainer tempContainer) {
        if (componentMonitorClass == null) {
            tempContainer.addComponent(ComponentMonitor.class, componentMonitor);
        } else {
            tempContainer.addComponent(ComponentMonitor.class, componentMonitorClass);
        }
    }

    private void addChildToParent(MutablePicoContainer newContainer) {
        if (addChildToParent) {
            if (parentContainer instanceof MutablePicoContainer) {
                ((MutablePicoContainer)parentContainer).addChildContainer(newContainer);
            } else {
                throw new PicoCompositionException("If using addChildContainer() the parent must be a MutablePicoContainer");
            }
        }
    }

    private void addContainerComponents(DefaultPicoContainer temp) {
        for (Object containerComp : containerComps) {
            temp.addComponent(containerComp);
        }
    }

    private ComponentFactory buildComponentFactory(DefaultPicoContainer container, final ComponentFactory lastCaf) {
        Object componentFactory = componentFactories.pop();
        DefaultPicoContainer tmpContainer = new TransientPicoContainer(container);
        tmpContainer.addComponent("componentFactory", componentFactory);
        if (lastCaf != null) {
            tmpContainer.addComponent(ComponentFactory.class, lastCaf);
        }
        ComponentFactory newlastCaf = (ComponentFactory) tmpContainer.getComponent("componentFactory");
        if (newlastCaf instanceof BehaviorFactory) {
            ((BehaviorFactory) newlastCaf).wrap(lastCaf);
        }
        return newlastCaf;
    }

    public PicoBuilder withHiddenImplementations() {
        componentFactories.push(implementationHiding());
        return this;
    }

    public PicoBuilder withSetterInjection() {
        injectionType = SDI();
        return this;
    }

    public PicoBuilder withAnnotatedMethodInjection() {
        injectionType = annotatedMethodDI();
        return this;
    }


    public PicoBuilder withAnnotatedFieldInjection() {
        injectionType = annotatedFieldDI();
        return this;
    }


    public PicoBuilder withConstructorInjection() {
        injectionType = CDI();
        return this;
    }

    public PicoBuilder withNamedMethodInjection() {
        injectionType = namedMethod();
        return this;
    }

    public PicoBuilder withCaching() {
        componentFactories.push(caching());
        return this;
    }

    public PicoBuilder withComponentFactory(ComponentFactory componentFactory) {
        if (componentFactory == null) {
            throw new NullPointerException("CAF cannot be null");
        }
        componentFactories.push(componentFactory);
        return this;
    }

    public PicoBuilder withSynchronizing() {
        componentFactories.push(Synchronizing.class);
        return this;
    }

    public PicoBuilder withLocking() {
        componentFactories.push(Locking.class);
        return this;
    }

    public PicoBuilder withBehaviors(BehaviorFactory... factories) {
        for (ComponentFactory componentFactory : factories) {
            componentFactories.push(componentFactory);
        }
        return this;
    }

    public PicoBuilder implementedBy(Class<? extends MutablePicoContainer> containerClass) {
        mpcClass = containerClass;
        return this;
    }

    public PicoBuilder withMonitor(ComponentMonitor componentMonitor) {
        this.componentMonitor = componentMonitor;
        componentMonitorClass = null;
        return this;
    }

    public PicoBuilder withComponentFactory(Class<? extends ComponentFactory> componentFactoryClass) {
        componentFactories.push(componentFactoryClass);
        return this;
    }

    public PicoBuilder withCustomContainerComponent(Object containerDependency) {
        containerComps.add(containerDependency);
        return this;
    }

    public PicoBuilder withPropertyApplier() {
        componentFactories.push(PropertyApplying.class);
        return this;
    }

    public PicoBuilder withAutomatic() {
        componentFactories.push(Automating.class);
        return this;
    }

    public PicoBuilder withMethodInjection() {
        componentFactories.push(new MethodInjection());
        return this;
    }

    public PicoBuilder addChildToParent() {
        addChildToParent =  true;
        return this;
    }
}
