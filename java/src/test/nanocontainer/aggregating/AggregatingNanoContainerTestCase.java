/*****************************************************************************
 * Copyright (Cc) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Aslak Hellesoy and Paul Hammant                          *
 *****************************************************************************/

package nanocontainer.aggregating;

import picocontainer.PicoRegistrationException;
import picocontainer.PicoInstantiationException;
import picocontainer.ClassRegistrationPicoContainer;
import picocontainer.PicoIntrospectionException;
import picocontainer.hierarchical.HierarchicalPicoContainer;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Map;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;
import nanocontainer.aggregating.reflect.SequentialInvocationHandler;
import nanocontainer.aggregating.AggregatingNanoContainer;

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

    public void testGetProxy() throws PicoRegistrationException, PicoInstantiationException, PicoIntrospectionException {

        ClassRegistrationPicoContainer pico = new HierarchicalPicoContainer.Default();

        Collection list = new ArrayList();
        pico.registerComponent(list);

        pico.registerComponent(Foo.class, FooImpl.class);
        pico.instantiateComponents();

        AggregatingNanoContainer aggContainer = new AggregatingNanoContainer(pico, new SequentialInvocationHandler(pico));
        Object proxy = aggContainer.getProxy();

        Collection proxyCollection = (Collection) proxy;
        Foo proxyFoo = (Foo) proxy;

        proxyCollection.add("Foo");
        proxyFoo.setBar("Zap");

        // Assert that the proxy subjects have changed.
        assertTrue("The collection should have a Foo", list.contains("Foo"));

        FooImpl foo = (FooImpl) pico.getComponent(Foo.class);
        assertEquals("Zap", foo.getBar());
    }

    public void testNoInvocationHandler() throws PicoRegistrationException, PicoInstantiationException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, PicoIntrospectionException {

        ClassRegistrationPicoContainer pico = new HierarchicalPicoContainer.Default();

        Collection list = new ArrayList();
        pico.registerComponent(list);

        pico.registerComponent(Foo.class, FooImpl.class);
        pico.instantiateComponents();

        AggregatingNanoContainer aggContainer = new AggregatingNanoContainer(pico, new SequentialInvocationHandler(pico));
        Object proxy = aggContainer.getProxy();

        Collection proxyCollection = (Collection) proxy;
        Foo foo = (Foo) proxy;

        Method put = Map.class.getMethod("put", new Class[]{Object.class, Object.class});

        //TODO-Aslak - Try to force a NoInvocationTargetException ? (PH)

        proxyCollection.add("Foo");
        try {
            put.invoke(foo, new Object[]{"zap", "zap"});
        } catch (IllegalArgumentException e) {
            // expected
        }

    }

}
