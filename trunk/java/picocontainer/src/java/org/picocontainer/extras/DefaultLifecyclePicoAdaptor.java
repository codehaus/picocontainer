/*****************************************************************************
 * Copyright (C) OldPicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.extras;

import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.lifecycle.LifecyclePicoAdapter;
import org.picocontainer.lifecycle.Startable;
import org.picocontainer.lifecycle.Stoppable;
import org.picocontainer.lifecycle.Disposable;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class DefaultLifecyclePicoAdaptor implements LifecyclePicoAdapter {

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

    private void initializeIfNotInitialized() throws PicoException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        if (startableAggregatedComponent == null) {
            try {
                startableAggregatedComponent = (Startable) picoContainer.getComponentMulticaster(true, false);
            } catch (ClassCastException e) {
            }
        }
        if (stoppableAggregatedComponent == null) {
            try {
                stoppableAggregatedComponent = (Stoppable) picoContainer.getComponentMulticaster(false, false);
            } catch (ClassCastException e) {
            }
        }
        if (disposableAggregatedComponent == null) {
            try {
                Object o = picoContainer.getComponentMulticaster(false, false);
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

