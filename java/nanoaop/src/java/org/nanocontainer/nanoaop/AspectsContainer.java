/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.nanoaop;

import org.aopalliance.intercept.MethodInterceptor;

/**
 * @author Stephen Molitor
 */
public interface AspectsContainer {

    void registerInterceptor(ClassPointcut classPointcut, MethodPointcut methodPointcut, MethodInterceptor interceptor);

    void registerInterceptor(ComponentPointcut componentPointcut, MethodPointcut methodPointcut,
            MethodInterceptor interceptor);

    void registerInterceptor(ClassPointcut classPointcut, MethodPointcut methodPointcut, Object interceptorComponentKey);

    void registerInterceptor(ComponentPointcut componentPointcut, MethodPointcut methodPointcut,
            Object interceptorComponentKey);

    void registerMixin(ClassPointcut classPointcut, Class[] interfaces, Class mixinClass);

    void registerMixin(ComponentPointcut componentPointcut, Class[] interfaces, Class mixinClass);

    void registerMixin(ClassPointcut classPointcut, Class[] interfaces, Object mixinComponentKey);

    void registerMixin(ComponentPointcut componentPointcut, Class[] interfaces, Object mixinComponentKey);

    void registerMixin(ClassPointcut classPointcut, Class mixinClass);

    void registerMixin(ComponentPointcut componentPointcut, Class mixinClass);

    PointcutsFactory getPointcutsFactory();

}