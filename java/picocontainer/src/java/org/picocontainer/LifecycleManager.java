/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/

package org.picocontainer;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public interface LifecycleManager {

    /**
     * Invoke the standard PicoContainer lifecycle for {@link Startable#start()} or whatever the implementor's
     * concept of start is.
     *
     * @param node The node to start the traversal.
     */
    void start(PicoContainer node);

    /**
     * Invoke the standard PicoContainer lifecycle for {@link Startable#stop()} or whatever the implementor's
     * concept of stop is.
     *
     * @param node The node to start the traversal.
     */
    void stop(PicoContainer node);

    /**
     * Invoke the standard PicoContainer lifecycle for {@link Disposable#dispose()} or whatever the implementor's
     * concept of dispose is.
     *
     * @param node The node to start the traversal.
     */
    void dispose(PicoContainer node);
}
