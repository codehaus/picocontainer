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

import org.aopalliance.intercept.MethodInterceptor;
import org.nanocontainer.nanoaop.AspectsManager;
import org.nanocontainer.nanoaop.ClassPointcut;
import org.nanocontainer.nanoaop.ComponentPointcut;
import org.nanocontainer.nanoaop.MethodPointcut;
import org.nanocontainer.nanoaop.PointcutsFactory;
import org.nanocontainer.nanoaop.defaults.ContainerLoader;
import org.picocontainer.PicoContainer;

import dynaop.Aspects;
import dynaop.Interceptor;
import dynaop.InterceptorFactory;
import dynaop.MixinFactory;
import dynaop.Pointcuts;
import dynaop.ProxyFactory;

/**
 * @author Stephen Molitor
 */
public class DynaopAspectsManager implements AspectsManager {

    private final ContainerLoader containerLoader = new ContainerLoader();
    private final PicoContainer container = PicoContainerProxy.create(containerLoader);
    private final Aspects containerAspects;
    private final Map componentAspects = new HashMap();
    private final PointcutsFactory pointcutsFactory = new DynaopPointcutsFactory();

    public DynaopAspectsManager(Aspects containerAspects) {
        this.containerAspects = containerAspects;
    }

    public DynaopAspectsManager() {
        this(new Aspects());
    }

    public void registerInterceptor(ClassPointcut classPointcut, MethodPointcut methodPointcut,
            ComponentPointcut interceptorComponent) {
        containerAspects.interceptor(getClassPointcut(classPointcut), getMethodPointcut(methodPointcut),
                createInterceptorFactory(interceptorComponent));
    }

    public void registerInterceptor(ClassPointcut classPointcut, MethodPointcut methodPointcut,
            MethodInterceptor interceptor) {
        containerAspects.interceptor(getClassPointcut(classPointcut), getMethodPointcut(methodPointcut),
                createInterceptor(interceptor));
    }

    public void registerInterceptor(ComponentPointcut componentPointcut, MethodPointcut methodPointcut,
            ComponentPointcut interceptorComponent) {
        getComponentAspects(componentPointcut).interceptor(Pointcuts.ALL_CLASSES, getMethodPointcut(methodPointcut),
                createInterceptorFactory(interceptorComponent));
    }

    public void registerInterceptor(ComponentPointcut componentPointcut, MethodPointcut methodPointcut,
            MethodInterceptor interceptor) {
        getComponentAspects(componentPointcut).interceptor(Pointcuts.ALL_CLASSES, getMethodPointcut(methodPointcut),
                createInterceptor(interceptor));
    }

    public void registerMixin(ClassPointcut classPointcut, Class mixinClass) {
        containerAspects.mixin(getClassPointcut(classPointcut), mixinClass, null);
    }

    public void registerMixin(ClassPointcut classPointcut, Class[] interfaces, Class mixinClass) {
        containerAspects.mixin(getClassPointcut(classPointcut), interfaces, mixinClass, null);
    }

    public void registerMixin(ClassPointcut classPointcut, Class[] interfaces, ComponentPointcut mixinComponent) {
        containerAspects.mixin(getClassPointcut(classPointcut), interfaces, createMixinFactory(mixinComponent));
    }

    public void registerMixin(ComponentPointcut componentPointcut, Class mixinClass) {
        getComponentAspects(componentPointcut).mixin(Pointcuts.ALL_CLASSES, mixinClass, null);
    }

    public void registerMixin(ComponentPointcut componentPointcut, Class[] interfaces, Class mixinClass) {
        getComponentAspects(componentPointcut).mixin(Pointcuts.ALL_CLASSES, interfaces, mixinClass, null);
    }

    public void registerMixin(ComponentPointcut componentPointcut, Class[] interfaces, ComponentPointcut mixinComponent) {
        getComponentAspects(componentPointcut).mixin(Pointcuts.ALL_CLASSES, interfaces,
                createMixinFactory(mixinComponent));
    }

    public Object applyAspects(Object componentKey, Object component, PicoContainer container) {
        containerLoader.setContainer(container);
        Object proxy = ProxyFactory.getInstance(containerAspects).wrap(component);
        return ProxyFactory.getInstance(getComponentAspects(componentKey)).wrap(proxy);
    }

    public PointcutsFactory getPointcutsFactory() {
        return pointcutsFactory;
    }

    private Aspects getComponentAspects(Object componentKey) {
        Aspects aspects = (Aspects) componentAspects.get(componentKey);
        if (aspects == null) {
            aspects = new Aspects();
            componentAspects.put(componentKey, aspects);
        }
        return aspects;
    }

    private Aspects getComponentAspects(ComponentPointcut componentPointcut) {
        return getComponentAspects(componentPointcut.getComponentKey());
    }

    private dynaop.ClassPointcut getClassPointcut(ClassPointcut classPointcut) {
        return (dynaop.ClassPointcut) classPointcut;
    }

    private dynaop.MethodPointcut getMethodPointcut(MethodPointcut methodPointcut) {
        return (dynaop.MethodPointcut) methodPointcut;
    }

    private Interceptor createInterceptor(MethodInterceptor methodInterceptor) {
        return new MethodInterceptorAdapter(methodInterceptor);
    }

    private InterceptorFactory createInterceptorFactory(ComponentPointcut interceptorComponent) {
        return new ContainerSuppliedInterceptorFactory(container, interceptorComponent.getComponentKey());
    }

    private MixinFactory createMixinFactory(ComponentPointcut mixinComponent) {
        return new ContainerSuppliedMixinFactory(container, mixinComponent.getComponentKey());
    }

}