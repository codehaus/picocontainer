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

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class DefaultLifecycleManager implements LifecycleManager, Serializable {

    private static final Method START;
    private static final Method STOP;
    private static final Method DISPOSE;

    static {
        try {
            START = Startable.class.getMethod("start", null);
            STOP = Startable.class.getMethod("stop", null);
            DISPOSE = Disposable.class.getMethod("dispose", null);
        } catch (NoSuchMethodException e) {
            ///CLOVER:OFF
            throw new InternalError(e.getMessage());
            ///CLOVER:ON
        }
    }

    private ComponentMonitor componentMonitor;

    public DefaultLifecycleManager(ComponentMonitor componentMonitor) {
        this.componentMonitor = componentMonitor;
    }

    public DefaultLifecycleManager() {
        componentMonitor = NullComponentMonitor.getInstance();
    }

    public void start(PicoContainer node) {
        new LifecycleVisitor(START, Startable.class, true, componentMonitor).traverse(node);
    }

    public void stop(PicoContainer node) {
        new LifecycleVisitor(STOP, Startable.class, false, componentMonitor).traverse(node);
    }

    public void dispose(PicoContainer node) {
        new LifecycleVisitor(DISPOSE, Disposable.class, false, componentMonitor).traverse(node);
    }
}
