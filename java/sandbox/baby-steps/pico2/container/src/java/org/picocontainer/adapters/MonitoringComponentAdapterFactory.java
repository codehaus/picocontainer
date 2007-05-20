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
 * but does not allow to use <code>null</code> for the addComponent monitor.
 *  
 * @author Mauro Talevi
 * @see org.picocontainer.defaults.ComponentAdapterFactory
 * @see org.picocontainer.defaults.ComponentMonitorStrategy
 * @since 1.2
 */
@Deprecated
public abstract class MonitoringComponentAdapterFactory implements ComponentAdapterFactory, Serializable {

    protected MonitoringComponentAdapterFactory() {
    }

}
