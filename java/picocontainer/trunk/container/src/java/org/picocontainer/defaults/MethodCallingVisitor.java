/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.defaults;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.monitors.NullComponentMonitor;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * A PicoVisitor implementation, that calls methods on the components of a specific type.
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @since 1.2
 */
public class MethodCallingVisitor extends TraversalCheckingVisitor implements Serializable {

    private transient Method method;
    private Class type;
    private boolean visitInInstantiationOrder;
    private List componentInstances;
    private ComponentMonitor componentMonitor;

    public MethodCallingVisitor(Method method, Class ofType, boolean visitInInstantiationOrder, ComponentMonitor componentMonitor) {
        this.method = method;
        this.type = ofType;
        this.visitInInstantiationOrder = visitInInstantiationOrder;
        this.componentMonitor = componentMonitor;
        this.componentInstances = new ArrayList();
    }

    public MethodCallingVisitor(Method method, Class ofType, boolean visitInInstantiationOrder) {
        this(method, ofType, visitInInstantiationOrder, NullComponentMonitor.getInstance());
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
                try {
                    componentMonitor.invoking(method, o);
                    long startTime = System.currentTimeMillis();
                    method.invoke(o, null);
                    componentMonitor.invoked(method, o, System.currentTimeMillis() - startTime);
                } catch (IllegalArgumentException e) {
                    componentMonitor.invocationFailed(method, o, e);
                    throw new PicoIntrospectionException("Can't call " + method.getName() + " on " + o, e);
                } catch (IllegalAccessException e) {
                    componentMonitor.invocationFailed(method, o, e);
                    throw new PicoIntrospectionException("Can't call " + method.getName() + " on " + o, e);
                } catch (InvocationTargetException e) {
                    componentMonitor.invocationFailed(method, o, e);
                    throw new PicoIntrospectionException("Failed when calling " + method.getName() + " on " + o, e
                            .getTargetException());
                }
            }
        } finally {
            componentInstances.clear();
        }
        return Void.TYPE;
    }

    public void visitContainer(PicoContainer pico) {
        super.visitContainer(pico);
        componentInstances.addAll(pico.getComponentInstancesOfType(type));
    }
}
