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

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;

/**
 * Base class for all monitoring ComponentAdapters.
 * It provides a {@link DelegatingComponentMonitor default ComponentMonitor},
 * but does not allow to use <code>null</code> for the component monitor.
 *  
 * @author Mauro Talevi
 * @version $Revision: $
 * @since 1.2
 */
public abstract class MonitoringComponentAdapter implements ComponentAdapter, Serializable {
    private ComponentMonitor componentMonitor;

    /**
     * Constructs a MonitoringComponentAdapter with an instance of {@link DelegatingComponentMonitor}
     */
    protected MonitoringComponentAdapter() {
        this(new DelegatingComponentMonitor());
    }

    /**
     * Constructs a MonitoringComponentAdapter with a given monitor
     * @param componentMonitor the component monitor used by this ComponentAdapter
     */
    protected MonitoringComponentAdapter(ComponentMonitor componentMonitor) {
        if (componentMonitor == null){
            throw new NullPointerException("componentMonitor");
        }
        this.componentMonitor = componentMonitor;
    }

    protected ComponentMonitor currentMonitor(){
        return componentMonitor;
    }
}
