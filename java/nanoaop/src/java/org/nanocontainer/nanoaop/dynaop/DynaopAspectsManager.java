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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import dynaop.ProxyFactory;

/**
 * @author Stephen Molitor
 */
public class DynaopAspectsManager implements AspectsManager {

    private final ContainerLoader containerLoader = new ContainerLoader();
    private final PicoContainer container = PicoContainerProxy.create(containerLoader);
    private final Aspects containerAspects;
    private final List componentAspects = new ArrayList();
    private final PointcutsFactory pointcutsFactory;

    public DynaopAspectsManager(Aspects containerAspects, PointcutsFactory pointcutsFactory) {
        this.containerAspects = containerAspects;
        this.pointcutsFactory = pointcutsFactory;
    }

    public DynaopAspectsManager(Aspects containerAspects) {
        this(containerAspects, new DynaopPointcutsFactory());
    }
    
    public DynaopAspectsManager(PointcutsFactory pointcutsFactory) {
        this(new Aspects(), pointcutsFactory);
    }

    public DynaopAspectsManager() {
        this(new Aspects());
    }

    public void registerInterceptor(ClassPointcut classPointcut, MethodPointcut methodPointcut,
            Object interceptorComponentKey) {
        containerAspects.interceptor(getClassPointcut(classPointcut), getMethodPointcut(methodPointcut),
                createInterceptorFactory(interceptorComponentKey));
    }

    public void registerInterceptor(ClassPointcut classPointcut, MethodPointcut methodPointcut,
            MethodInterceptor interceptor) {
        containerAspects.interceptor(getClassPointcut(classPointcut), getMethodPointcut(methodPointcut),
                createInterceptor(interceptor));
    }

    public void registerInterceptor(ComponentPointcut componentPointcut, MethodPointcut methodPointcut,
            Object interceptorComponentKey) {
        componentAspects.add(new InterceptorComponentAspect(componentPointcut, getMethodPointcut(methodPointcut),
                createInterceptorFactory(interceptorComponentKey)));
    }

    public void registerInterceptor(ComponentPointcut componentPointcut, MethodPointcut methodPointcut,
            MethodInterceptor interceptor) {
        componentAspects.add(new InterceptorComponentAspect(componentPointcut, getMethodPointcut(methodPointcut),
                createInterceptor(interceptor)));
    }

    public void registerMixin(ClassPointcut classPointcut, Class mixinClass) {
        containerAspects.mixin(getClassPointcut(classPointcut), mixinClass, null);
    }

    public void registerMixin(ClassPointcut classPointcut, Class[] interfaces, Class mixinClass) {
        containerAspects.mixin(getClassPointcut(classPointcut), interfaces, mixinClass, null);
    }

    public void registerMixin(ClassPointcut classPointcut, Class[] interfaces, Object mixinComponentKey) {
        containerAspects.mixin(getClassPointcut(classPointcut), interfaces, createMixinFactory(mixinComponentKey));
    }

    public void registerMixin(ComponentPointcut componentPointcut, Class mixinClass) {
        componentAspects.add(new MixinComponentAspect(componentPointcut, mixinClass));
    }

    public void registerMixin(ComponentPointcut componentPointcut, Class[] interfaces, Class mixinClass) {
        componentAspects.add(new MixinComponentAspect(componentPointcut, interfaces, mixinClass));
    }

    public void registerMixin(ComponentPointcut componentPointcut, Class[] interfaces, Object mixinComponentKey) {
        componentAspects.add(new MixinComponentAspect(componentPointcut, interfaces,
                createMixinFactory(mixinComponentKey)));
    }

    public PointcutsFactory getPointcutsFactory() {
        return pointcutsFactory;
    }

    public Object applyAspects(Object componentKey, Object component, PicoContainer container) {
        containerLoader.setContainer(container);
        Object proxy = ProxyFactory.getInstance(containerAspects).wrap(component);
        proxy = applyComponentAspects(componentKey, proxy);
        return proxy;
    }

    private dynaop.ClassPointcut getClassPointcut(final ClassPointcut classPointcut) {
        if (classPointcut instanceof dynaop.ClassPointcut) {
            return (dynaop.ClassPointcut) classPointcut;
        }

        return new dynaop.ClassPointcut() {
            public boolean picks(Class clazz) {
                return classPointcut.picks(clazz);
            }
        };
    }

    private dynaop.MethodPointcut getMethodPointcut(final MethodPointcut methodPointcut) {
        if (methodPointcut instanceof dynaop.MethodPointcut) {
            return (dynaop.MethodPointcut) methodPointcut;
        }

        return new dynaop.MethodPointcut() {
            public boolean picks(Method method) {
                return methodPointcut.picks(method);
            }
        };
    }

    private Interceptor createInterceptor(MethodInterceptor methodInterceptor) {
        return new MethodInterceptorAdapter(methodInterceptor);
    }

    private InterceptorFactory createInterceptorFactory(Object interceptorComponent) {
        return new ContainerSuppliedInterceptorFactory(container, interceptorComponent);
    }

    private MixinFactory createMixinFactory(Object mixinComponent) {
        return new ContainerSuppliedMixinFactory(container, mixinComponent);
    }

    private Object applyComponentAspects(Object componentKey, Object proxy) {
        Iterator iterator = componentAspects.iterator();
        while (iterator.hasNext()) {
            ComponentAspect componentAspect = (ComponentAspect) iterator.next();
            proxy = componentAspect.applyAspect(componentKey, proxy);
        }
        return proxy;
    }

}