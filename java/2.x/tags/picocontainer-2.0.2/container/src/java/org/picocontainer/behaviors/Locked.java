/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.behaviors;


import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Paul Hammant
 */
public class Locked<T> extends AbstractBehavior<T> {
    /**
	 * Serialization UUID.
	 */
	private static final long serialVersionUID = 2355716085957171021L;
	
	/**
	 * Reentrant lock.
	 */
	private Lock lock = new ReentrantLock();

    public Locked(ComponentAdapter<T> delegate) {
        super(delegate);
    }

    public T getComponentInstance(PicoContainer container) throws PicoCompositionException {
        T retVal = null;
        lock.lock();
        try {
          retVal = super.getComponentInstance(container);
        }
        finally {
          lock.unlock();
        }
        return retVal;
    }

    public String getDescriptor() {
        return "Locked";
    }

}