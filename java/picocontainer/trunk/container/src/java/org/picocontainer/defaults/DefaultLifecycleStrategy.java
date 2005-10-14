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

import java.io.Serializable;
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
public class DefaultLifecycleStrategy implements LifecycleStrategy, Serializable {

    private ComponentMonitor componentMonitor;
    private static Method start, stop, dispose;

    {
        try {
            start = Startable.class.getMethod("start", new Class[0]);
            stop = Startable.class.getMethod("stop", new Class[0]);
            dispose = Disposable.class.getMethod("dispose", new Class[0]);
        } catch (NoSuchMethodException e) {
        }
    }

    public DefaultLifecycleStrategy(ComponentMonitor componentMonitor) {
        this.componentMonitor = componentMonitor;

    }

    public void start(Object component) {
        if (component != null && component instanceof Startable) {
            long str = System.currentTimeMillis();
            componentMonitor.invoking(start, component);
            ((Startable) component).start();
            componentMonitor.invoked(start, component, System.currentTimeMillis() - str);
        }
    }

    public void stop(Object component) {
        if (component != null && component instanceof Startable) {
            long str = System.currentTimeMillis();
            componentMonitor.invoking(stop, component);
            ((Startable) component).stop();
            componentMonitor.invoked(stop, component, System.currentTimeMillis() - str);
        }
    }

    public void dispose(Object component) {
        if (component != null && component instanceof Disposable) {
            long str = System.currentTimeMillis();
            componentMonitor.invoking(dispose, component);
            ((Disposable) component).dispose();
            componentMonitor.invoked(dispose, component, System.currentTimeMillis() - str);
        }
    }

    public boolean hasLifecycle(Class type) {
        return Startable.class.isAssignableFrom(type) || Disposable.class.isAssignableFrom(type);
    }

}
