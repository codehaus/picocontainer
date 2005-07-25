/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.defaults;

import java.io.Serializable;

import org.picocontainer.ComponentMonitorStrategy;

/**
 * Abstract {@link ComponentAdapterFactory} supporting different {@linkComponentMonitorStrategy}
 * 
 * @author Mauro Talevi
 * @since 1.2
 */
public abstract class MonitoringComponentAdapterFactory implements ComponentAdapterFactory, Serializable {
    private ComponentMonitorStrategy componentMonitorStrategy;

    /**
     * Creates a MonitoringComponentAdapterFactory with a custom monitor strategy
     * @param monitorStrategy the ComponentMonitorStrategy
     */
    public MonitoringComponentAdapterFactory(ComponentMonitorStrategy monitorStrategy) {
        this.componentMonitorStrategy = monitorStrategy;
    }

    /**
     * Creates a  MonitoringComponentAdapterFactory with default monitor strategy
     */
    public MonitoringComponentAdapterFactory() {
        this(new DefaultComponentMonitorStrategy());
    }
    
    public void changeMonitorStrategy(ComponentMonitorStrategy monitorStrategy) {
        this.componentMonitorStrategy = monitorStrategy;
    }

    /**
     * Allow subclasses to get hold of current monitor strategy 
     * @return The ComponentMonitorStrategy currently used
     */
    protected ComponentMonitorStrategy currentMonitorStrategy(){
        return componentMonitorStrategy;
    }

}
