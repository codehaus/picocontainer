/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Mauro Talevi                                             *
 *****************************************************************************/
package org.picocontainer;


/**
 * <p>
 * Interface to control the monitoring strategy used by {@link PicoContainer}.
 * The strategy sets the {@link ComponentMonitor} used by the {@link ComponentAdapter}s.
 * The default strategy is to have no monitoring.
 * </p>
 * <p>
 * The monitor strategy can be driven by the {@link MutablePicoContainer}, 
 * either when instantiated or changed via the 
 * {@link MutablePicoContainer#changeMonitorStrategy(ComponentMonitorStrategy)}.
 * </p>
 * 
 * @author Mauro Talevi
 * @version $Revision:  $
 * @see ComponentMonitor
 * @since 1.2 
 */
public interface ComponentMonitorStrategy {

    /**
     * Returns the current ComponentMonitor used by the strategy
     * @return The ComponentMonitor
     */
    ComponentMonitor currentMonitor();
    
    /**
     * Changes the current ComponentMonitor used by the strategy
     * @param monitor the new ComponentMonitor
     */
    void changeMonitor(ComponentMonitor monitor);
        
}
