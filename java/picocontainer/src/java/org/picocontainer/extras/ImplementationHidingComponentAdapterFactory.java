/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.extras;

import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.internals.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ImplementationHidingComponentAdapterFactory extends DecoratingComponentAdapterFactory {
    public ImplementationHidingComponentAdapterFactory(ComponentAdapterFactory delegate) {
        super(delegate);
    }

    public ComponentAdapter createComponentAdapter(Object componentKey,
                                                   Class componentImplementation,
                                                   Parameter[] parameters)
            throws PicoIntrospectionException {
        return new Adapter(super.createComponentAdapter(componentKey, componentImplementation, parameters));
    }

    public static class Adapter extends DecoratingComponentAdapter {
        public Adapter(ComponentAdapter decoratedComponentAdapter) {
            super(decoratedComponentAdapter);
        }

        public Object instantiateComponent(ComponentRegistry componentRegistry)
                throws PicoInitializationException {
            Object component = super.instantiateComponent(componentRegistry);
            // TODO: search for all interfaces for component-implementation instead
            Class[] interfaces = new Class[] { (Class) getComponentKey() };
            return Proxy.newProxyInstance(getComponentImplementation().getClassLoader(),
                    interfaces, new ImplementationHidingProxy(component));
        }

        private class ImplementationHidingProxy implements InvocationHandler {
            private Object componentInstance;

            public ImplementationHidingProxy(Object componentInstance) {
                this.componentInstance = componentInstance;
            }

            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return method.invoke(componentInstance, args);
            }
        }
    }
}
