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

import org.nanocontainer.nanoaop.ComponentPointcut;

import dynaop.Aspects;
import dynaop.Interceptor;
import dynaop.InterceptorFactory;
import dynaop.MethodPointcut;
import dynaop.Pointcuts;
import dynaop.ProxyFactory;

/**
 * @author Stephen Molitor
 */
class InterceptorComponentAspect extends ComponentAspect {

    private MethodPointcut methodPointcut;
    private Interceptor interceptor;
    private InterceptorFactory interceptorFactory;

    InterceptorComponentAspect(ComponentPointcut componentPointcut, MethodPointcut methodPointcut,
            Interceptor interceptor) {
        super(componentPointcut);
        this.methodPointcut = methodPointcut;
        this.interceptor = interceptor;
    }

    InterceptorComponentAspect(ComponentPointcut componentPointcut, MethodPointcut methodPointcut,
            InterceptorFactory interceptorFactory) {
        super(componentPointcut);
        this.methodPointcut = methodPointcut;
        this.interceptorFactory = interceptorFactory;
    }

    Object wrap(Object component) {
        Aspects aspects = new Aspects();
        if (interceptor != null) {
            aspects.interceptor(Pointcuts.ALL_CLASSES, methodPointcut, interceptor);
        } else {
            aspects.interceptor(Pointcuts.ALL_CLASSES, methodPointcut, interceptorFactory);
        }
        return ProxyFactory.getInstance(aspects).wrap(component);
    }

}