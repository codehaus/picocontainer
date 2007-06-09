/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/
package org.picocontainer.gems.adapters;

import com.thoughtworks.proxy.Invoker;
import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;
import com.thoughtworks.proxy.kit.ReflectionUtils;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.adapters.CachingBehaviorAdapter;
import org.picocontainer.adapters.BehaviorAdapter;
import org.picocontainer.defaults.NotConcreteRegistrationException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Set;


/**
 * A {@link ComponentAdapter} that realizes a {@link ThreadLocal} addComponent instance.
 * <p>
 * The addAdapter creates proxy instances, that will create the necessary instances on-the-fly invoking the methods of the
 * instance. Use this addAdapter, if you are instantiating your components in a single thread, but should be different when
 * accessed from different threads. See {@link ThreadLocalComponentAdapterFactory} for details.
 * </p>
 * <p>
 * Note: Because this implementation uses a {@link Proxy}, you can only access the methods exposed by the implemented
 * interfaces of your addComponent.
 * </p>
 * 
 * @author J&ouml;rg Schaible
 */
public final class ThreadLocalComponentAdapter extends BehaviorAdapter {

    private transient Class[] interfaces;
    private final ProxyFactory proxyFactory;

    /**
     * Construct a ThreadLocalComponentAdapter.
     * 
     * @param delegate The {@link ComponentAdapter} to delegate.
     * @param proxyFactory The {@link ProxyFactory} to use.
     * @throws PicoIntrospectionException Thrown if the addComponent does not implement any interface.
     */
    public ThreadLocalComponentAdapter(final ComponentAdapter delegate, final ProxyFactory proxyFactory)
            throws PicoIntrospectionException {
        super(new CachingBehaviorAdapter(delegate, new ThreadLocalReference()));
        this.proxyFactory = proxyFactory;
        interfaces = getInterfaces();
    }

    /**
     * Construct a ThreadLocalComponentAdapter using {@link Proxy} instances.
     * 
     * @param delegate The {@link ComponentAdapter} to delegate.
     * @throws PicoIntrospectionException Thrown if the addComponent does not implement any interface.
     */
    public ThreadLocalComponentAdapter(final ComponentAdapter delegate) throws PicoIntrospectionException {
        this(new CachingBehaviorAdapter(delegate, new ThreadLocalReference()), new StandardProxyFactory());
    }

    public Object getComponentInstance(final PicoContainer pico)
            throws PicoInitializationException, PicoIntrospectionException,
            NotConcreteRegistrationException {

        if (interfaces == null) {
            interfaces = getInterfaces();
        }

        final ComponentAdapter delegate = getDelegate();
        final Invoker invoker = new ThreadLocalInvoker(pico, delegate);
        return proxyFactory.createProxy(interfaces, invoker);
    }

    final private Class[] getInterfaces() {
        final Object componentKey = getComponentKey();
        final Class[] interfaces;
        if (componentKey instanceof Class && ((Class)componentKey).isInterface()) {
            interfaces = new Class[]{(Class)componentKey};
        } else {
            final Set allInterfaces = ReflectionUtils.getAllInterfaces(getComponentImplementation());
            interfaces = (Class[])allInterfaces.toArray(new Class[allInterfaces.size()]);
        }
        if (interfaces.length == 0) {
            throw new PicoIntrospectionException("Can't proxy implementation for "
                    + getComponentImplementation().getName()
                    + ". It does not implement any interfaces.");
        }
        return interfaces;
    }

    final static private class ThreadLocalInvoker implements Invoker {

        private final PicoContainer pico;
        private final ComponentAdapter delegate;

        private ThreadLocalInvoker(final PicoContainer pico, final ComponentAdapter delegate) {
            this.pico = pico;
            this.delegate = delegate;
        }

        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            final Object delegatedInstance = delegate.getComponentInstance(pico);
            if (method.equals(ReflectionUtils.equals)) { // necessary for JDK 1.3
                return args[0] != null && args[0].equals(delegatedInstance);
            } else {
                try {
                    return method.invoke(delegatedInstance, args);
                } catch (final InvocationTargetException e) {
                    throw e.getTargetException();
                }
            }
        }
    }
}