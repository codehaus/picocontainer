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

import org.aopalliance.intercept.MethodInterceptor;

import dynaop.Interceptor;
import dynaop.Invocation;

/**
 * @author Stephen Molitor
 */
public class MethodInterceptorAdapter implements Interceptor {
    
    private final MethodInterceptor delegate;
    
    public MethodInterceptorAdapter(MethodInterceptor delegate) {
        this.delegate = delegate;
    }

    public Object intercept(Invocation invocation) throws Throwable {
        return delegate.invoke(new InvocationAdapter(invocation));
    }

}
