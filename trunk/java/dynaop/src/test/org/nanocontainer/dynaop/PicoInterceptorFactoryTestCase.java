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

import junit.framework.TestCase;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

import dynaop.Interceptor;
import dynaop.InterceptorFactory;

/**
 * @author Stephen Molitor
 */
public class PicoInterceptorFactoryTestCase extends TestCase {

    public void testCreate() {
        MutablePicoContainer container = new DefaultPicoContainer();
        LoggingInterceptor loggingInterceptor = new LoggingInterceptor(
                        new StringBuffer());
        container.registerComponentInstance("interceptorComponentKey",
                        loggingInterceptor);

        InterceptorFactory interceptorFactory = new PicoInterceptorFactory(
                        container, "interceptorComponentKey");
        Interceptor interceptor = interceptorFactory.create(null);
        assertNotNull(interceptor);
        assertSame(loggingInterceptor, interceptor);
    }

    public void testInterceptorNotFoundInContainer() {
        MutablePicoContainer container = new DefaultPicoContainer();
        InterceptorFactory interceptorFactory = new PicoInterceptorFactory(
                        container, "interceptorComponentKey");
        try {
            Interceptor interceptor = interceptorFactory.create(null);
            fail("NullPointerException should have been raised");
        } catch (NullPointerException e) {
            // expected
        }
    }

}