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
import java.util.ArrayList;
import java.util.List;

/**
 * This component adapter makes it possible to hide the implementation
 * of a real subject (behind a proxy).
 * If the key of the component is of type {@link java.lang.Class} and that class represents an interface, the proxy
 * will only implement the interface represented by that Class. Otherwise (if the key is
 * something else), the proxy will implement all the interfaces of the underlying subject.
 * In any case, the proxy will also implement
 * {@link Swappable}, making it possible to swap out the underlying
 * subject at runtime.
 * <p>
 * <em>
 * Note that this class doesn't cache instances. If you want caching,
 * use a {@link CachingComponentAdapter} around this one.
 * </em>
 * 
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ImplementationHidingComponentAdapter extends DecoratingComponentAdapter {
    private static Method hotswap;
    static {
        try {
            hotswap = Swappable.class.getMethod("hotswap", new Class[]{Object.class});
        } catch (NoSuchMethodException e) {
            throw new InternalError();
        }
    }

    private final boolean strict;

    /**
     * Alternative constructor allowing to set interface-only strictness.
     * @param delegate the delegate adapter
     * @param strict true if the adapter should only accept classes that are hideable behind interfaces.
     * If false, a non-implementation hidden instance will be created instead of throwing an exception.
     */
    public ImplementationHidingComponentAdapter(ComponentAdapter delegate, boolean strict) {
        super(delegate);
        this.strict = strict;
    }


    /**
     * Creates a strict ImplementationHidingComponentAdapter that will throw an exception
     * when trying to instantiate a class that doesn't implement any interfaces.
     * @param delegate the delegate adapter
     */
    public ImplementationHidingComponentAdapter(ComponentAdapter delegate) {
        this(delegate, true);
    }

    public Object getComponentInstance()
            throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {

        List interfaces = new ArrayList();
        if(getDelegate().getComponentKey() instanceof Class && ((Class)getDelegate().getComponentKey()).isInterface()) {
            interfaces.add(getDelegate().getComponentKey());
        } else {
            interfaces.addAll(ClassHierarchyIntrospector.getAllInterfaces(getDelegate().getComponentImplementation()));
        }
        if (interfaces.size() == 0) {
            if(strict) {
                throw new PicoIntrospectionException("Can't hide implementation for " + getDelegate().getComponentImplementation().getName() + ". It doesn't implement any interfaces.");
            } else {
                return getDelegate().getComponentInstance();
            }
        }
        interfaces.add(Swappable.class);
        final DelegatingInvocationHandler delegatingInvocationHandler = new DelegatingInvocationHandler();
        return Proxy.newProxyInstance(
                getClass().getClassLoader(),
                (Class[]) interfaces.toArray(new Class[interfaces.size()]),
                delegatingInvocationHandler);
    }

    private Object getDelegatedComponentInstance() {
        return super.getComponentInstance();
    }

    private class DelegatingInvocationHandler implements InvocationHandler {
        private Object delegatedInstance;

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Class declaringClass = method.getDeclaringClass();
            if (declaringClass.equals(Object.class)) {
                if (method.equals(ClassHierarchyIntrospector.hashCode)) {
                    // Return the hashCode of ourself, as Proxy.newProxyInstance() may
                    // return cached proxies. We want a unique hashCode for each created proxy!
                    return new Integer(System.identityHashCode(this));
                }
                if (method.equals(ClassHierarchyIntrospector.equals)) {
                    return new Boolean(proxy == args[0]);
                }
                // If it's any other method defined by Object, call on ourself.
                return method.invoke(this, args);
            } else if (hotswap.equals(method)) {
                return hotswap(args[0]);
            } else {
                if (delegatedInstance == null) {
                    delegatedInstance = ImplementationHidingComponentAdapter.this.getDelegatedComponentInstance();
                }
                return method.invoke(delegatedInstance, args);
            }
        }

        private Object hotswap(Object newSubject) {
            Object result = delegatedInstance;
            delegatedInstance = newSubject;
            return result;
        }
    }
}
