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

import java.util.Properties;

import org.nanocontainer.nanoaop.Interceptor;
import org.picocontainer.PicoContainer;

import dynaop.InterceptorFactory;
import dynaop.Proxy;

/**
 * @author Stephen Molitor
 */
public class ContainerSuppliedInterceptorFactoy implements InterceptorFactory {

    private final PicoContainer container;
    private final Object componentKey;

    public ContainerSuppliedInterceptorFactoy(PicoContainer container, Object componentKey) {
        this.container = container;
        this.componentKey = componentKey;
    }

    public dynaop.Interceptor create(Proxy proxy) {
        Interceptor nanoInterceptor = (Interceptor) container.getComponentInstance(componentKey);
        return new DynaopInterceptor(nanoInterceptor);
    }

    public Properties getProperties() {
        return new Properties();
    }

}