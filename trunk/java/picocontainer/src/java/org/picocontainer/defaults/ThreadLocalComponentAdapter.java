/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/
package org.picocontainer.defaults;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;


/**
 * A {@link ComponentAdapter}that realizes a {@link ThreadLocal}component
 * instance. The adapter creates proxy instances, that will create the necessary
 * instances on-the-fly invoking the methods of the instance.
 *
 * <em>
 * IMPORTANT! This class will be moved out of the PicoContainer core
 * before release of the final version!
 * </em>
 * 
 * @author J&ouml;rg Schaible
 */
public class ThreadLocalComponentAdapter
        extends DecoratingComponentAdapter {

    private static Map m_proxyMap = Collections.synchronizedMap(new HashMap());

    /**
     * Construct a ThreadLocalComponentAdapter.
     * @param delegate The {@link ComponentAdapter}to delegate.
     */
    public ThreadLocalComponentAdapter(ComponentAdapter delegate) {
        super(new CachingComponentAdapter(delegate, new ThreadLocalReference()));
    }

    /**
     * @see org.picocontainer.ComponentAdapter#getComponentInstance()
     */
    public Object getComponentInstance()
            throws PicoInitializationException, PicoIntrospectionException,
            AssignabilityRegistrationException, NotConcreteRegistrationException {

        final Object componentKey = getDelegate().getComponentKey();
        final String key = String.valueOf(System.identityHashCode(componentKey))
                + "."
                + String.valueOf(System.identityHashCode(getContainer()));
        Object proxy = m_proxyMap.get(key);
        if (proxy == null) {
            final Class[] interfaces;
            if (componentKey instanceof Class && ((Class)componentKey).isInterface()) {
                interfaces = new Class[]{(Class)getDelegate().getComponentKey()};
            } else {
                interfaces = (Class[])ClassHierarchyIntrospector.getAllInterfaces(
                        getDelegate().getComponentImplementation()).toArray(new Class[0]);
            }
            if (interfaces.length == 0) { throw new PicoIntrospectionException(
                    "Can't proxy implementation for "
                            + getDelegate().getComponentImplementation().getName()
                            + ". It doesn't implement any interfaces."); }
            final ThreadLocalInvocationHandler threadLocalInvocationHandler = new ThreadLocalInvocationHandler();
            proxy = Proxy.newProxyInstance(
                    getClass().getClassLoader(), interfaces, threadLocalInvocationHandler);
            m_proxyMap.put(key, proxy);
        }
        return proxy;
    }

    private class ThreadLocalInvocationHandler
            implements InvocationHandler {

        /**
         * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
         *          java.lang.reflect.Method, java.lang.Object[])
         */
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            final Object delegatedInstance = ThreadLocalComponentAdapter.this.getDelegate()
                    .getComponentInstance();
            return method.invoke(delegatedInstance, args);
        }

    }
}