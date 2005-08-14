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
 * A PicoVisitor for the life cycle of the components.
 * 
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @since 1.1
 * @deprecated since 1.2 in favour of {@link org.picocontainer.LifecycleManager}
 */
public class LifecycleVisitor extends MethodCallingVisitor {
    private static final Method START;
    private static final Method STOP;
    private static final Method DISPOSE;
    static {
        try {
            START = Startable.class.getMethod("start", null);
            STOP = Startable.class.getMethod("stop", null);
            DISPOSE = Disposable.class.getMethod("dispose", null);
        } catch (NoSuchMethodException e) {
            // /CLOVER:OFF
            throw new InternalError(e.getMessage());
            // /CLOVER:ON
        }
    }

    /**
     * Construct a LifecycleVisitor.
     * 
     * @param method the method to call
     * @param ofType the component type
     * @param visitInInstantiationOrder flag for the visiting order
     * @param componentMonitor the {@link ComponentMonitor} to use
     * @deprecated since 1.2 in favour of {@link org.picocontainer.LifecycleManager}
     */
    public LifecycleVisitor(Method method, Class ofType, boolean visitInInstantiationOrder, ComponentMonitor componentMonitor) {
        super(method, ofType, visitInInstantiationOrder, componentMonitor);
    }

    /**
     * Construct a LifecycleVisitor.
     * 
     * @param method the method to call
     * @param ofType the component type
     * @param visitInInstantiationOrder flag for the visiting order
     * @deprecated since 1.2 in favour of {@link org.picocontainer.LifecycleManager}
     */
    public LifecycleVisitor(Method method, Class ofType, boolean visitInInstantiationOrder) {
        super(method, ofType, visitInInstantiationOrder);
    }

    /**
     * Invoke the standard PicoContainer lifecycle for {@link Startable#start()}.
     * 
     * @param node The node to start the traversal.
     * @deprecated since 1.2 in favour of {@link org.picocontainer.LifecycleManager}
     */
    public static void start(Object node) {
        new LifecycleVisitor(START, Startable.class, true).traverse(node);
    }

    /**
     * Invoke the standard PicoContainer lifecycle for {@link Startable#stop()}.
     * 
     * @param node The node to start the traversal.
     * @deprecated since 1.2 in favour of {@link org.picocontainer.LifecycleManager}
     */
    public static void stop(Object node) {
        new LifecycleVisitor(STOP, Startable.class, false).traverse(node);
    }

    /**
     * Invoke the standard PicoContainer lifecycle for {@link Disposable#dispose()}.
     * 
     * @param node The node to start the traversal.
     * @deprecated since 1.2 in favour of {@link org.picocontainer.LifecycleManager}
     */
    public static void dispose(Object node) {
        new LifecycleVisitor(DISPOSE, Disposable.class, false).traverse(node);
    }

}
