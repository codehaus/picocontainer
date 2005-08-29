/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Jon Tirsen                                               *
 *****************************************************************************/

package org.picocontainer.defaults;

import java.io.Serializable;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleManager;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoVisitor;

/**
 * <p>
 * Component adapter which decorates another adapter.
 * </p>
 * <p>
 * This adapter supports a {@link ComponentMonitorStrategy component monitor strategy}
 * and will propagate change of monitor to the delegate if the delegate itself
 * support the monitor strategy.
 * </p>
 * <p>
 * This adapter is also {@link LifecycleManager lifecycle manager} and will 
 * propagate the delegate's {@link LifecycleStrategy lifecycle strategy} if the delegate 
 * is also a lifecycle manager.
 * </p>
 * 
 * @author Jon Tirsen
 * @author Aslak Hellesoy
 * @author Mauro Talevi
 * @version $Revision$
 */
public class DecoratingComponentAdapter implements ComponentAdapter, ComponentMonitorStrategy, LifecycleManager, Serializable {

    private ComponentAdapter delegate;

    public DecoratingComponentAdapter(ComponentAdapter delegate) {
         this.delegate = delegate;
    }
    
    public Object getComponentKey() {
        return delegate.getComponentKey();
    }

    public Class getComponentImplementation() {
        return delegate.getComponentImplementation();
    }

    public Object getComponentInstance(PicoContainer container) throws PicoInitializationException, PicoIntrospectionException {
        return delegate.getComponentInstance(container);
    }

    public void verify(PicoContainer container) throws PicoIntrospectionException {
        delegate.verify(container);
    }

    public ComponentAdapter getDelegate() {
        return delegate;
    }

    public void accept(PicoVisitor visitor) {
        visitor.visitComponentAdapter(this);
        delegate.accept(visitor);
    }

    /**
     * Delegates change of monitor if the delegate supports 
     * a component monitor strategy.
     * {@inheritDoc}
     */
    public void changeMonitor(ComponentMonitor monitor) {
        if ( delegate instanceof ComponentMonitorStrategy ){
            ((ComponentMonitorStrategy)delegate).changeMonitor(monitor);
        }
    }

    /**
     * Returns delegate's current monitor if the delegate supports 
     * a component monitor strategy.
     * {@inheritDoc}
     * @throws PicoIntrospectionException if no component monitor is found in delegate
     */
    public ComponentMonitor currentMonitor() {
        if ( delegate instanceof ComponentMonitorStrategy ){
            return ((ComponentMonitorStrategy)delegate).currentMonitor();
        }
        throw new PicoIntrospectionException("No component monitor found in delegate");
    }
    
    /**
     * Invokes delegate start method if the delegate is a LifecycleManager
     * {@inheritDoc}
     */
    public void start(PicoContainer container) {
        if ( delegate instanceof LifecycleManager ){
            ((LifecycleManager)delegate).start(container);
        }
    }

    /**
     * Invokes delegate stop method if the delegate is a LifecycleManager
     * {@inheritDoc}
     */
    public void stop(PicoContainer container) {
        if ( delegate instanceof LifecycleManager ){
            ((LifecycleManager)delegate).stop(container);
        }
    }
    
    /**
     * Invokes delegate dispose method if the delegate is a LifecycleManager
     * {@inheritDoc}
     */
    public void dispose(PicoContainer container) {
        if ( delegate instanceof LifecycleManager ){
            ((LifecycleManager)delegate).dispose(container);
        }
    }

    public LifecycleStrategy currentLifecycleStrategy() {
        if ( delegate instanceof LifecycleManager ){
            return ((LifecycleManager)delegate).currentLifecycleStrategy();
        }
        throw new PicoIntrospectionException("No lifecycle strategy found in delegate");
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[");
        buffer.append(this.getClass().getName());
        buffer.append(" delegate=");
        buffer.append(delegate);
        buffer.append("]");
        return buffer.toString();
    }

}

