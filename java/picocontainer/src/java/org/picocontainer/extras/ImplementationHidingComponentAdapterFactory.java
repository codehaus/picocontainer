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

import org.picocontainer.*;
import org.picocontainer.defaults.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class ImplementationHidingComponentAdapterFactory extends DecoratingComponentAdapterFactory {

    private final InterfaceFinder interfaceFinder = new InterfaceFinder();

    public ImplementationHidingComponentAdapterFactory() {
        this(new DefaultComponentAdapterFactory());
    }

    public ImplementationHidingComponentAdapterFactory(ComponentAdapterFactory delegate) {
        super(delegate);
    }

    public ComponentAdapter createComponentAdapter(Object componentKey,
                                                   Class componentImplementation,
                                                   Parameter[] parameters)
            throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        return new Adapter(super.createComponentAdapter(componentKey, componentImplementation, parameters));
    }

    public void hotSwap(Class aClass) {
    }

    public class Adapter extends DecoratingComponentAdapter {
        public Adapter(ComponentAdapter delegate) {
            super(delegate);
        }

        public Object getComponentInstance(MutablePicoContainer picoContainer)
                throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
            final Object component = super.getComponentInstance(picoContainer);
            Class[] interfaces = interfaceFinder.getInterfaces(component);
            if(interfaces.length == 0) {
                throw new PicoIntrospectionException() {
                    public String getMessage() {
                        return "Can't hide implementation for " + component.getClass().getName() + ". It doesn't implement any interfaces.";
                    }
                };
            }
            return Proxy.newProxyInstance(getComponentImplementation().getClassLoader(),
                    interfaces, new ImplementationHidingProxy(component));
        }

        // TODO: We need to handle methods from Object (equals, hashcode...).
        // See DefaultComponentMulticasterFactory
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
