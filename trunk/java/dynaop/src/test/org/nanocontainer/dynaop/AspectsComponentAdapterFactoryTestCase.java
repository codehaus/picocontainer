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
public class AspectsComponentAdapterFactoryTestCase extends TestCase {

    public void testHardCodedConfiguration() {
        StringBuffer log = new StringBuffer();

        Aspects aspects = new Aspects();
        aspects.interceptor(Pointcuts.instancesOf(MyComponent.class),
                        Pointcuts.ALL_METHODS, new LoggingInterceptor(log));
        aspects.mixin(Pointcuts.instancesOf(MyComponent.class),
                        IdentifiableMixin.class, new Closure() {
                            public void execute(Object o) {
                            }
                        });

        AspectsComponentAdapterFactory factory = new AspectsComponentAdapterFactory(
                        new DefaultComponentAdapterFactory(), aspects);
        MutablePicoContainer container = new DefaultPicoContainer(factory);

        container.registerComponentImplementation(MyComponent.class,
                        MyComponentImpl.class);
        MyComponent myComponent = (MyComponent) container
                        .getComponentInstance(MyComponent.class);

        myComponent.aMethod();
        assertEquals("startend", log.toString());

        Identifiable identifiable = (Identifiable) myComponent;
        assertEquals(new Integer(0), identifiable.getId());
        identifiable.setId(new Integer(1));
        assertEquals(new Integer(1), identifiable.getId());
    }

    public void testInterceptorHasDependencies() {
        Aspects aspects = new Aspects();
        AspectsComponentAdapterFactory factory = new AspectsComponentAdapterFactory(
                        new DefaultComponentAdapterFactory(), aspects);
        MutablePicoContainer container = new DefaultPicoContainer(factory);

        aspects.interceptor(Pointcuts.instancesOf(MyComponent.class),
                        Pointcuts.ALL_METHODS, new PicoInterceptorFactory(
                                        container, LoggingInterceptor.class));
        container.registerComponentImplementation(LoggingInterceptor.class);

        StringBuffer log = new StringBuffer();
        container.registerComponentInstance(StringBuffer.class, log);

        container.registerComponentImplementation(MyComponent.class,
                        MyComponentImpl.class);

        MyComponent myComponent = (MyComponent) container
                        .getComponentInstance(MyComponent.class);

        myComponent.aMethod();
        assertEquals("startend", log.toString());
    }

    public void testAspectsConfiguredLate() {
        Aspects aspects = new Aspects();
        AspectsComponentAdapterFactory factory = new AspectsComponentAdapterFactory(
                        new DefaultComponentAdapterFactory(), aspects);
        MutablePicoContainer container = new DefaultPicoContainer(factory);

        container.registerComponentImplementation(LoggingInterceptor.class);

        StringBuffer log = new StringBuffer();
        container.registerComponentInstance(StringBuffer.class, log);

        container.registerComponentImplementation(MyComponent.class,
                        MyComponentImpl.class);

        aspects.interceptor(Pointcuts.instancesOf(MyComponent.class),
                        Pointcuts.ALL_METHODS, new PicoInterceptorFactory(
                                        container, LoggingInterceptor.class));

        MyComponent myComponent = (MyComponent) container
                        .getComponentInstance(MyComponent.class);

        myComponent.aMethod();
        assertEquals("startend", log.toString());
    }

    public void testInterceptorDependencyConfiguredAfterInterceptor() {
        Aspects aspects = new Aspects();

        AspectsComponentAdapterFactory factory = new AspectsComponentAdapterFactory(
                        new DefaultComponentAdapterFactory(), aspects);
        MutablePicoContainer container = new DefaultPicoContainer(factory);

        container.registerComponentImplementation(LoggingInterceptor.class);

        container.registerComponentImplementation(MyComponent.class,
                        MyComponentImpl.class);

        aspects.interceptor(Pointcuts.instancesOf(MyComponent.class),
                        Pointcuts.ALL_METHODS, new PicoInterceptorFactory(
                                        container, LoggingInterceptor.class));

        MyComponent myComponent = (MyComponent) container
                        .getComponentInstance(MyComponent.class);

        // Instantiate the log and register it in the
        // container after we've retrieved the component, myComponent, whose
        // interceptor
        // is going to use the log. Not recommened as it's confusing, but we're
        // just trying to make sure we don't have any temporal couplings.
        StringBuffer log = new StringBuffer();
        container.registerComponentInstance(StringBuffer.class, log);

        myComponent.aMethod();
        assertEquals("startend", log.toString());
    }

    public void testMixinFactory() {
        Aspects aspects = new Aspects();
        AspectsComponentAdapterFactory factory = new AspectsComponentAdapterFactory(
                        new DefaultComponentAdapterFactory(), aspects);

        MutablePicoContainer container = new DefaultPicoContainer(factory);
        container.registerComponentImplementation(MyComponent.class,
                        MyComponentImpl.class);
        container.registerComponentImplementation(IdentifiableMixin.class);

        aspects.mixin(Pointcuts.instancesOf(MyComponent.class),
                        new Class[]{Identifiable.class}, new PicoMixinFactory(
                                        container, IdentifiableMixin.class));

        MyComponent myComponent = (MyComponent) container
                        .getComponentInstance(MyComponent.class);

        Identifiable identifiable = (Identifiable) myComponent;
        assertEquals(new Integer(0), identifiable.getId());
        identifiable.setId(new Integer(1));
        assertEquals(new Integer(1), identifiable.getId());
    }

}