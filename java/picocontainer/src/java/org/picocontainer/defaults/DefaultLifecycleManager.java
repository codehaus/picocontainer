/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/

package org.picocontainer.defaults;

import org.picocontainer.Disposable;
import org.picocontainer.LifecycleManager;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoVisitor;
import org.picocontainer.Startable;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * This class implements the default lifecycle based on
 * <ul>
 * <li>{@link org.picocontainer.Startable#start()}</li>
 * <li>{@link org.picocontainer.Startable#stop()}</li>
 * <li>{@link org.picocontainer.Disposable#dispose()}</li>
 * </ul>
 *
 * It also allows custom lifecycle strategies to be plugged in via {@link #DefaultLifecycleManager(org.picocontainer.PicoVisitor, org.picocontainer.PicoVisitor, org.picocontainer.PicoVisitor)}.
 *
 * @author Paul Hammant
 * @author J&ouml;rg Schaible
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class DefaultLifecycleManager implements LifecycleManager, Serializable {

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
    public DefaultLifecycleManager(ComponentMonitor componentMonitor) {
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
    public DefaultLifecycleManager(PicoVisitor startVisitor, PicoVisitor stopVisitor, PicoVisitor disposeVisitor) {
        this.startVisitor = startVisitor;
        this.stopVisitor = stopVisitor;
        this.disposeVisitor = disposeVisitor;
    }

    /**
     * Creates a lifecycle manager with default visitors using a {@link NullComponentMonitor}.
     */
    public DefaultLifecycleManager() {
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
