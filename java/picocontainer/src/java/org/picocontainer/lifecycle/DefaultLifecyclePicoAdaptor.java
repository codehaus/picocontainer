/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.lifecycle;

import org.picocontainer.PicoContainer;

public class DefaultLifecyclePicoAdaptor implements LifecyclePicoAdaptor {

    private Startable startableAggregatedComponent;
    private Stoppable stoppableAggregatedComponent;
    private Disposable disposableAggregatedComponent;
    private boolean started;
    private boolean disposed;
    private final PicoContainer picoContainer;

    public DefaultLifecyclePicoAdaptor(PicoContainer picoContainer) {
        this.picoContainer = picoContainer;
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

    private void initializeIfNotInitialized() {
        if (startableAggregatedComponent == null) {
            try {
                startableAggregatedComponent = (Startable) picoContainer.getCompositeComponent(true, false);
            } catch (ClassCastException e) {
            }
        }
        if (stoppableAggregatedComponent == null) {
            try {

                stoppableAggregatedComponent = (Stoppable) picoContainer.getCompositeComponent(false, false);
            } catch (ClassCastException e) {
            }
        }
        if (disposableAggregatedComponent == null) {
            try {
                //TODO-Aslak broken ?
                Object o = picoContainer.getCompositeComponent(false, false);
                disposableAggregatedComponent = (Disposable) o;
            } catch (ClassCastException e) {
            }
        }

    }

    public void start() throws Exception {
        checkDisposed();
        initializeIfNotInitialized();
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
        initializeIfNotInitialized();
        if (started == false) {
            throw new IllegalStateException("Already stopped.");
        }
        started = false;
        if (stoppableAggregatedComponent != null) {
            stoppableAggregatedComponent.stop();
        }
    }

    public void dispose() throws Exception {
        checkDisposed();
        initializeIfNotInitialized();
        disposed = true;
        if (disposableAggregatedComponent != null) {
            disposableAggregatedComponent.dispose();
        }
    }

    private void checkDisposed() {
        if (disposed) {
            throw new IllegalStateException("Components Disposed Of");
        }
    }

}

