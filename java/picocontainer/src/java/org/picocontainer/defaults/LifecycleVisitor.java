/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.defaults;

import org.picocontainer.Parameter;
import org.picocontainer.PicoVisitor;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.Startable;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Disposable;
import org.picocontainer.ComponentAdapter;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @version $Revision$
 */
public class LifecycleVisitor implements PicoVisitor {
    public static final PicoVisitor STARTER;
    public static final PicoVisitor STOPPER;
    public static final PicoVisitor DISPOSER;
    static {
        try {
            STARTER = new LifecycleVisitor(Startable.class.getMethod("start", null), Startable.class, true);
            STOPPER = new LifecycleVisitor(Startable.class.getMethod("stop", null), Startable.class, false);
            DISPOSER = new LifecycleVisitor(Disposable.class.getMethod("dispose", null), Disposable.class, false) {
                public void visitContainer(PicoContainer pico) {
                    super.visitContainer(pico);
                    // TODO: Never true for DPC, fix this!
                    if (pico.getParent() instanceof MutablePicoContainer) {
                        ((MutablePicoContainer) pico.getParent()).removeChildContainer(pico);
                    }
                }
            };
        } catch (NoSuchMethodException e) {
            ///CLOVER:OFF
            throw new InternalError(e.getMessage());
            ///CLOVER:ON
        }
    }

    private final Method method;
    private final Class type;
    private final boolean visitInInstantiationOrder;

    public LifecycleVisitor(Method method, Class ofType, boolean visitInInstantiationOrder) {
        this.method = method;
        this.type = ofType;
        this.visitInInstantiationOrder = visitInInstantiationOrder;
    }

    public void visitContainer(PicoContainer pico) {
        List componentInstances = new LinkedList(pico.getComponentInstancesOfType(type));
        if(visitInInstantiationOrder) {
            visitComponentInstances(componentInstances);
        } else {
            Collections.reverse(componentInstances);
            visitComponentInstances(componentInstances);
        }
    }

    public void visitComponentAdapter(ComponentAdapter componentAdapter) {
    }

    public void visitParameter(Parameter parameter) {
    }

    private void visitComponentInstances(List componentInstances) {
        for (Iterator iterator = componentInstances.iterator(); iterator.hasNext();) {
            Object o = iterator.next();
            try {
                method.invoke(o, null);
            } catch (IllegalArgumentException e) {
                throw new PicoIntrospectionException("Can't call " + method.getName() + " on " + o, e);
            } catch (IllegalAccessException e) {
                throw new PicoIntrospectionException("Can't call " + method.getName() + " on " + o, e);
            } catch (InvocationTargetException e) {
                throw new PicoIntrospectionException("Failed when calling " + method.getName() + " on " + o, e.getTargetException());
            }
        }
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.PicoVisitor#isReverseTraversal()
     */
    public boolean isReverseTraversal() {
        return !visitInInstantiationOrder;
    }
}
