/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.lifecycle;

import org.picocontainer.LifecycleStrategy;

/**
 * Allow for use of alternate LifecycleStrategy strategies to be used
 * at the same time. A component can be started/stopped/disposed according
 * to *any* of the supplied LifecycleStrategy instances.
 *
 * @author Paul Hammant
 */
public class CompositeLifecycleStrategy implements LifecycleStrategy {

    private final StartableLifecycleStrategy[] alternateStrategies;

    public CompositeLifecycleStrategy(StartableLifecycleStrategy... alternateStrategies) {
        this.alternateStrategies = alternateStrategies;
    }

    public void start(Object component) {
        for (StartableLifecycleStrategy lifecycleStrategy : alternateStrategies) {
            lifecycleStrategy.start(component);
        }
    }

    public void stop(Object component) {
        for (StartableLifecycleStrategy lifecycleStrategy : alternateStrategies) {
            lifecycleStrategy.stop(component);
        }
    }

    public void dispose(Object component) {
        for (StartableLifecycleStrategy lifecycleStrategy : alternateStrategies) {
            lifecycleStrategy.dispose(component);
        }
    }

    public boolean hasLifecycle(Class<?> type) {
        for (StartableLifecycleStrategy lifecycleStrategy : alternateStrategies) {
            if (lifecycleStrategy.hasLifecycle(type)) {
                return true;
            }
        }
        return false;
    }
}
