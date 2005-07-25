/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.picocontainer.defaults;

import junit.framework.TestCase;

import org.picocontainer.ComponentMonitorStrategy;
import org.picocontainer.monitors.ConsoleComponentMonitor;
import org.picocontainer.monitors.NullComponentMonitor;

/**
 * @author Mauro Talevi
 */
public class DefaultComponentMonitorStrategyTestCase extends TestCase {

    public void testMonitorStrategyWithDefaultMonitor() {
        ComponentMonitorStrategy strategy = new DefaultComponentMonitorStrategy();
        assertEquals( NullComponentMonitor.class.getName(), strategy.currentMonitor().getClass().getName() );
    }
    
    public void testMonitorStrategyCanChangeMonitor() {
        ComponentMonitorStrategy strategy = new DefaultComponentMonitorStrategy();
        strategy.changeMonitor(new ConsoleComponentMonitor(System.out));
        assertEquals( ConsoleComponentMonitor.class.getName(), strategy.currentMonitor().getClass().getName() );
    }

    public void testMonitorStrategyThrowsExceptionForInvalidMonitor() {
        try {
            ComponentMonitorStrategy strategy = new DefaultComponentMonitorStrategy(null);
        } catch ( NullPointerException e) {
            assertEquals( "NPE", e.getMessage(), "monitor");
        }
    }

    public void testToString() {
        ComponentMonitorStrategy strategy = new DefaultComponentMonitorStrategy();
        assertEquals(NullComponentMonitor.class.getName(), strategy.toString() );
    }
    
}