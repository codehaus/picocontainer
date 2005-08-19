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

import org.picocontainer.ComponentMonitor;
import org.picocontainer.Disposable;
import org.picocontainer.PicoContainer;
import org.picocontainer.Startable;
import org.picocontainer.monitors.NullComponentMonitor;

import java.lang.reflect.Method;


/**
 * This class implements the default lifecycle. It is based on
 * <ul>
 * <li>{@link org.picocontainer.Startable#start()}</li>
 * <li>{@link org.picocontainer.Startable#stop()}</li>
 * <li>{@link org.picocontainer.Disposable#dispose()}</li>
 * </ul>
 * This LifecycleManager will call the lifecycle methods of the {@link PicoContainer}, which is responsible for
 * propagating the lifecycle to its components and children.
 * 
 * @author Paul Hammant
 * @author J&ouml;rg Schaible
 * @author Aslak Helles&oslash;y
 * @since 1.2
 */
public class DefaultLifecycleManager extends CustomLifecycleManager {

    protected static Method startMethod = null;
    protected static Method stopMethod = null;
    protected static Method disposeMethod = null;

    static {
        try {
            startMethod = Startable.class.getMethod("start", new Class[0]);
            stopMethod = Startable.class.getMethod("stop", new Class[0]);
            disposeMethod = Disposable.class.getMethod("dispose", new Class[0]);
        } catch (NoSuchMethodException e) {
            throw new InternalError(e.getMessage());
        }
    }

    public DefaultLifecycleManager(ComponentMonitor componentMonitor) {
        super(startMethod, stopMethod, disposeMethod, componentMonitor);
    }

    public DefaultLifecycleManager() {
        this(NullComponentMonitor.getInstance());
    }

}
