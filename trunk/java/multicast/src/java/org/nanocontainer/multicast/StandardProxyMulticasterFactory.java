/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy                    *
 *****************************************************************************/

package org.nanocontainer.multicast;

import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Chris Stevenson
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class StandardProxyMulticasterFactory extends AbstractMulticasterFactory {

    protected Object createProxy(ClassLoader classLoader, List objectsToAggregateCallFor, Object[] targets, InvocationInterceptor invocationInterceptor, Invoker invoker) {
        return Proxy.newProxyInstance(
                classLoader,
                interfaceFinder.getInterfaces(objectsToAggregateCallFor),
                new AggregatingInvocationHandler(classLoader, targets, invocationInterceptor, invoker)
        );
    }

    protected class AggregatingInvocationHandler extends AbstractAggregatingInvocationHandler implements InvocationHandler {
        public AggregatingInvocationHandler(ClassLoader classLoader, Object[] tartets, InvocationInterceptor invocationInterceptor, Invoker invoker) {
            super(StandardProxyMulticasterFactory.this, classLoader, tartets, invocationInterceptor, invoker);
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return invokeMethod(proxy, method, args);
        }
    }
}
