/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.defaults;

import org.picocontainer.ComponentAdapter;
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

    public LifecycleVisitor(Method method, Class ofType, boolean visitInInstantiationOrder) {
        this.method = method;
        this.type = ofType;
        this.visitInInstantiationOrder = visitInInstantiationOrder;
        this.componentInstances = new ArrayList();
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
                long startTime = System.currentTimeMillis();
                try {
//                    if (PicoContainer.SHOULD_LOG) {
//                        System.out.print("PICO: Calling " + method.toString() + " on " + o + "... ");
//                        System.out.flush();
//                    }
                    method.invoke(o, null);
                } catch (IllegalArgumentException e) {
                    throw new PicoIntrospectionException("Can't call " + method.getName() + " on " + o, e);
                } catch (IllegalAccessException e) {
                    throw new PicoIntrospectionException("Can't call " + method.getName() + " on " + o, e);
                } catch (InvocationTargetException e) {
                    throw new PicoIntrospectionException("Failed when calling " + method.getName() + " on " + o, e.getTargetException());
                } finally {
//                    if (PicoContainer.SHOULD_LOG) {
//                        long endTime = System.currentTimeMillis();
//                        System.out.println("[" + (endTime - startTime) + "ms]");
//                    }
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
     * @param node The node to start the traversal.
     */
    public static void start(Object node) {
        new LifecycleVisitor(START, Startable.class, true).traverse(node);;
    }
    
    /**
     * Invoke the standard PicoContainer lifecycle for {@link Startable#stop()}.
     * @param node The node to start the traversal.
     */
    public static void stop(Object node) {
        new LifecycleVisitor(STOP, Startable.class, false).traverse(node);;
    }
    
    /**
     * Invoke the standard PicoContainer lifecycle for {@link Disposable#dispose()}.
     * @param node The node to start the traversal.
     */
    public static void dispose(Object node) {
        new LifecycleVisitor(DISPOSE, Disposable.class, false).traverse(node);;
    }

}
