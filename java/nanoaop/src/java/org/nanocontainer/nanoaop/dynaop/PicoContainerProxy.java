/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.nanoaop.dynaop;

import org.nanocontainer.nanoaop.defaults.ContainerLoader;
import org.picocontainer.PicoContainer;

import dynaop.Aspects;
import dynaop.Interceptor;
import dynaop.Invocation;
import dynaop.Pointcuts;
import dynaop.ProxyFactory;

/**
 * @author Stephen Molitor
 */
public class PicoContainerProxy implements Interceptor {

    private final ContainerLoader containerLoader;

    public static PicoContainer create(ContainerLoader containerLoader) {
        Aspects aspects = new Aspects();
        aspects.interceptor(Pointcuts.ALL_CLASSES, Pointcuts.ALL_METHODS, new PicoContainerProxy(containerLoader));
        aspects.interfaces(Pointcuts.ALL_CLASSES, new Class[] { PicoContainer.class });
        return (PicoContainer) ProxyFactory.getInstance(aspects).extend(Object.class);
    }

    public PicoContainerProxy(ContainerLoader containerLoader) {
        this.containerLoader = containerLoader;
    }

    public Object intercept(Invocation invocation) throws Throwable {
        return invocation.getMethod().invoke(containerLoader.getContainer(), invocation.getArguments());
    }

}