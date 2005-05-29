/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.defaults;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.io.Serializable;


/**
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @version $Revision$
 * @since 1.1
 */
public class LifecycleVisitor extends AbstractPicoVisitor implements Serializable {

    private transient Method method;
    private Class type;
    private boolean visitInInstantiationOrder;
    private List componentInstances;
    private ComponentMonitor componentMonitor;

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

}
