/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.nanoaop.dynaop;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.List;

import org.nanocontainer.nanoaop.AdvisablePicoContainer;
import org.nanocontainer.nanoaop.ClassPointcut;
import org.nanocontainer.nanoaop.ComponentPointcut;
import org.nanocontainer.nanoaop.Interceptor;
import org.nanocontainer.nanoaop.MethodPointcut;
import org.nanocontainer.nanoaop.PointcutFactory;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.defaults.DefaultPicoContainer;

import dynaop.Aspects;
import dynaop.Pointcuts;
import dynaop.ProxyFactory;

/**
 * @author Stephen Molitor
 */
public class DynaopAdvisablePicoContainer extends DelegatingContainer implements AdvisablePicoContainer {

    private final Aspects containerAspects = new Aspects();
    private final Map byComponentAspects = new HashMap();
    private final PointcutFactory pointcutFactory = new DynaopPointcutFactory();

    public DynaopAdvisablePicoContainer(MutablePicoContainer delegate) {
        super(delegate);
    }

    public DynaopAdvisablePicoContainer() {
        this(new DefaultPicoContainer());
    }

    public PointcutFactory getPointcutFactory() {
        return pointcutFactory;
    }

    public void addInterceptor(ClassPointcut classPointcut, MethodPointcut methodPointcut, Interceptor interceptor) {
        containerAspects.interceptor((dynaop.ClassPointcut) classPointcut, (dynaop.MethodPointcut) methodPointcut,
                new DynaopInterceptor(interceptor));
    }

    public void addInterceptor(ComponentPointcut componentPointcut, MethodPointcut methodPointcut,
            Interceptor interceptor) {
        getComponentAspects(componentPointcut.getComponentKey()).interceptor(Pointcuts.ALL_CLASSES,
                (dynaop.MethodPointcut) methodPointcut, new DynaopInterceptor(interceptor));
    }

    public void addInterceptor(ClassPointcut classPointcut, MethodPointcut methodPointcut,
            ComponentPointcut interceptorComponentPointcut) {
        containerAspects.interceptor((dynaop.ClassPointcut) classPointcut, (dynaop.MethodPointcut) methodPointcut,
                new ContainerSuppliedInterceptorFactoy(this, interceptorComponentPointcut.getComponentKey()));
    }

    public void addInterceptor(ComponentPointcut componentPointcut, MethodPointcut methodPointcut,
            ComponentPointcut interceptorComponentPointcut) {
        getComponentAspects(componentPointcut.getComponentKey()).interceptor(Pointcuts.ALL_CLASSES,
                (dynaop.MethodPointcut) methodPointcut,
                new ContainerSuppliedInterceptorFactoy(this, interceptorComponentPointcut.getComponentKey()));
    }

    public Object getComponentInstance(Object componentKey) {
        Object component = super.getComponentInstance(componentKey);
        component = ProxyFactory.getInstance(containerAspects).wrap(component);
        return ProxyFactory.getInstance(getComponentAspects(componentKey)).wrap(component);
    }

    private Aspects getComponentAspects(Object componentKey) {
        Aspects aspects = (Aspects) byComponentAspects.get(componentKey);
        if (aspects == null) {
            aspects = new Aspects();
            byComponentAspects.put(componentKey, aspects);
        }
        return aspects;
    }
}