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
import org.picocontainer.PicoException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.DefaultComponentMulticasterPicoAdapter;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.lifecycle.Disposable;
import org.picocontainer.lifecycle.LifecyclePicoAdapter;
import org.picocontainer.lifecycle.Startable;
import org.picocontainer.lifecycle.Stoppable;

/**
 * @author Paul Hammant
 * @author Ward Cunningham
 * @version $Revision$
 */
public class DefaultLifecyclePicoAdapter implements LifecyclePicoAdapter {

    private Startable startableAggregatedComponent;
    private Stoppable stoppableAggregatedComponent;
    private Disposable disposableAggregatedComponent;
    private boolean started;
    private boolean disposed;
    private final PicoContainer picoContainer;
    private final ComponentMulticasterPicoAdapter multicasterAdapter;

    public DefaultLifecyclePicoAdapter(PicoContainer picoContainer, ComponentMulticasterPicoAdapter multicasterAdapter) {
         this.picoContainer = picoContainer;
         this.multicasterAdapter = multicasterAdapter;
    }

    public DefaultLifecyclePicoAdapter(PicoContainer picoContainer) {
        this.picoContainer = picoContainer;
        if(picoContainer instanceof ComponentMulticasterPicoAdapter) {
            multicasterAdapter = (ComponentMulticasterPicoAdapter)picoContainer;
        } else {
            multicasterAdapter = new DefaultComponentMulticasterPicoAdapter(picoContainer);
        }
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

    private void initializeIfNotInitialized() throws PicoException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        if (startableAggregatedComponent == null) {
            try {
                startableAggregatedComponent = (Startable) multicasterAdapter.getComponentMulticaster(true, false);
            } catch (ClassCastException e) {
            }
        }
        if (stoppableAggregatedComponent == null) {
            try {
                stoppableAggregatedComponent = (Stoppable) multicasterAdapter.getComponentMulticaster(false, false);
            } catch (ClassCastException e) {
            }
        }
        if (disposableAggregatedComponent == null) {
            try {
                Object o = multicasterAdapter.getComponentMulticaster(false, false);
                disposableAggregatedComponent = (Disposable) o;
            } catch (ClassCastException e) {
            }
        }

    }

    public void start() {
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

    public void stop() {
        checkDisposed();
        initializeIfNotInitialized();
        if (started == false) {
            throw new IllegalStateException("Already stopped (or maybe never started).");
        }
        started = false;
        if (stoppableAggregatedComponent != null) {
            stoppableAggregatedComponent.stop();
        }
    }

    public void dispose() {
        checkDisposed();
        initializeIfNotInitialized();
        disposed = true;
        if (disposableAggregatedComponent != null) {
            disposableAggregatedComponent.dispose();
        }
    }

    private void checkDisposed() {
        if (disposed) {
            throw new IllegalStateException("Components already disposed of");
        }
    }

}

