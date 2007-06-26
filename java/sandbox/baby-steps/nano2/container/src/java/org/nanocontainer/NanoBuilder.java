package org.nanocontainer;

import org.picocontainer.BehaviorFactory;
import org.picocontainer.ComponentFactory;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.InjectionFactory;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoBuilder;
import org.picocontainer.PicoContainer;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.containers.TransientPicoContainer;

import org.nanocontainer.script.NanoContainerMarkupException;


public final class NanoBuilder {

    private Class<? extends NanoContainer> ncClass = DefaultNanoContainer.class;
    private final PicoBuilder picoBuilder;
    private ClassLoader classLoader = DefaultNanoContainer.class.getClassLoader();

    public NanoBuilder(PicoContainer parentcontainer, InjectionFactory injectionType) {
        picoBuilder = new PicoBuilder(parentcontainer, injectionType);
    }

    public NanoBuilder(PicoContainer parentcontainer) {
        picoBuilder = new PicoBuilder(parentcontainer);
    }

    public NanoBuilder(InjectionFactory injectionType) {
        picoBuilder = new PicoBuilder(injectionType);
    }

    public NanoBuilder() {
        picoBuilder = new PicoBuilder();
    }

    public NanoContainer build() {
        DefaultPicoContainer temp = new TransientPicoContainer();
        temp.addComponent(ClassLoader.class, classLoader);
        temp.addComponent("nc", ncClass);
        temp.addComponent(MutablePicoContainer.class, picoBuilder.build());
        return (NanoContainer)temp.getComponent("nc");
    }

    public NanoBuilder withConsoleMonitor() {
        picoBuilder.withConsoleMonitor();
        return this;
    }

    public NanoBuilder withLifecycle() {
        picoBuilder.withLifecycle();
        return this;
    }

    public NanoBuilder withReflectionLifecycle() {
        picoBuilder.withReflectionLifecycle();
        return this;
    }

    public NanoBuilder withMonitor(Class<? extends ComponentMonitor> clazz) {
        picoBuilder.withMonitor(clazz);
        return this;
    }

    public NanoBuilder withHiddenImplementations() {
        picoBuilder.withHiddenImplementations();
        return this;
    }

    public NanoBuilder withComponentFactory(ComponentFactory componentFactory) {
        picoBuilder.withComponentFactory(componentFactory);
        return this;
    }

    public NanoBuilder withComponentAdapterFactories(BehaviorFactory... factories) {
        picoBuilder.withBehaviors(factories);
        return this;
    }

    public NanoBuilder withSetterInjection() {
        picoBuilder.withSetterInjection();
        return this;
    }

    public NanoBuilder withAnnotationInjection() {
        picoBuilder.withAnnotationInjection();
        return this;
    }

    public NanoBuilder withConstructorInjection() {
        picoBuilder.withConstructorInjection();
        return this;
    }

    public NanoBuilder withCaching() {
        picoBuilder.withCaching();
        return this;
    }

    public NanoBuilder withThreadSafety() {
        picoBuilder.withThreadSafety();
        return this;
    }

    public NanoBuilder implementedBy(Class<? extends NanoContainer> nanoContainerClass) {
        ncClass = nanoContainerClass;
        return this;
    }

    public NanoBuilder picoImplementedBy(Class<? extends MutablePicoContainer> picoContainerClass) {
        picoBuilder.implementedBy(picoContainerClass);
        return this;
    }

    public NanoBuilder withClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        return this;
    }

    public NanoBuilder withComponentFactory(String componentFactoryName) {
        picoBuilder.withComponentFactory(loadClass(componentFactoryName, ComponentFactory.class));
        return this;
    }

    private <T> Class<? extends T> loadClass(String className, Class<T> asClass) {
        try {
            return classLoader.loadClass(className).asSubclass(asClass);
        } catch (ClassNotFoundException e) {
            throw new NanoContainerMarkupException(e);
        }
    }

    public NanoBuilder withMonitor(String monitorName) {
        picoBuilder.withMonitor(loadClass(monitorName, ComponentMonitor.class));
        return this;
    }
}
