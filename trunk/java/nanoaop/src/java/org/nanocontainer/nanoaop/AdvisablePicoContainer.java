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

import org.picocontainer.MutablePicoContainer;

/**
 * @author Stephen Molitor
 */
public interface AdvisablePicoContainer extends MutablePicoContainer {

    PointcutFactory getPointcutFactory();

    void addInterceptor(ClassPointcut classPointcut, MethodPointcut methodPointcut, Interceptor interceptor);

    void addInterceptor(ComponentPointcut componentPointcut, MethodPointcut methodPointcut, Interceptor interceptor);

    void addInterceptor(ClassPointcut classPointcut, MethodPointcut methodPointcut,
            ComponentPointcut interceptorComponentPointcut);

    void addInterceptor(ComponentPointcut classPointcut, MethodPointcut methodPointcut,
            ComponentPointcut componentPointcut);

}