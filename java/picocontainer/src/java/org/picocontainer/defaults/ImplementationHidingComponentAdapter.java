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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * This component adapter makes it possible to hide the implementation
 * of a real subject (behind a proxy) provided the key is an interface.
 *
 * This class exists here, because a) it has no deps on external jars, b) dynamic proxy is quite easy.
 * The user is prompted to look at nanocontainer-proxytoys for alternate and bigger implementations.
 *
 * @see org.nanocontainer.proxytoys.HotSwappingComponentAdapter for a more feature-rich version of this class.
 * @see org.nanocontainer.proxytoys.HotSwappingComponentAdapterFactory
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @version $Revision$
 */
public class ImplementationHidingComponentAdapter extends DecoratingComponentAdapter {

    public ImplementationHidingComponentAdapter(ComponentAdapter delegate) {
        super(delegate);
    }

    public Object getComponentInstance()
            throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {

        Class[] interfaces;
        Object componentKey = getDelegate().getComponentKey();
        if (componentKey instanceof Class && ((Class) getDelegate().getComponentKey()).isInterface()) {
            interfaces = new Class[]{(Class) getDelegate().getComponentKey()};
        } else if (componentKey instanceof Class[]) {
            interfaces = (Class[]) componentKey;
        } else {
            throw new PicoIntrospectionException("Can't hide non interface keyed implementations.");
        }
        for (int i = 0; i < interfaces.length; i++) {
            Class anInterface = interfaces[i];
            if (!anInterface.isInterface()) {
                 throw new PicoIntrospectionException("Can't hide non interface keyed implementation :" + anInterface.getName());
             }
        }
        if (interfaces.length == 0) {
            throw new PicoIntrospectionException("Can't hide implementation for " + getDelegate().getComponentImplementation().getName() + ". It doesn't implement any interfaces.");
        }
        return Proxy.newProxyInstance(getClass().getClassLoader(),
                interfaces, new InvocationHandler() {
                    public Object invoke(final Object proxy, final Method method,
                                         final Object[] args)
                            throws Throwable {
                        try {
                            return method.invoke(getDelegate().getComponentInstance(), args);
                        } catch (final InvocationTargetException ite) {
                            throw ite.getTargetException();
                        }
                    }
                });
    }
}
