package org.nanocontainer;

import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.PicoBuilder;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.adapters.ImplementationHidingComponentAdapterFactory;
import org.picocontainer.monitors.ConsoleComponentMonitor;
import com.thoughtworks.xstream.io.xml.xppdom.Xpp3DomBuilder;


public class NanoBuilder {

    private Class ncClass = DefaultNanoContainer.class;
    private PicoBuilder picoBuilder;
    private ClassLoader classLoader = DefaultNanoContainer.class.getClassLoader();

    public NanoBuilder(PicoContainer parentcontainer) {
        picoBuilder = new PicoBuilder(parentcontainer);
    }

    public NanoBuilder() {
        picoBuilder = new PicoBuilder();
    }

    public NanoContainer build() {
        DefaultPicoContainer temp = new DefaultPicoContainer();
        temp.addComponent(ClassLoader.class, classLoader);
        temp.addComponent("nc", ncClass);
        temp.addComponent(MutablePicoContainer.class, picoBuilder.build());
        return (NanoContainer) temp.getComponent("nc");
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

    public NanoBuilder withMonitor(Class clazz) {
        picoBuilder.withMonitor(clazz);
        return this;
    }

    public NanoBuilder withHiddenImplementations() {
        picoBuilder.withHiddenImplementations();
        return this;
    }

    public NanoBuilder withComponentAdapterFactory(ComponentAdapterFactory componentAdapterFactory) {
        picoBuilder.withComponentAdapterFactory(componentAdapterFactory);
        return this;
    }

    public NanoBuilder withComponentAdapterFactories(ComponentAdapterFactory... factories) {
        picoBuilder.withComponentAdapterFactories(factories);
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
}
