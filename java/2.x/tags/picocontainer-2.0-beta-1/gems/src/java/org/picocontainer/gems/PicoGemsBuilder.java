/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.gems;

import org.picocontainer.gems.behaviors.ImplementationHidingBehaviorFactory;
import org.picocontainer.gems.monitors.Log4JComponentMonitor;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.BehaviorFactory;

public class PicoGemsBuilder {

    public static BehaviorFactory IMPL_HIDING() {
        return new ImplementationHidingBehaviorFactory();
    }

    public static ComponentMonitor LOG4J() {
        return new Log4JComponentMonitor();
    }



}
