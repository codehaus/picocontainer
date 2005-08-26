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
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoVisitor;

/**
 * Component adapter which decorates another adapter.
 * This adapter support a {@link ComponentMonitorStrategy component monitor strategy}
 * and will propagate change of monitor to the delegate if the delegate itself
 * support the monitor strategy.
 * 
 * @author Jon Tirsen
 * @author Aslak Hellesoy
 * @author Mauro Talevi
 * @version $Revision$
 */
public class DecoratingComponentAdapter implements ComponentAdapter, ComponentMonitorStrategy, Serializable {

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
    
    public String toString() {
        StringBuffer buffer = new StringBuffer(this.getClass().getName());
        buffer.append("[");
        buffer.append(delegate.toString());
        buffer.append("]");
        return buffer.toString();
    }
}
