/*****************************************************************************
 * Copyright (ComponentC) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.defaults;

import org.picocontainer.PicoContainer;
import org.picocontainer.lifecycle.Disposable;
import org.picocontainer.lifecycle.Lifecycle;
import org.picocontainer.lifecycle.Startable;
import org.picocontainer.lifecycle.Stoppable;

import java.io.Serializable;
import java.util.List;

/**
 * Helper class for DefaultPicoContainer's Lifecycle support.
 * Primarily to avoid the DefaultPicoContainer class being too big.
 *
 * @author Paul Hammant
 * @author Ward Cunningham
 * @author Aslak Helles&oslash;y
 * @author Chris Stevenson
 * @version $Revision$
 */
class LifecycleAdapter implements Lifecycle, Serializable {

    private boolean started;
    private boolean disposed;
    private final PicoContainer picoContainer;
    private final ComponentMulticasterAdapter componentMulticasterAdapter;

    private LifecycleAdapter(PicoContainer picoContainer, ComponentMulticasterAdapter componentMulticasterAdapter) {
        this.picoContainer = picoContainer;
        this.componentMulticasterAdapter = componentMulticasterAdapter;
    }

    public LifecycleAdapter(PicoContainer picoContainer) {
        this(picoContainer, new DefaultComponentMulticasterAdapter());
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

    public void start() {
        checkDisposed();
        if (started) {
            throw new IllegalStateException("Already started.");
        }
        try {
            final Startable startable = ((Startable) componentMulticasterAdapter.getComponentMulticaster(picoContainer, true));
            startable.start();
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
            ((Stoppable) componentMulticasterAdapter.getComponentMulticaster(picoContainer, false)).stop();
        } catch (ClassCastException ignore) {
        }
        started = false;
    }

    public void dispose() {
        checkDisposed();
        try {
            ((Disposable) componentMulticasterAdapter.getComponentMulticaster(picoContainer, false)).dispose();
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

