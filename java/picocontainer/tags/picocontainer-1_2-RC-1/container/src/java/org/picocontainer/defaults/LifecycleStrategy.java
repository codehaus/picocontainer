/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.defaults;

/**
 * An interface which specifies the lifecyle strategy on the component instance. 
 * Lifecycle strategies are used by component adapters to delegate the lifecyle
 * operations on the component instances. 
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
     * Invoke the "start" method on the component instance if this is Startable
     * 
     * @param component the instance of the component to start
     */
    public void start(Object component);
    
    /**
     * Invoke the "stop" method on the component instance if this is Startable
     * 
     * @param component the instance of the component to stop
     */
    public void stop(Object component);

    /**
     * Invoke the "dispose" method on the component instance if this is Disposable
     * 
     * @param component the instance of the component to dispose
     */
    public void dispose(Object component);

}
