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

import org.picocontainer.ComponentMonitor;

/**
 * Abstract {@link ComponentAdapterFactory} supporting different {@link ComponentMonitor}
 * 
 * @author Mauro Talevi
 * @since 1.2
 */
public abstract class MonitoringComponentAdapterFactory implements ComponentAdapterFactory, Serializable {
    private ComponentMonitor componentMonitor;

    /**
     * Creates a MonitoringComponentAdapterFactory with a custom monitor 
     * @param monitor the ComponentMonitor used by the factory
     */
    public MonitoringComponentAdapterFactory(ComponentMonitor monitor) {
        this.componentMonitor = monitor;
    }

    /**
     * Creates a  MonitoringComponentAdapterFactory with default monitor
     */
    public MonitoringComponentAdapterFactory() {
        this(new DelegatingComponentMonitor());
    }
    
    public void changeMonitor(ComponentMonitor monitor) {
        this.componentMonitor = monitor;
    }

    /**
     * Retur the monitor currently used
     * @return The ComponentMonitor currently used
     */
    protected ComponentMonitor currentMonitor(){
        return componentMonitor;
    }

}
