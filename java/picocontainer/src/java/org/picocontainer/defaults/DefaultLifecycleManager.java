package org.picocontainer.defaults;

import org.picocontainer.ComponentVisitor;
import org.picocontainer.ContainerVisitor;
import org.picocontainer.Disposable;
import org.picocontainer.LifecycleManager;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.Startable;

import java.io.Serializable;

/**
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @version $Revision$
 */
public class DefaultLifecycleManager implements LifecycleManager, Serializable {
    private final MutablePicoContainer pico;
    private final Class startableClass;
    private final Class stoppableClass;
    private final Class disposableClass;

    private boolean started = false;
    private boolean disposed = false;
    private ContainerVisitor containerStarter = new ContainerVisitor() {
        public void visit(PicoContainer pico) {
            if(pico instanceof MutablePicoContainer) {
                MutablePicoContainer mutablePicoContainer = (MutablePicoContainer) pico;
                mutablePicoContainer.getLifecycleManager().start();
            }
        }
    };
    private ContainerVisitor containerStopper = new ContainerVisitor() {
        public void visit(PicoContainer pico) {
            if(pico instanceof MutablePicoContainer) {
                MutablePicoContainer mutablePicoContainer = (MutablePicoContainer) pico;
                mutablePicoContainer.getLifecycleManager().stop();
            }
        }
    };
    private ContainerVisitor containerDisposer = new ContainerVisitor() {
        public void visit(PicoContainer pico) {
            if(pico instanceof MutablePicoContainer) {
                MutablePicoContainer mutablePicoContainer = (MutablePicoContainer) pico;
                mutablePicoContainer.getLifecycleManager().dispose();
            }
        }
    };

    private ComponentVisitor componentStarter = new ComponentVisitor() {
        public void visitComponentInstance(Object o) {
            startComponent(o);
        }
    };
    private ComponentVisitor componentStopper = new ComponentVisitor() {
        public void visitComponentInstance(Object o) {
            stopComponent(o);
        }
    };
    private ComponentVisitor componentDisposer = new ComponentVisitor() {
        public void visitComponentInstance(Object o) {
            disposeComponent(o);
        }
    };

    public DefaultLifecycleManager(MutablePicoContainer pico, Class startableClass, Class stoppableClass, Class disposableClass) {
        this.pico = pico;
        this.startableClass = startableClass;
        this.stoppableClass = stoppableClass;
        this.disposableClass = disposableClass;
    }

    public void start() {
        if (disposed) throw new IllegalStateException("Already disposed");
        if (started) throw new IllegalStateException("Already started");
        pico.accept(componentStarter, startableClass, true);
        pico.accept(containerStarter);
        started = true;
    }

    protected void startComponent(Object o) {
        ((Startable) o).start();
    }

    public void stop() {
        if (disposed) throw new IllegalStateException("Already disposed");
        if (!started) throw new IllegalStateException("Not started");
        pico.accept(containerStopper);
        pico.accept(componentStopper, stoppableClass, false);
        started = false;
    }

    protected void stopComponent(Object o) {
        ((Startable) o).stop();
    }

    public void dispose() {
        if (disposed) throw new IllegalStateException("Already disposed");
        pico.accept(containerDisposer);
        pico.accept(componentDisposer, disposableClass, false);
        if (pico.getParent() instanceof MutablePicoContainer) {
            ((MutablePicoContainer) pico.getParent()).removeChildContainer(pico);
        }
        disposed = true;

    }

    protected void disposeComponent(Object o) {
        ((Disposable) o).dispose();
    }

}
