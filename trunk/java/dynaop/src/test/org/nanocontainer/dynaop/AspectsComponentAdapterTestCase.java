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

import dynaop.Aspects;
import dynaop.Pointcuts;
import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapter;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * @author Stephen Molitor
 */
public class AspectsComponentAdapterTestCase extends TestCase {

    public void testGetComponentInstance() {
        MutablePicoContainer container = new DefaultPicoContainer();

        StringBuffer log = new StringBuffer();
        Aspects aspects = new Aspects();
        aspects.interceptor(Pointcuts.instancesOf(MyComponent.class),
                        Pointcuts.ALL_METHODS, new LoggingInterceptor(log));

        container.registerComponent(new AspectsComponentAdapter(aspects,
                        new ConstructorInjectionComponentAdapter(
                                        MyComponent.class,
                                        MyComponentImpl.class)));

        MyComponent myComponent = (MyComponent) container
                        .getComponentInstance(MyComponent.class);

        myComponent.aMethod();
        assertEquals("startend", log.toString());
    }

}