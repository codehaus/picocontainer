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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;

import dynaop.Invocation;

/**
 * @author Stephen Molitor
 */
public class InvocationAdapter implements MethodInvocation {

    private final Invocation delegate;

    public InvocationAdapter(Invocation delegate) {
        this.delegate = delegate;
    }

    public Method getMethod() {
        return delegate.getMethod();
    }

    public Object[] getArguments() {
        return delegate.getArguments();
    }

    public AccessibleObject getStaticPart() {
        return delegate.getMethod();
    }

    public Object getThis() {
        return delegate.getProxy().getProxyContext().unwrap();
    }

    public Object proceed() throws Throwable {
        return delegate.proceed();
    }
    
}