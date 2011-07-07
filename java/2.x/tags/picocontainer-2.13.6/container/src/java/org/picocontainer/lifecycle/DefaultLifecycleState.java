/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.lifecycle;

import org.picocontainer.PicoCompositionException;

import java.io.Serializable;

/**
 * Bean-like implementation of LifecycleState.
 * @author Paul Hammant
 * @author Michael Rimov
 *
 */
@SuppressWarnings("serial")
public class DefaultLifecycleState implements LifecycleState, Serializable {

    /**
	 * Default state of a container once it has been built.
	 */
	private static final String CONSTRUCTED = "CONSTRUCTED";

	/**
	 * 'Start' Lifecycle has been called.
	 */
	private static final String STARTED = "STARTED";

	/**
	 * 'Stop' lifecycle has been called.
	 */
	private static final String STOPPED = "STOPPED";

	/**
	 * 'Dispose' lifecycle has been called.
	 */
	private static final String DISPOSED = "DISPOSED";

	/**
	 * Initial state.
	 */
    private String state = CONSTRUCTED;

    /** {@inheritDoc} **/
    public void removingComponent() {
        if (isStarted()) {
            throw new PicoCompositionException("Cannot remove components after the container has started");
        }

        if (isDisposed()) {
            throw new PicoCompositionException("Cannot remove components after the container has been disposed");
        }
    }

    /** {@inheritDoc} **/
    public void starting() {
		if (isConstructed() || isStopped()) {
            state = STARTED;
			return;
		}
	    throw new IllegalStateException("Cannot start.  Current container state was: " + state);
    }


    /** {@inheritDoc} **/
    public void stopping() {
        if (!(isStarted())) {
            throw new IllegalStateException("Cannot stop.  Current container state was: " + state);
        }
    }

    /** {@inheritDoc} **/
    public void stopped() {
        state = STOPPED;
    }

    /** {@inheritDoc} **/
    public boolean isStarted() {
        return state == STARTED;
    }

    /** {@inheritDoc} **/
    public void disposing() {
        if (!(isStopped() || isConstructed())) {
            throw new IllegalStateException("Cannot dispose.  Current lifecycle state is: " + state);
        }

    }

    /** {@inheritDoc} **/
    public void disposed() {
        state = DISPOSED;
    }

    
    /** {@inheritDoc} **/
	public boolean isDisposed() {
		return state == DISPOSED;
    }

    /** {@inheritDoc} **/
	public boolean isStopped() {
		return state == STOPPED;
    }

	/**
	 * Returns true if no other state has been triggered so far.
	 * @return
	 */
	public boolean isConstructed() {
		return state == CONSTRUCTED;
	}

}
