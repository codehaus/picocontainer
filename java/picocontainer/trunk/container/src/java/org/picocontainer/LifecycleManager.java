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

import org.picocontainer.defaults.LifecycleStrategy;

/**
 * A manager for the lifecycle of a container's components.
 * 
 * @author Paul Hammant
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 * @version $Revision$
 * @see LifecycleStrategy 
 */
public interface LifecycleManager {

    /**
     * Invoke the "start" method on the container's components. It is up to the implementor to define exactly what a
     * component's "start" method is and what components of the container are affected.
     * 
     * @param container the container to "start" its component's lifecylce
     */
    void start(PicoContainer container);

    /**
     * Invoke the "stop" method on the container's components. It is up to the implementor to define exactly what a
     * component's "stop" method is and what components of the container are affected.
     * 
     * @param container the container to "stop" its component's lifecylce
     */
    void stop(PicoContainer container);

    /**
     * Invoke the "dispose" method on the container's components. It is up to the implementor to define exactly what a
     * component's "dispose" method is and what components of the container are affected.
     * 
     * @param container the container to "dispose" its component's lifecylce
     */
    void dispose(PicoContainer container);

}
