/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.defaults;

import java.io.Serializable;

import org.picocontainer.Disposable;
import org.picocontainer.Startable;

/**
 * Default lifecycle strategy.  Starts and stops component if Startable,
 * and disposes it if Disposable.
 * 
 * @author Mauro Talevi
 * @see Startable
 * @see Disposable
 */
public class DefaultLifecycleStrategy implements LifecycleStrategy, Serializable {

    public void start(Object component) {
        if ( component != null && component instanceof Startable ){
            ((Startable)component).start();
        }
    }

    public void stop(Object component) {
        if ( component != null && component instanceof Startable ){
            ((Startable)component).stop();
        }
    }

    public void dispose(Object component) {
        if ( component != null && component instanceof Disposable ){
            ((Disposable)component).dispose();
        }
    }

}
