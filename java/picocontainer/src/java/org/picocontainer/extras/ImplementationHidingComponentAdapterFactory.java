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
            Object component = super.getComponentInstance(picoContainer);
            // TODO: search for all interfaces for component-implementation instead
            // There is code for this in DefaultComponentMulticasterFactory. Reuse that. (Static? Util class?)
            Object aClass = getComponentKey();
            Class[] interfaces = null;
            if (aClass.getClass() == Class.class && ((Class) aClass).isInterface()) {
                interfaces = new Class[]{(Class) aClass};
                return Proxy.newProxyInstance(getComponentImplementation().getClassLoader(),
                        interfaces, new ImplementationHidingProxy(component));
            } else {
                // Not interface/Impl separated form of component.
                //TODO is there some form of generic dynamic proxy that can be used here ?
                return component;
            }

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
