/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Mauro Talevi                                             *
 *****************************************************************************/
package org.picocontainer.adapters;

import java.io.Serializable;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.ComponentMonitorStrategy;
import org.picocontainer.defaults.DelegatingComponentMonitor;

/**
 * Abstract {@link org.picocontainer.defaults.ComponentAdapterFactory ComponentAdapterFactory} supporting a
 * {@link org.picocontainer.defaults.ComponentMonitorStrategy ComponentMonitorStrategy}.
 * It provides a {@link org.picocontainer.defaults.DelegatingComponentMonitor default ComponentMonitor},
 * but does not allow to use <code>null</code> for the component monitor.
 *  
 * @author Mauro Talevi
 * @see org.picocontainer.defaults.ComponentAdapterFactory
 * @see org.picocontainer.defaults.ComponentMonitorStrategy
 * @since 1.2
 */
public abstract class MonitoringComponentAdapterFactory implements ComponentAdapterFactory, ComponentMonitorStrategy, Serializable {
    private ComponentMonitor componentMonitor;

    /**
     * Constructs a MonitoringComponentAdapterFactory with a custom monitor 
     * @param monitor the ComponentMonitor used by the factory
     */
    protected MonitoringComponentAdapterFactory(ComponentMonitor monitor) {
        if (monitor == null){
            throw new NullPointerException("componentMonitor");
        }
        this.componentMonitor = monitor;
    }

    /**
     * Constructs a  MonitoringComponentAdapterFactory with a {@link org.picocontainer.defaults.DelegatingComponentMonitor default monitor}.
     */
    protected MonitoringComponentAdapterFactory() {
        this(new DelegatingComponentMonitor());
    }
    
    public void changeMonitor(ComponentMonitor monitor) {
        this.componentMonitor = monitor;
    }

    /**
     * Returns the monitor currently used
     * @return The ComponentMonitor currently used
     */
    public ComponentMonitor currentMonitor(){
        return componentMonitor;
    }

}
