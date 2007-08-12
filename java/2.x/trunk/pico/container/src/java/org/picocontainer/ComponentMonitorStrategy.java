/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.picocontainer;


/**
 * <p>
 * Interface responsible for changing monitoring strategy.
 * It may be implemented by {@link org.picocontainer.PicoContainer containers} and
 * single {@link org.picocontainer.ComponentAdapter component adapters}.
 * The choice of supporting the monitor strategy is left to the
 * implementers of the container and adapters.
 * </p>
 *
 * @author Paul Hammant
 * @author Joerg Schaible
 * @author Mauro Talevi
 */
public interface ComponentMonitorStrategy {

    /**
     * Changes the component monitor used
     * @param monitor the new ComponentMonitor to use
     */
    void changeMonitor(ComponentMonitor monitor);

    /**
     * Returns the monitor currently used
     * @return The ComponentMonitor currently used
     */
    ComponentMonitor currentMonitor();

}
