/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.defaults;

/**
 * An interface which specifies the lifecyle strategy on the addComponent instance.
 * Lifecycle strategies are used by addComponent adapters to delegate the lifecyle
 * operations on the addComponent instances.
 *
 * @author Paul Hammant
 * @author Peter Royal
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 * @see org.picocontainer.Startable
 * @see org.picocontainer.Disposable
 */
public interface LifecycleStrategy {
    
    /**
     * Invoke the "start" method on the addComponent instance if this is startable.
     * It is up to the implementation of the strategy what "start" and "startable" means.
     * 
     * @param component the instance of the addComponent to start
     */
    void start(Object component);
    
    /**
     * Invoke the "stop" method on the addComponent instance if this is stoppable.
     * It is up to the implementation of the strategy what "stop" and "stoppable" means.
     * 
     * @param component the instance of the addComponent to stop
     */
    void stop(Object component);

    /**
     * Invoke the "dispose" method on the addComponent instance if this is disposable.
     * It is up to the implementation of the strategy what "dispose" and "disposable" means.
     * 
     * @param component the instance of the addComponent to dispose
     */
    void dispose(Object component);

    /**
     * Test if a addComponent instance has a lifecycle.
     * @param type the addComponent's type
     * 
     * @return <code>true</code> if the addComponent has a lifecycle
     */
    boolean hasLifecycle(Class type);

}
