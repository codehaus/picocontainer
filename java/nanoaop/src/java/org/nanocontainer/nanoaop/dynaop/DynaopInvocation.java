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

import dynaop.Proxy;

/**
 * @author Stephen Molitor
 */
public class DynaopInvocation implements dynaop.Invocation, org.nanocontainer.nanoaop.Invocation {

    private final dynaop.Invocation delegate;

    public DynaopInvocation(dynaop.Invocation delegate) {
        this.delegate = delegate;
    }
    
    public Object[] getArguments() {
        return delegate.getArguments();
    }

    public Proxy getProxy() {
        return delegate.getProxy();
    }

    public Method getMethod() {
        return delegate.getMethod();
    }

    public Object proceed() throws Throwable {
        return delegate.proceed();
    }

}