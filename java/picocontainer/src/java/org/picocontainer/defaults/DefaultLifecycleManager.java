/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/

package org.picocontainer.defaults;

import org.picocontainer.Disposable;
import org.picocontainer.LifecycleManager;
import org.picocontainer.PicoContainer;
import org.picocontainer.Startable;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

/**
 * This class implements the default lifecycle based on
 * <ul>
 * <li>{@link org.picocontainer.Startable#start()}</li>
 * <li>{@link org.picocontainer.Startable#stop()}</li>
 * <li>{@link org.picocontainer.Disposable#dispose()}</li>
 * </ul>
 *
 * It also allows custom lifecycle strategies to be plugged in via {@link #DefaultLifecycleManager(org.picocontainer.PicoVisitor, org.picocontainer.PicoVisitor, org.picocontainer.PicoVisitor)}.
 *
 * @author Paul Hammant
 * @author J&ouml;rg Schaible
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class DefaultLifecycleManager implements LifecycleManager, Serializable {

    private ComponentMonitor componentMonitor;

    protected static Method startMethod = null;
    protected static Method stopMethod = null;
    protected static Method disposeMethod = null;

    private static Object[] emptyArray = new Object[0];

    static {
        try {
            startMethod = Startable.class.getMethod("start", new Class[0]);
            stopMethod = Startable.class.getMethod("stop", new Class[0]);
            disposeMethod = Disposable.class.getMethod("dispose", new Class[0]);
        } catch (NoSuchMethodException e) {
        }
    }

    public DefaultLifecycleManager(ComponentMonitor componentMonitor) {
        this.componentMonitor = componentMonitor;
    }

    public DefaultLifecycleManager() {
        this.componentMonitor = NullComponentMonitor.getInstance();
    }

    public void start(PicoContainer node) {
        List startables = node.getComponentInstancesOfType(Startable.class);
        for (int i = 0; i < startables.size(); i++) {
            doMethod(startMethod ,startables.get(i));
        }
    }

    public void stop(PicoContainer node) {
        List startables = node.getComponentInstancesOfType(Startable.class);
        for (int i = startables.size() -1 ; 0 <= i; i--) {
            doMethod(stopMethod ,startables.get(i));
        }
    }

    public void dispose(PicoContainer node) {
        List disposables = node.getComponentInstancesOfType(Disposable.class);
        for (int i = disposables.size() -1 ; 0 <= i; i--) {
            doMethod(disposeMethod, disposables.get(i));
        }
    }

    protected void doMethod(Method method, Object instance) {
        componentMonitor.invoking(method, instance);
        try {
            long beginTime = System.currentTimeMillis();
            method.invoke(instance, emptyArray);
            componentMonitor.invoked(method, instance, System.currentTimeMillis() - beginTime);
        } catch (Exception e) {
            invocationFailed(method, instance, e);
        }
    }

    protected void invocationFailed(Method method, Object instance, Exception e) {
        componentMonitor.invocationFailed(method, instance, e);
        throw new org.picocontainer.PicoInitializationException("Method '" + method.getName()
                + "' failed on instance '" + instance+ "' for reason '" + e.getMessage() + "'", e);
    }

}
