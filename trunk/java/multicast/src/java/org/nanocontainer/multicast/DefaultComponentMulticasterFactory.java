/*****************************************************************************
 * Copyright (ComponentC) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy                    *
 *****************************************************************************/

package org.nanocontainer.multicast;

import org.nanocontainer.multicast.ComponentMulticasterFactory;
import org.picocontainer.defaults.InterfaceFinder;
import org.nanocontainer.multicast.Invoker;
import org.nanocontainer.multicast.InvocationInterceptor;
import org.nanocontainer.multicast.ComponentMulticasterFactory;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Chris Stevenson
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class DefaultComponentMulticasterFactory implements ComponentMulticasterFactory, Serializable {

    private final org.picocontainer.defaults.InterfaceFinder interfaceFinder = new org.picocontainer.defaults.InterfaceFinder();

    public Object createComponentMulticaster(
            ClassLoader classLoader,
            List objectsToAggregateCallFor,
            boolean callInReverseOrder,
            InvocationInterceptor invocationInterceptor,
            Invoker invoker
            ) {
        Class[] interfaces = interfaceFinder.getInterfaces(objectsToAggregateCallFor);
        List copy = new ArrayList(objectsToAggregateCallFor);

        if (!callInReverseOrder) {
            // reverse the list
            Collections.reverse(copy);
        }
        Object[] objects = copy.toArray();

        Object result = Proxy.newProxyInstance(
                classLoader,
                interfaces,
                new AggregatingInvocationHandler(classLoader, objects, invocationInterceptor, invoker)
        );

        return result;
    }

    private class AggregatingInvocationHandler implements InvocationHandler {
        private Object[] children;
        private ClassLoader classLoader;
        private final Invoker invoker;
        private InvocationInterceptor invocationInterceptor;

        public AggregatingInvocationHandler(ClassLoader classLoader, Object[] children, InvocationInterceptor invocationInterceptor, Invoker invoker) {
            this.classLoader = classLoader;
            this.children = children;
            this.invocationInterceptor = invocationInterceptor;
            this.invoker = invoker;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Class declaringClass = method.getDeclaringClass();
            if (declaringClass.equals(Object.class)) {
                if (method.equals(org.picocontainer.defaults.InterfaceFinder.hashCode)) {
                    // Return the hashCode of ourself, as Proxy.newProxyInstance() may
                    // return cached proxies. We want a unique hashCode for each created proxy!
                    return new Integer(System.identityHashCode(AggregatingInvocationHandler.this));
                }
                if (method.equals(org.picocontainer.defaults.InterfaceFinder.equals)) {
                    return new Boolean(proxy == args[0]);
                }
                // If it's any other method defined by Object, call on ourself.
                return method.invoke(AggregatingInvocationHandler.this, args);
            } else {
                return invokeOnTargetsOfSameTypeAsDeclaringClass(declaringClass, children, method, args);
            }
        }

        private Object invokeOnTargetsOfSameTypeAsDeclaringClass(Class declaringClass, Object[] targets, Method method, Object[] args) throws IllegalAccessException, InvocationTargetException, InstantiationException {
            Class returnType = method.getReturnType();

            // Lazily created list holding all results.
            List results = new ArrayList();
            invoker.invoke(targets, declaringClass, method, args, results, invocationInterceptor);

            Object result;
            if (results.size() == 1) {
                // Got exactly one result. Just return that.
                result = results.get(0);
            } else if (returnType.isInterface()) {
                // We have two or more results
                // We can make a new proxy that aggregates all the results.
                //Class[] resultInterfaces = getInterfaces(results.toArray());
                result = createComponentMulticaster(
                        classLoader,
                        results,
                        true,
                        invocationInterceptor,
                        invoker
                );
            } else {
                // Got multiple results that can't be wrapped in a proxy. Try to instantiate a default object.
                result = returnType.equals(Void.TYPE) ? null : returnType.newInstance();
            }

            return result;
        }
    }
}
