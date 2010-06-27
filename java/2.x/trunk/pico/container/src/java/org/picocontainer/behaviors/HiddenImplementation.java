/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.behaviors;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;

/**
 * This component adapter makes it possible to hide the implementation
 * of a real subject (behind a proxy) provided the key is an interface.
 * <p/>
 * This class exists here, because a) it has no deps on external jars, b) dynamic proxy is quite easy.
 * The user is prompted to look at picocontainer-gems for alternate and bigger implementations.
 *
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @see org.picocontainer.gems.adapters.HotSwappingComponentAdapter for a more feature-rich version of this class.
 */
@SuppressWarnings("serial")
public class HiddenImplementation<T> extends AbstractBehavior<T> {

	/**
     * Creates an ImplementationHidingComponentAdapter with a delegate 
     * @param delegate the component adapter to which this adapter delegates
     */
    public HiddenImplementation(ComponentAdapter<T> delegate) {
        super(delegate);
    }

    public T getComponentInstance(final PicoContainer container, Type into) throws PicoCompositionException {

        ComponentAdapter<T> delegate = getDelegate();
        Object componentKey = delegate.getComponentKey();
        Class<?>[] classes;
        if (componentKey instanceof Class && ((Class<?>) delegate.getComponentKey()).isInterface()) {
            classes = new Class[]{(Class<?>) delegate.getComponentKey()};
        } else if (componentKey instanceof Class[]) {
            classes = (Class[]) componentKey;
        } else {
            return delegate.getComponentInstance(container, into);
        }

        verifyInterfacesOnly(classes);
        return createProxy(classes, container, delegate.getComponentImplementation().getClassLoader());
    }

    public String getDescriptor() {
        return "Hidden";
    }

    @SuppressWarnings("unchecked")
    protected T createProxy(Class[] interfaces, final PicoContainer container, final ClassLoader classLoader) {
        final PicoContainer container1 = container;
        return (T) Proxy.newProxyInstance(classLoader, interfaces, new InvocationHandler() {
            private final PicoContainer container = container1;
            private Object instance;
            public synchronized Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                if (instance == null) {
                    instance = getDelegate().getComponentInstance(container, NOTHING.class);
                }
                return invokeMethod(instance, method, args, container);
            }
        });
    }

    protected Object invokeMethod(Object componentInstance, Method method, Object[] args, PicoContainer container) throws Throwable {
        ComponentMonitor componentMonitor = currentMonitor();
        try {
            componentMonitor.invoking(container, this, method, componentInstance, args);
            long startTime = System.currentTimeMillis();
            Object rv = method.invoke(componentInstance, args);
            componentMonitor.invoked(container, this,
                                     method, componentInstance, System.currentTimeMillis() - startTime, args, rv);
            return rv;
        } catch (final InvocationTargetException ite) {
            componentMonitor.invocationFailed(method, componentInstance, ite);
            throw ite.getTargetException();
        }
    }

    private void verifyInterfacesOnly(Class<?>[] classes) {
        for (Class<?> clazz : classes) {
            if (!clazz.isInterface()) {
                throw new PicoCompositionException(
                    "Class keys must be interfaces. " + clazz + " is not an interface.");
            }
        }
    }

}
