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
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;

import dynaop.Aspects;
import dynaop.Pointcuts;
import dynaop.util.Closure;

/**
 * @author Stephen Molitor
 */
public class DynaopComponentAdapterFactoryTestCase extends TestCase {

    public void testHardCodedConfiguration() {
        StringBuffer log = new StringBuffer();

        Aspects aspects = new Aspects();
        aspects.interceptor(Pointcuts.instancesOf(Foo.class),
                        Pointcuts.ALL_METHODS, new LoggingInterceptor(log));
        aspects.mixin(Pointcuts.instancesOf(Foo.class),
                        IdentifiableMixin.class, new Closure() {
                            public void execute(Object o) {
                            }
                        });

        DynaopComponentAdapterFactory factory = new DynaopComponentAdapterFactory(
                        new DefaultComponentAdapterFactory(), aspects);
        MutablePicoContainer container = new DefaultPicoContainer(factory);

        container.registerComponentImplementation(Foo.class, FooImpl.class);
        Foo foo = (Foo) container.getComponentInstance(Foo.class);

        foo.aMethod();
        assertEquals("startend", log.toString());
        
        Identifiable identifiable = (Identifiable) foo;
        assertEquals(new Integer(0), identifiable.getId());
        identifiable.setId(new Integer(1));
        assertEquals(new Integer(1), identifiable.getId());
    }

}