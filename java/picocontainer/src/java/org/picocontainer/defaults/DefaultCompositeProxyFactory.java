/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy                    *
 *****************************************************************************/

package org.picocontainer.defaults;

import org.picocontainer.extras.CompositeProxyFactory;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Iterator;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.io.Serializable;

/**
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.1 $
 */
public class DefaultCompositeProxyFactory implements CompositeProxyFactory, Serializable {

    private static Method equals;
    private static Method hashCode;

    static {
        try {
            equals = Object.class.getMethod("equals", new Class[]{Object.class});
            hashCode = Object.class.getMethod("hashCode", null);
        } catch (NoSuchMethodException e) {
            ///CLOVER:OFF
            throw new InternalError();
            ///CLOVER:ON
        } catch (SecurityException e) {
            ///CLOVER:OFF
            throw new InternalError();
            ///CLOVER:ON
        }
    }

    public Object createComponentMulticaster(
            ClassLoader classLoader,
            List objectsToAggregateCallFor,
            boolean callInReverseOrder
            ) {
        Class[] interfaces = getInterfaces(objectsToAggregateCallFor);
        List copy = new ArrayList(objectsToAggregateCallFor);

        if (!callInReverseOrder) {
            // reverse the list
            Collections.reverse(copy);
        }
        Object[] objects = copy.toArray();

        Object result = Proxy.newProxyInstance(
                classLoader,
                interfaces,
                new AggregatingInvocationHandler(classLoader, objects)
        );

        return result;
    }

    private class AggregatingInvocationHandler implements InvocationHandler {
        private Object[] children;
        private ClassLoader classLoader;

        public AggregatingInvocationHandler(ClassLoader classLoader, Object[] children) {
            this.classLoader = classLoader;
            this.children = children;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Class declaringClass = method.getDeclaringClass();
            if (declaringClass.equals(Object.class)) {
                if (method.equals(hashCode)) {
                    // Return the hashCode of ourself, as Proxy.newProxyInstance() may
                    // return cached proxies. We want a unique hashCode for each created proxy!
                    return new Integer(System.identityHashCode(AggregatingInvocationHandler.this));
                }
                if (method.equals(equals)) {
                    return new Boolean(proxy == args[0]);
                }
                // If the method is defined by Object (like hashCode or equals), call
                // on ourself. This is a bit of a hack, but actually ok in most cases.
                return method.invoke(AggregatingInvocationHandler.this, args);
            } else {
                return invokeOnTargetsOfSameTypeAsDeclaringClass(declaringClass, children, method, args);
            }
        }

        private Object invokeOnTargetsOfSameTypeAsDeclaringClass(Class declaringClass, Object[] targets, Method method, Object[] args) throws IllegalAccessException, InvocationTargetException, InstantiationException {
            Class returnType = method.getReturnType();

            // Lazily created list holding all results.
            List results = null;
            for (int i = 0; i < targets.length; i++) {
                boolean isValidType = declaringClass.isAssignableFrom(targets[i].getClass());
                if (isValidType) {
                    // It's ok to call the method on this one
                    Object componentResult = method.invoke(targets[i], args);
                    if (results == null) {
                        results = new ArrayList();
                    }
                    results.add(componentResult);
                }
            }

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
                        true
                );
            } else {
                // Got multiple results that can't be wrapped in a proxy. Try to instantiate a default object.
                result = returnType.equals(Void.TYPE) ? null : returnType.newInstance();
            }

            return result;
        }
    }

    /**
     * Get all the interfaces implemented by an array of objects.
     * @return an array of interfaces.
     */
    private final Class[] getInterfaces(List objects) {
        Set interfaces = new HashSet();
        for (Iterator iterator = objects.iterator(); iterator.hasNext();) {
            Object o = iterator.next();
            Class componentClass = o.getClass();
            // Strangely enough Class.getInterfaces() does not include the interfaces
            // implemented by superclasses. So we must loop up the hierarchy.
            while (componentClass != null) {
                Class[] implemeted = componentClass.getInterfaces();
                List implementedList = Arrays.asList(implemeted);
                interfaces.addAll(implementedList);
                componentClass = componentClass.getSuperclass();
            }
        }

        Class[] result = (Class[]) interfaces.toArray(new Class[interfaces.size()]);
        return result;
    }

}
