package picocontainer.lifecycle;

import picocontainer.ComponentFactory;
import picocontainer.PicoContainer;
import picocontainer.PicoInitializationException;
import picocontainer.defaults.NullContainer;
import picocontainer.defaults.DefaultComponentFactory;
import picocontainer.hierarchical.HierarchicalPicoContainer;

public class LifecyclePicoContainer extends HierarchicalPicoContainer implements Startable, Stoppable, Disposable {

    private Startable startableAggregatedComponent;
    private Stoppable stoppingAggregatedComponent;
    private Disposable disposingAggregatedComponent;
    private boolean started;
    private boolean disposed;

    public LifecyclePicoContainer(ComponentFactory componentFactory, PicoContainer parentContainer) {
        super(componentFactory, parentContainer);
    }

    public static class Default extends LifecyclePicoContainer {
        public Default() {
            super(new DefaultComponentFactory(), new NullContainer());
        }
    }

    public void instantiateComponents() throws PicoInitializationException {
        super.instantiateComponents();
        try {
            startableAggregatedComponent = (Startable) getAggregateComponentProxy(true, false);
        } catch (ClassCastException e) {
        }
        try {

            stoppingAggregatedComponent = (Stoppable) getAggregateComponentProxy(false, false);
        } catch (ClassCastException e) {
        }
        try {

            disposingAggregatedComponent = (Disposable) getAggregateComponentProxy(false, false);
        } catch (ClassCastException e) {
        }

    }

    public void start() throws Exception {
        checkDisposed();
        if (started) {
            throw new IllegalStateException("Already started.");
        }
        started = true;
        if (startableAggregatedComponent != null) {
            startableAggregatedComponent.start();
        }
    }

    public void stop() throws Exception {
        checkDisposed();
        if (started == false) {
            throw new IllegalStateException("Already stopped.");
        }
        started = false;
        if (stoppingAggregatedComponent != null) {
            stoppingAggregatedComponent.stop();
        }
    }

    private void checkDisposed() {
        if (disposed) {
            throw new IllegalStateException("Components Disposed Of");
        }
    }

    public void dispose() throws Exception {
        checkDisposed();
        disposed = true;
        if (disposingAggregatedComponent != null) {
            disposingAggregatedComponent.dispose();
        }
    }
}

