/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.defaults;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * This component adapter makes it possible to hide the implementation
 * of a real subject (behind a proxy).
 * If the key of the component is of type {@link java.lang.Class} and that class represents an interface, the proxy
 * will only implement the interface represented by that Class. Otherwise (if the key is
 * something else), the proxy will implement all the interfaces of the underlying subject.
 * In any case, the proxy will also implement
 * {@link Swappable}, making it possible to swap out the underlying
 * subject at runtime.
 * 
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @version $Revision$
 */
public class ImplementationHidingComponentAdapter extends DecoratingComponentAdapter {
    private final InterfaceFinder interfaceFinder = new InterfaceFinder();

    public ImplementationHidingComponentAdapter(ComponentAdapter delegate) {
        super(delegate);
    }

    public Object getComponentInstance()
            throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {

        Class[] interfaces;
        if(getDelegate().getComponentKey() instanceof Class && ((Class)getDelegate().getComponentKey()).isInterface()) {
            interfaces = new Class[] {(Class) getDelegate().getComponentKey()};
        } else {
            interfaces = interfaceFinder.getInterfaces(getDelegate().getComponentImplementation());
        }
        Class[] swappableAugmentedInterfaces = new Class[interfaces.length + 1];
        swappableAugmentedInterfaces[interfaces.length] = Swappable.class;
        System.arraycopy(interfaces, 0, swappableAugmentedInterfaces, 0, interfaces.length);
        if (interfaces.length == 0) {
            throw new PicoIntrospectionException("Can't hide implementation for " + getDelegate().getComponentImplementation().getName() + ". It doesn't implement any interfaces.");
        }
        final DelegatingInvocationHandler delegatingInvocationHandler = new DelegatingInvocationHandler(this);
        return Proxy.newProxyInstance(getClass().getClassLoader(),
                swappableAugmentedInterfaces, delegatingInvocationHandler);
    }

    private Object getDelegatedComponentInstance() {
        return super.getComponentInstance();
    }

    private class DelegatingInvocationHandler implements InvocationHandler, Swappable {
        private final ImplementationHidingComponentAdapter adapter;
        private Object delegatedInstance;

        public DelegatingInvocationHandler(ImplementationHidingComponentAdapter adapter) {
            this.adapter = adapter;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Class declaringClass = method.getDeclaringClass();
            if (declaringClass.equals(Object.class)) {
                if (method.equals(InterfaceFinder.hashCode)) {
                    // Return the hashCode of ourself, as Proxy.newProxyInstance() may
                    // return cached proxies. We want a unique hashCode for each created proxy!
                    return new Integer(System.identityHashCode(DelegatingInvocationHandler.this));
                }
                if (method.equals(InterfaceFinder.equals)) {
                    return new Boolean(proxy == args[0]);
                }
                // If it's any other method defined by Object, call on ourself.
                return method.invoke(DelegatingInvocationHandler.this, args);
            } else if (declaringClass.equals(Swappable.class)) {
                return method.invoke(this, args);
            } else {
                if (delegatedInstance == null) {
                    delegatedInstance = adapter.getDelegatedComponentInstance();
                }
                return method.invoke(delegatedInstance, args);
            }
        }

        public Object __hotSwap(Object newSubject) {
            Object result = delegatedInstance;
            delegatedInstance = newSubject;
            return result;
        }
    }
}
