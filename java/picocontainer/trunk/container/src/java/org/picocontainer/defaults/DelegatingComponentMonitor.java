/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Mauro Talevi                                             *
 *****************************************************************************/

package org.picocontainer.defaults;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.monitors.NullComponentMonitor;

/**
 * A {@link ComponentMonitor} which delegates to another monitor 
 * 
 * @author Mauro Talevi
 * @version $Revision: $
 */
public class DelegatingComponentMonitor implements ComponentMonitor, Serializable {

    private  ComponentMonitor delegate;
    
    /**
     * Creates a DelegatingComponentMonitor with a given delegate
     * @param delegate the ComponentMonitor to which this monitor delegates
     */
    public DelegatingComponentMonitor(ComponentMonitor delegate) {
        this.delegate = delegate;
    }

    /**
     * Creates a DelegatingComponentMonitor with an instance of 
     * {@link NullComponentMonitor}.
     */
    public DelegatingComponentMonitor() {
        this(NullComponentMonitor.getInstance());
    }
    
    public void instantiating(Constructor constructor) {
        delegate.instantiating(constructor);
    }

    public void instantiated(Constructor constructor, long duration) {
        delegate.instantiated(constructor, duration);
    }

    public void instantiationFailed(Constructor constructor, Exception e) {
        delegate.instantiationFailed(constructor, e);
    }

    public void invoking(Method method, Object instance) {
        delegate.invoking(method, instance);
    }

    public void invoked(Method method, Object instance, long duration) {
        delegate.invoked(method, instance, duration);
    }

    public void invocationFailed(Method method, Object instance, Exception e) {
        delegate.invocationFailed(method, instance, e);
    }

}
