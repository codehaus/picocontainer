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

import dynaop.Aspects;
import dynaop.Interceptor;
import dynaop.Invocation;
import dynaop.ProxyFactory;

/**
 * @author Stephen Molitor
 */
public class ComponentWrapper implements Interceptor {

    private final Aspects aspects;

    public ComponentWrapper(Aspects aspects) {
        this.aspects = aspects;
    }

    public Object intercept(Invocation invocation) throws Throwable {
        Object component = invocation.proceed();
        return ProxyFactory.getInstance(aspects).wrap(component);
    }

}