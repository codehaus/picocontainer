/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.defaults;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.Disposable;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.Startable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @version $Revision$
 * @since 1.1
 */
public class LifecycleVisitor extends AbstractPicoVisitor {

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

    private final Method method;
    private final Class type;
    private final boolean visitInInstantiationOrder;
    private final List componentInstances;
    private ComponentMonitor componentMonitor;

    // TODO - in reality this ctor is unlikely to be called, the other ctor is prevalent in the ..
    // public static void start|stop|dispose(Object node) methods below.
    // next on the list is to inject a "LifecycleManager" to DPC (which may or may not use a PicoVisitor)
    // Aslak and Paul have talked about this, and it's been on the mail list as a proposal -
    // Refer "LifecycleManager - pluggable", 29/10/2004 (mail)
    public LifecycleVisitor(Method method, Class ofType, boolean visitInInstantiationOrder, ComponentMonitor componentMonitor) {
        this.method = method;
        this.type = ofType;
        this.visitInInstantiationOrder = visitInInstantiationOrder;
        this.componentMonitor = componentMonitor;
        this.componentInstances = new ArrayList();
    }

    public LifecycleVisitor(Method method, Class ofType, boolean visiInInstantiationOrder) {
        this(method, ofType, visiInInstantiationOrder, NullComponentMonitor.getInstance());
    }

    public Object traverse(Object node) {
        componentInstances.clear();
        try {
            super.traverse(node);
            if (!visitInInstantiationOrder) {
                Collections.reverse(componentInstances);
            }
            for (Iterator iterator = componentInstances.iterator(); iterator.hasNext();) {
                Object o = iterator.next();
//                ComponentInteraction componentInteraction = null;
                try {
                    //                  componentInteraction = componentMonitor.invoking(method, o, "Lifecycle Visitation");
                    method.invoke(o, null);
                    //                componentInteraction.complete();
                } catch (IllegalArgumentException e) {
                    //              componentInteraction.failed(e);
                    throw new PicoIntrospectionException("Can't call " + method.getName() + " on " + o, e);
                } catch (IllegalAccessException e) {
                    //            componentInteraction.failed(e);
                    throw new PicoIntrospectionException("Can't call " + method.getName() + " on " + o, e);
                } catch (InvocationTargetException e) {
                    //          componentInteraction.failed(e);
                    throw new PicoIntrospectionException("Failed when calling " + method.getName() + " on " + o, e.getTargetException());
                }
            }
        } finally {
            componentInstances.clear();
        }
        return Void.TYPE;
    }

    public void visitContainer(PicoContainer pico) {
        checkTraversal();
        componentInstances.addAll(pico.getComponentInstancesOfType(type));
    }

    public void visitComponentAdapter(ComponentAdapter componentAdapter) {
        checkTraversal();
    }

    public void visitParameter(Parameter parameter) {
        checkTraversal();
    }

    /**
     * Invoke the standard PicoContainer lifecycle for {@link Startable#start()}.
     *
     * @param node The node to start the traversal.
     */
    public static void start(Object node) {
        new LifecycleVisitor(START, Startable.class, true).traverse(node);
        ;
    }

    /**
     * Invoke the standard PicoContainer lifecycle for {@link Startable#stop()}.
     *
     * @param node The node to start the traversal.
     */
    public static void stop(Object node) {
        new LifecycleVisitor(STOP, Startable.class, false).traverse(node);
        ;
    }

    /**
     * Invoke the standard PicoContainer lifecycle for {@link Disposable#dispose()}.
     *
     * @param node The node to start the traversal.
     */
    public static void dispose(Object node) {
        new LifecycleVisitor(DISPOSE, Disposable.class, false).traverse(node);
        ;
    }

}
