/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.componentadapters;

import java.io.Serializable;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.defaults.ComponentMonitorStrategy;
import org.picocontainer.defaults.DelegatingComponentMonitor;

/**
 * Abstract {@link ComponentAdapter ComponentAdapter} supporting a 
 * {@link org.picocontainer.defaults.ComponentMonitorStrategy ComponentMonitorStrategy}.
 * It provides a {@link org.picocontainer.defaults.DelegatingComponentMonitor default ComponentMonitor},
 * but does not allow to use <code>null</code> for the component monitor.
 *  
 * @author Mauro Talevi
 * @version $Revision: $
 * @see ComponentAdapter
 * @see org.picocontainer.defaults.ComponentMonitorStrategy
 * @since 1.2
 */
public abstract class MonitoringComponentAdapter implements ComponentAdapter, ComponentMonitorStrategy, Serializable {
    private ComponentMonitor componentMonitor;

    /**
     * Constructs a MonitoringComponentAdapter with a custom monitor
     * @param monitor the component monitor used by this ComponentAdapter
     */
    protected MonitoringComponentAdapter(ComponentMonitor monitor) {
        if (monitor == null){
            throw new NullPointerException("monitor");
        }
        this.componentMonitor = monitor;
    }

    /**
     * Constructs a MonitoringComponentAdapter with a {@link org.picocontainer.defaults.DelegatingComponentMonitor default monitor}.
     */
    protected MonitoringComponentAdapter() {
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
