/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Original code by Aslak Hellesoy and Paul Hammant                          *
 *****************************************************************************/

package nanocontainer;

import picocontainer.PicoRegistrationException;
import picocontainer.PicoStartException;
import picocontainer.PicoContainer;
import picocontainer.PicoContainerImpl;

import java.util.Collection;
import java.util.ArrayList;
import java.lang.reflect.UndeclaredThrowableException;

import junit.framework.TestCase;
import nanocontainer.reflect.SequentialInvocationHandler;

public class AggregatingNanoContainerTestCase extends TestCase {


    public static interface Foo {
        void setBar(String s);
    }

    public static class FooImpl implements Foo {
        private String bar;

        public void setBar(String bar) {
            this.bar = bar;
        }

        public String getBar() {
            return bar;
        }
    }

    public void testGetProxy() throws PicoRegistrationException, PicoStartException {

        PicoContainer pico = new PicoContainerImpl.Default();

        Collection list = new ArrayList();
        pico.registerComponent(list);

        pico.registerComponent(Foo.class, FooImpl.class);
        pico.start();

        AggregatingNanoContainer aggContainer = new AggregatingNanoContainer(pico, new SequentialInvocationHandler(pico));
        aggContainer.start();
        
        Object proxy = aggContainer.getProxy();

        Collection proxyCollection = (Collection) proxy;
        Foo proxyFoo = (Foo) proxy;

        proxyCollection.add("Foo");
        try {
            proxyFoo.setBar("Zap");
        } catch (UndeclaredThrowableException e) {
            throw e;
        }

        // Assert that the proxy subjects have changed.
        assertTrue("The collection should have a Foo", list.contains("Foo"));

        FooImpl foo = (FooImpl) pico.getComponent(Foo.class);
        assertEquals("Zap", foo.getBar());
    }
}
