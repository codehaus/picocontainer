/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           * 
 *****************************************************************************/
package org.picocontainer.defaults;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.picocontainer.Disposable;
import org.picocontainer.PicoContainer;
import org.picocontainer.Startable;


/**
 * A factory for immutable PicoContainer proxies.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.2
 */
public class ImmutablePicoContainerProxyFactory implements InvocationHandler, Serializable {

    private static final Class[] interfaces = new Class[]{PicoContainer.class};
    protected static Method startMethod = null;
    protected static Method stopMethod = null;
    protected static Method disposeMethod = null;
    protected static Method equalsMethod = null;

    static {
        try {
            startMethod = Startable.class.getMethod("start", new Class[0]);
            stopMethod = Startable.class.getMethod("stop", new Class[0]);
            disposeMethod = Disposable.class.getMethod("dispose", new Class[0]);
            equalsMethod = Object.class.getMethod("equals", new Class[]{Object.class});
        } catch (final NoSuchMethodException e) {
            throw new InternalError(e.getMessage());
        }
    }

    private final PicoContainer pico;

    /**
     * Construct a ImmutablePicoContainerProxyFactory.
     * 
     * @param pico the container to hide
     * @throws NullPointerException if <tt>pico</tt> is <code>null</code>
     * @since 1.2
     */
    protected ImmutablePicoContainerProxyFactory(final PicoContainer pico) {
        if (pico == null) {
            throw new NullPointerException();
        }
        this.pico = pico;
    }

    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        if (method.equals(startMethod) || method.equals(stopMethod) || method.equals(disposeMethod)) {
            throw new UnsupportedOperationException("This container is immutable, "
                    + method.getName()
                    + " is not allowed");
        } else if (method.equals(equalsMethod)) { // necessary for JDK 1.3
            return new Boolean(args[0] != null && args[0].equals(pico));
        }
        try {
            return method.invoke(pico, args);
        } catch (final InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    /**
     * Create a new immutable PicoContainer proxy. The proxy will completly hide the implementation of the given
     * {@link PicoContainer} and will also prevent the invocation of any methods of the lifecycle methods from
     * {@link Startable} or {@link Disposable}.
     * 
     * @param pico
     * @return the new proxy
     * @throws NullPointerException if <tt>pico</tt> is <code>null</code>
     * @since 1.2
     */
    public static PicoContainer newProxyInstance(final PicoContainer pico) {
        return (PicoContainer)Proxy.newProxyInstance(
                PicoContainer.class.getClassLoader(), interfaces,
                new ImmutablePicoContainerProxyFactory(pico));
    }
}
