/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.extras;

import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultComponentMulticasterPicoAdapter;
import org.picocontainer.lifecycle.Disposable;
import org.picocontainer.lifecycle.LifecyclePicoAdapter;
import org.picocontainer.lifecycle.Startable;
import org.picocontainer.lifecycle.Stoppable;

/**
 * @author Paul Hammant
 * @author Ward Cunningham
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class DefaultLifecyclePicoAdapter implements LifecyclePicoAdapter {

    private boolean started;
    private boolean disposed;
    private final PicoContainer picoContainer;
    private final DefaultComponentMulticasterPicoAdapter multicasterAdapter;

    public DefaultLifecyclePicoAdapter(PicoContainer picoContainer, DefaultComponentMulticasterPicoAdapter multicasterAdapter) {
        this.picoContainer = picoContainer;
        this.multicasterAdapter = multicasterAdapter;
    }

    public DefaultLifecyclePicoAdapter(PicoContainer picoContainer) {
        this.picoContainer = picoContainer;
        multicasterAdapter = new DefaultComponentMulticasterPicoAdapter(picoContainer);
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isStopped() {
        return !started;
    }

    public boolean isDisposed() {
        return disposed;
    }

    public PicoContainer getPicoContainer() {
        return picoContainer;
    }

    public void start() {
        checkDisposed();
        if (started) {
            throw new IllegalStateException("Already started.");
        }
        try {
            ((Startable) multicasterAdapter.getComponentMulticaster(true, false)).start();
        } catch (ClassCastException ignore) {
        }
        started = true;
    }

    public void stop() {
        checkDisposed();
        if (!started) {
            throw new IllegalStateException("Already stopped (or maybe never started).");
        }
        try {
            ((Stoppable) multicasterAdapter.getComponentMulticaster(false, false)).stop();
        } catch (ClassCastException ignore) {
        }
        started = false;
    }

    public void dispose() {
        checkDisposed();
        try {
            ((Disposable) multicasterAdapter.getComponentMulticaster(false, false)).dispose();
        } catch (ClassCastException ignore) {
        }
        disposed = true;
    }

    private void checkDisposed() {
        if (disposed) {
            throw new IllegalStateException("Components already disposed of");
        }
    }

}

