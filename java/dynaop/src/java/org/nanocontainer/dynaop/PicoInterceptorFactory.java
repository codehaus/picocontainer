/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/
package org.nanocontainer.dynaop;

import java.util.Properties;

import org.picocontainer.PicoContainer;

import dynaop.Interceptor;
import dynaop.InterceptorFactory;
import dynaop.Proxy;

/**
 * Manufactures interceptors from a <code>PicoContainer</code>. Useful when
 * an interceptor has dependencies on other components in the
 * <code>PicoContainer</code>.
 * 
 * @author Stephen Molitor
 */
public class PicoInterceptorFactory implements InterceptorFactory {

    private PicoContainer container;
    private Object interceptorComponentKey;

    /**
     * Creates a new <code>PicoInterceptorFactory</code> that will manufacture
     * interceptors by retrieving them from a <code>PicoContainer</code> using
     * a given component key.
     * 
     * @param container
     *            the <code>PicoContainer</code> to retrieve the interceptor
     *            from.
     * @param interceptorComponentKey
     *            the component key that will be used to retrieve the
     *            interceptor from the container.
     */
    public PicoInterceptorFactory(PicoContainer container,
                    Object interceptorComponentKey) {
        this.container = container;
        this.interceptorComponentKey = interceptorComponentKey;
    }

    /**
     * Manufactures an <code>Interceptor</code> by retrieving it from the
     * <code>PicoContainer</code>.
     * 
     * @param proxy
     *            the proxy that the interceptor will wrap.
     * @return the <code>Interceptor</code> object.
     * @throws NullPointerException
     *             if the interceptor can not be found in the container.
     */
    public Interceptor create(Proxy proxy) throws NullPointerException {
        Interceptor interceptor = (Interceptor) container
                        .getComponentInstance(interceptorComponentKey);
        if (interceptor == null) {
            throw new NullPointerException("Interceptor with component key "
                            + interceptorComponentKey
                            + " + not found in PicoContainer");
        }
        return interceptor;
    }

    /**
     * Gets properties. Useful for debuging.
     * 
     * @return a <code>Properties</code> object.
     */
    public Properties getProperties() {
        Properties properties = new Properties();
        properties.setProperty("advice", "method interceptor");
        properties.setProperty("scope", "per-instance");
        properties.setProperty("class", create(null).getClass().getName());
        return properties;
    }

}