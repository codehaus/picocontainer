/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/
package org.nanocontainer.concurrent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.CachingComponentAdapter;
import org.picocontainer.defaults.DecoratingComponentAdapter;
import org.picocontainer.defaults.NotConcreteRegistrationException;

import com.thoughtworks.proxy.toys.multicast.ClassHierarchyIntrospector;


/**
 * A {@link ComponentAdapter}that realizes a {@link ThreadLocal}component instance.
 * <p>
 * The adapter creates proxy instances, that will create the necessary instances on-the-fly invoking the methods of the
 * instance. Use this adapter, if you are instantiating your components in a single thread, but should be different when
 * accessed from different threads. See {@link org.nanocontainer.concurrent.ThreadLocalComponentAdapterFactory}for details.
 * </p>
 * <p>
 * Note: Because this implementation uses a {@link Proxy}, you can only access the methods exposed by the implemented
 * interfaces of your component.
 * </p>
 * @author J&ouml;rg Schaible
 */
public class ThreadLocalComponentAdapter extends DecoratingComponentAdapter {

    private transient Class[] m_interfaces;

    /**
     * Construct a ThreadLocalComponentAdapter.
     * @param delegate The {@link ComponentAdapter}to delegate.
     * @throws PicoIntrospectionException Thrown if the component does not implement any interface.
     */
    public ThreadLocalComponentAdapter(final ComponentAdapter delegate) throws PicoIntrospectionException {
        super(new CachingComponentAdapter(delegate, new ThreadLocalReference()));
        m_interfaces = getInterfaces();
    }

    /**
     * @see org.picocontainer.ComponentAdapter#getComponentInstance(PicoContainer)
     */
    public Object getComponentInstance(final PicoContainer pico)
            throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException,
            NotConcreteRegistrationException {

        final ComponentAdapter delegate = getDelegate();
        if (m_interfaces == null) {
            m_interfaces = getInterfaces();
        }
        final InvocationHandler threadLocalInvocationHandler = new ThreadLocalInvocationHandler(pico, delegate);
        return Proxy.newProxyInstance(getComponentImplementation().getClassLoader(), m_interfaces, threadLocalInvocationHandler);
    }

    final private Class[] getInterfaces() {
        final Object componentKey = getComponentKey();
        final Class[] interfaces;
        if (componentKey instanceof Class && ((Class)componentKey).isInterface()) {
            interfaces = new Class[]{(Class)componentKey};
        } else {
            interfaces = ClassHierarchyIntrospector.getAllInterfaces(getComponentImplementation());
        }
        if (interfaces.length == 0) {
            throw new PicoIntrospectionException("Can't proxy implementation for "
                    + getComponentImplementation().getName()
                    + ". It does not implement any interfaces.");
        }
        return interfaces;
    }

    final static private class ThreadLocalInvocationHandler implements InvocationHandler {

        private final PicoContainer pico;
        private final ComponentAdapter delegate;

        public ThreadLocalInvocationHandler(final PicoContainer pico, final ComponentAdapter delegate) {
            this.pico = pico;
            this.delegate = delegate;
        }

        /**
         * {@inheritDoc}
         * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
         */
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            final Object delegatedInstance = delegate.getComponentInstance(pico);
            try {
                return method.invoke(delegatedInstance, args);
            } catch (final InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
    }
}