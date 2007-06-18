/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.behaviors;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.ComponentCharacteristic;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.ComponentCharacteristics;
import org.picocontainer.behaviors.AbstractBehaviorFactory;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class SynchronizedBehaviorFactory extends AbstractBehaviorFactory {

    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, ComponentCharacteristic componentCharacteristic, Object componentKey, Class componentImplementation, Parameter... parameters) {
        SynchronizedBehavior synchronizedBehavior = new SynchronizedBehavior(super.createComponentAdapter(
            componentMonitor,
            lifecycleStrategy,
            componentCharacteristic,
            componentKey,
            componentImplementation,
            parameters));
        ComponentCharacteristics.THREAD_SAFE.setProcessedIn(componentCharacteristic);
        return synchronizedBehavior;
    }
}
