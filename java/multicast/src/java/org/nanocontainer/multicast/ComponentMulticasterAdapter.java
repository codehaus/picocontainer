/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.multicast;

import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;

import java.io.Serializable;
import java.util.List;

/**
 * This class makes it possible to multicast invocations to several components
 * in a container. It thereby supports pluggable lifecycles.
 *
 * @author Aslak Helles&oslash;y
 * @author Chris Stevenson
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Revision$
 */
public class ComponentMulticasterAdapter implements Serializable {
    private final MulticasterFactory factory;
    private final InvocationInterceptor invocationInterceptor;

    public ComponentMulticasterAdapter() {
        this(new StandardProxyMulticasterFactory(), new NullInvocationInterceptor());
    }

    public ComponentMulticasterAdapter(MulticasterFactory factory, InvocationInterceptor invocationInterceptor) {
        this.factory = factory;
        this.invocationInterceptor = invocationInterceptor;
    }

    /**
     * Creates a proxy that will multicast method invocations to all objects in the container that correspond to the
     * type.
     * @param picoContainer the container containing the components.
     * @param type the component type. Null is allowed here, and means "all types".
     * @param callInInstantiationOrder true if invocations should be done in the components' instantiation order (dependency order).
     * @return a proxy object that can be cast to the specified type.
     * @throws PicoException if the multicaster can't be created.
     */
    public Object createComponentMulticaster(PicoContainer picoContainer, Class type, boolean callInInstantiationOrder) throws PicoException {
        List componentsToMulticast = picoContainer.getComponentInstances();
        return factory.createComponentMulticaster(getClass().getClassLoader(), componentsToMulticast, callInInstantiationOrder, invocationInterceptor, new MulticastInvoker());
    }
}
