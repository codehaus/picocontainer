package org.picocontainer.alternatives;

import org.picocontainer.LifecycleManager;
import org.picocontainer.PicoVisitor;
import org.picocontainer.Startable;
import org.picocontainer.Disposable;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ComponentMonitor;
import org.picocontainer.defaults.LifecycleVisitor;
import org.picocontainer.defaults.NullComponentMonitor;

import java.lang.reflect.Method;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class RootVisitingLifecycleManager implements LifecycleManager {

    private static final Method START;
    private static final Method STOP;
    private static final Method DISPOSE;
    private PicoVisitor startVisitor;
    private PicoVisitor stopVisitor;
    private PicoVisitor disposeVisitor;

    static {
        try {
            START = Startable.class.getMethod("start", null);
            STOP = Startable.class.getMethod("stop", null);
            DISPOSE = Disposable.class.getMethod("dispose", null);
        } catch (NoSuchMethodException e) {
            ///CLOVER:OFF
            throw new InternalError(e.getMessage());
            ///CLOVER:ON
        }
    }

    /**
     * Creates a lifecycle manager which will invoke lifecycle methods on components implementing:
     * <ul>
     * <li>{@link org.picocontainer.Startable#start()}</li>
     * <li>{@link org.picocontainer.Startable#stop()}</li>
     * <li>{@link org.picocontainer.Disposable#dispose()}</li>
     * </ul>
     *
     * @param componentMonitor the monitor that will receive lifecycle events.
     */
    public RootVisitingLifecycleManager(ComponentMonitor componentMonitor) {
        this(new LifecycleVisitor(START, Startable.class, true, componentMonitor),
                new LifecycleVisitor(STOP, Startable.class, false, componentMonitor),
                new LifecycleVisitor(DISPOSE, Disposable.class, false, componentMonitor));
    }

    /**
     * Creates a lifecycle manager using pluggable lifecycle.
     *
     * @param startVisitor the visitor to use on start()
     * @param stopVisitor the visitor to use on stop()
     * @param disposeVisitor the visitor to use on dispose()
     */
    public RootVisitingLifecycleManager(PicoVisitor startVisitor, PicoVisitor stopVisitor, PicoVisitor disposeVisitor) {
        this.startVisitor = startVisitor;
        this.stopVisitor = stopVisitor;
        this.disposeVisitor = disposeVisitor;
    }

    /**
     * Creates a lifecycle manager with default visitors using a {@link org.picocontainer.defaults.NullComponentMonitor}.
     */
    public RootVisitingLifecycleManager() {
        this(new NullComponentMonitor());
    }

    public void start(PicoContainer node) {
        startVisitor.traverse(node);
    }

    public void stop(PicoContainer node) {
        stopVisitor.traverse(node);
    }

    public void dispose(PicoContainer node) {
        disposeVisitor.traverse(node);
    }

}
