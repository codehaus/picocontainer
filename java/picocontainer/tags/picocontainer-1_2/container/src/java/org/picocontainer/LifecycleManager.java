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
 * A manager for the lifecycle of a container's components.  
 * The lifecycle manager is implemented by the component adapters
 * which will resolve the dependencies of the component instance and 
 * delegate the implementation of the lifecycle control to the 
 * {@link org.picocontainer.defaults.LifecycleStrategy lifecycle strategy}.
 * 
 * @author Paul Hammant
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 * @version $Revision$
 * @see org.picocontainer.defaults.LifecycleStrategy 
 * @since 1.2
 */
public interface LifecycleManager {

    /**
     * Invoke the "start" method on the container's components. 
     * @param container the container to "start" its components' lifecycle
     */
    void start(PicoContainer container);

    /**
     * Invoke the "stop" method on the container's components. 
     * @param container the container to "stop" its components' lifecycle
     */
    void stop(PicoContainer container);

    /**
     * Invoke the "dispose" method on the container's components. 
     * @param container the container to "dispose" its components' lifecycle
     */
    void dispose(PicoContainer container);

    /**
     * Test if a container's component has a lifecycle.
     * @return <code>true</code> if the component has a lifecycle
     */
    boolean hasLifecycle();
}
