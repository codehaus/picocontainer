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
import java.lang.reflect.Method;

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

    public Object getComponentMulticaster(PicoContainer picoContainer, boolean callInInstantiationOrder) throws PicoException {
        List componentsToMulticast = picoContainer.getComponentInstances();
        return factory.createComponentMulticaster(getClass().getClassLoader(), componentsToMulticast, callInInstantiationOrder, invocationInterceptor, new MulticastInvoker());
    }
}
