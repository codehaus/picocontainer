/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Mauro Talevi                                             *
 *****************************************************************************/

package org.picocontainer.monitors;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.ComponentMonitorStrategy;
import org.picocontainer.monitors.NullComponentMonitor;

/**
 * <p>
 * A {@link ComponentMonitor monitor} which delegates to another monitor.
 * It provides a {@link NullComponentMonitor default ComponentMonitor},
 * but does not allow to use <code>null</code> for the delegate.
 * </p>
 * <p>
 * It also supports a {@link org.picocontainer.ComponentMonitorStrategy monitor strategy}
 * that allows to change the delegate.
 * </p>
 * 
 * @author Mauro Talevi
 * @version $Revision: $
 * @since 1.2
 */
public class DelegatingComponentMonitor implements ComponentMonitor, ComponentMonitorStrategy, Serializable {

    private  ComponentMonitor delegate;
    
    /**
     * Creates a DelegatingComponentMonitor with a given delegate
     * @param delegate the ComponentMonitor to which this monitor delegates
     */
    public DelegatingComponentMonitor(ComponentMonitor delegate) {
        checkMonitor(delegate);
        this.delegate = delegate;
    }

    /**
     * Creates a DelegatingComponentMonitor with an instance of 
     * {@link NullComponentMonitor}.
     */
    public DelegatingComponentMonitor() {
        this(NullComponentMonitor.getInstance());
    }
    
    public Constructor instantiating(Constructor constructor) {
        return delegate.instantiating(constructor);
    }

    public void instantiated(Constructor constructor, Object instantiated, Object[] injected, long duration) {
        delegate.instantiated(constructor, instantiated, injected, duration);
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

    public void lifecycleInvocationFailed(Method method, Object instance, RuntimeException cause) {
        delegate.lifecycleInvocationFailed(method,instance, cause);
    }

    public void noComponent(Object componentKey) {
        delegate.noComponent(componentKey); 
    }

    /**
     * If the delegate supports a {@link ComponentMonitorStrategy monitor strategy},
     * this is used to changed the monitor while keeping the same delegate.
     * Else the delegate is replaced by the new monitor.
     * {@inheritDoc}
     */
    public void changeMonitor(ComponentMonitor monitor) {
        checkMonitor(monitor);
        if ( delegate instanceof ComponentMonitorStrategy ){
            ((ComponentMonitorStrategy)delegate).changeMonitor(monitor);
        } else {
            delegate = monitor;
        }
    }

    public ComponentMonitor currentMonitor() {
        if ( delegate instanceof ComponentMonitorStrategy ){
            return ((ComponentMonitorStrategy)delegate).currentMonitor();
        } else {
            return delegate;
        }
    }
    
    private void checkMonitor(ComponentMonitor monitor) {
        if ( monitor == null ){
            throw new NullPointerException("monitor");
        }
    }

}
