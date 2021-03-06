/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.lifecycle;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.ComponentMonitorStrategy;

import java.io.Serializable;

/**
 * Abstract base class for lifecycle strategy implementation supporting a {@link ComponentMonitor}.
 * 
 * @author J&ouml;rg Schaible
 */
public abstract class AbstractMonitoringLifecycleStrategy implements LifecycleStrategy, ComponentMonitorStrategy, Serializable {

    private ComponentMonitor componentMonitor;

    /**
     * Construct a AbstractMonitoringLifecycleStrategy.
     * 
     * @param monitor the componentMonitor to use
     * @throws NullPointerException if the monitor is <code>null</code>
     */
    public AbstractMonitoringLifecycleStrategy(ComponentMonitor monitor) {
        changeMonitor(monitor);
    }
    
    public void changeMonitor(ComponentMonitor monitor) {
        if (monitor == null) {
            throw new NullPointerException("Monitor is null");
        }
        this.componentMonitor = monitor;
    }

    public ComponentMonitor currentMonitor() {
        return componentMonitor;
    }

}
