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
     * Invoke the "start" method on the container's components and child components.
     * It is up to the implementor to define exactly what a component's "start" method is.
     *
     * @param node The node to start the traversal.
     */
    void start(PicoContainer node);

    /**
     * Invoke the "stop" method on the container's components and child components.
     * It is up to the implementor to define exactly what a component's "stop" method is.
     *
     * @param node The node to start the traversal.
     */
    void stop(PicoContainer node);

    /**
     * Invoke the "dispose" method on the container's components and child components.
     * It is up to the implementor to define exactly what a component's "dispose" method is.
     *
     * @param node The node to start the traversal.
     */
    void dispose(PicoContainer node);
}
