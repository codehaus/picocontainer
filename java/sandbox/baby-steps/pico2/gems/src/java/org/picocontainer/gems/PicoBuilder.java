package org.picocontainer.gems;

import org.picocontainer.PicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.adapters.SynchronizedComponentAdapterFactory;
import org.picocontainer.gems.monitors.Log4JComponentMonitor;
import org.picocontainer.gems.monitors.CommonsLoggingComponentMonitor;
import org.picocontainer.gems.adapters.HotSwappingComponentAdapterFactory;
import org.picocontainer.gems.adapters.ImplementationHidingComponentAdapterFactory;

public class PicoBuilder {

    org.picocontainer.PicoBuilder delegate;

    public PicoBuilder() {
        delegate = new org.picocontainer.PicoBuilder();
    }

    public PicoBuilder(PicoContainer parent) {
        delegate = new org.picocontainer.PicoBuilder(parent);
    }

    public PicoBuilder withLifecycle() {
        delegate.withLifecycle();
        return this;
    }

    public PicoBuilder withReflectionLifecycle() {
        delegate.withReflectionLifecycle();
        return this;
    }

    public PicoBuilder withConsoleMonitor() {
        delegate.withConsoleMonitor();
        return this;
    }

    public PicoBuilder withMonitor(Class cmClass) {
        delegate.withMonitor(cmClass);
        return this;
    }


    public MutablePicoContainer build() {
        return delegate.build();
    }

    public PicoBuilder withHiddenImplementations() {
        delegate.withComponentAdapterFactory(ImplementationHidingComponentAdapterFactory.class);
        return this;
    }

    public PicoBuilder withSetterInjection() {
        delegate.withSetterInjection();
        return this;
    }

    public PicoBuilder withAnnotationInjection() {
        delegate.withAnnotationInjection();
        return this;
    }

    public PicoBuilder withConstructorInjection() {
        delegate.withConstructorInjection();
        return this;
    }

    public PicoBuilder withCaching() {
        delegate.withCaching();
        return this;
    }

    // Custom to gems...

    public PicoBuilder withLog4JMonitoring() {
        delegate.withMonitor(Log4JComponentMonitor.class);
        return this;
    }

    public PicoBuilder withCommonsLoggingMonitoring() {
        delegate.withMonitor(CommonsLoggingComponentMonitor.class);
        return this;
    }

    public PicoBuilder withSwappableComponentImplementations() {
        delegate.withMonitor(HotSwappingComponentAdapterFactory.class);
        return this;
    }


}
