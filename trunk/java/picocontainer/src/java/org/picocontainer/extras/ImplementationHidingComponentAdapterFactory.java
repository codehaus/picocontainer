/*****************************************************************************
 * Copyright (C) OldPicoContainer Organization. All rights reserved.            *
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
import org.picocontainer.Parameter;
import org.picocontainer.defaults.*;

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
            throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        return new Adapter(super.createComponentAdapter(componentKey, componentImplementation, parameters));
    }

    public class Adapter extends DecoratingComponentAdapter {
        public Adapter(ComponentAdapter delegate) {
            super(delegate);
        }

        public Object getComponentInstance(AbstractPicoContainer picoContainer)
                throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
            Object component = super.getComponentInstance(picoContainer);
            // TODO: search for all interfaces for component-implementation instead
            // There is code for this in DefaultComponentMulticasterFactory. Reuse that. (Static? Util class?)
            Class[] interfaces = new Class[]{(Class) getComponentKey()};
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
    };
}
