/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.defaults;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.Disposable;
import org.picocontainer.Startable;

import java.lang.reflect.Method;

/**
 * Default lifecycle strategy.  Starts and stops component if Startable,
 * and disposes it if Disposable.
 *
 * @author Mauro Talevi
 * @author J&ouml;rg Schaible
 * @see Startable
 * @see Disposable
 */
public class DefaultLifecycleStrategy extends AbstractMonitoringLifecylceStrategy {

    private static Method start, stop, dispose;
    {
        try {
            start = Startable.class.getMethod("start", null);
            stop = Startable.class.getMethod("stop", null);
            dispose = Disposable.class.getMethod("dispose", null);
        } catch (NoSuchMethodException e) {
        }
    }

    public DefaultLifecycleStrategy(ComponentMonitor monitor) {
        super(monitor);
    }

    public void start(Object component) {
        if (component != null && component instanceof Startable) {
            long str = System.currentTimeMillis();
            currentMonitor().invoking(start, component);
            ((Startable) component).start();
            currentMonitor().invoked(start, component, System.currentTimeMillis() - str);
        }
    }

    public void stop(Object component) {
        if (component != null && component instanceof Startable) {
            long str = System.currentTimeMillis();
            currentMonitor().invoking(stop, component);
            ((Startable) component).stop();
            currentMonitor().invoked(stop, component, System.currentTimeMillis() - str);
        }
    }

    public void dispose(Object component) {
        if (component != null && component instanceof Disposable) {
            long str = System.currentTimeMillis();
            currentMonitor().invoking(dispose, component);
            ((Disposable) component).dispose();
            currentMonitor().invoked(dispose, component, System.currentTimeMillis() - str);
        }
    }

    public boolean hasLifecycle(Class type) {
        return Startable.class.isAssignableFrom(type) || Disposable.class.isAssignableFrom(type);
    }
}
