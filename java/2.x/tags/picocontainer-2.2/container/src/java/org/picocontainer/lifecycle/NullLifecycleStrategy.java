/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.lifecycle;

import java.io.Serializable;

import org.picocontainer.LifecycleStrategy;

/**
 * Lifecycle strategy that does nothing.
 *
 */
public class NullLifecycleStrategy implements LifecycleStrategy, Serializable {

    /**
	 * Serialization UUID.
	 */
	private static final long serialVersionUID = -626149098386614685L;

    /** {@inheritDoc} **/
	public void start(final Object component) {
		//Does nothing
    }

    /** {@inheritDoc} **/
    public void stop(final Object component) {
		//Does nothing
    }

    /** {@inheritDoc} **/
    public void dispose(final Object component) {
		//Does nothing
    }

    /** {@inheritDoc} **/
    public boolean hasLifecycle(final Class<?> type) {
        return false;
    }
}
