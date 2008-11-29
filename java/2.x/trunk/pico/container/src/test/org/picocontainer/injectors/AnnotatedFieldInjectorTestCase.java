/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.injectors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.Test;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoBuilder;
import org.picocontainer.annotations.Inject;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;

public class AnnotatedFieldInjectorTestCase {

    public static class Helicopter {
        @Inject
        private PogoStick pogo;

        public Helicopter() {
        }
    }

    public static class Helicopter2 {
        private PogoStick pogo;

        public Helicopter2() {
        }
    }

    public static class PogoStick {
    }

    @Test
    public void testFieldInjection() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(new AnnotatedFieldInjector(Helicopter.class, Helicopter.class, null,
                new NullComponentMonitor(), new NullLifecycleStrategy(), Inject.class, false));
        pico.addComponent(PogoStick.class, new PogoStick());
        Helicopter chopper = pico.getComponent(Helicopter.class);
        assertNotNull(chopper);
        assertNotNull(chopper.pogo);
    }

    @Test
    public void testFieldInjectionWithoutAnnotationDoesNotWork() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(new AnnotatedFieldInjector(Helicopter2.class, Helicopter2.class, null,
                new NullComponentMonitor(), new NullLifecycleStrategy(), Inject.class, false));
        pico.addComponent(PogoStick.class, new PogoStick());
        Helicopter2 chopper = pico.getComponent(Helicopter2.class);
        assertNotNull(chopper);
        assertNull(chopper.pogo);
    }

    @Test
    public void testFieldDeosNotHappenWithoutRightInjectorDoesNotWork() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(new SetterInjector(Helicopter.class, Helicopter.class, null,
                new NullComponentMonitor(), new NullLifecycleStrategy(),
                "set", false));
        pico.addComponent(PogoStick.class, new PogoStick());
        Helicopter chopper = pico.getComponent(Helicopter.class);
        assertNotNull(chopper);
        assertNull(chopper.pogo);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(value = {ElementType.METHOD, ElementType.FIELD})
    public @interface AlternativeInject {
    }

    public static class Helicopter3 {
        @AlternativeInject
        private PogoStick pogo;

        public Helicopter3() {
        }
    }

    @Test
    public void testFieldInjectionWithAlternativeInjectionAnnotation() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(new AnnotatedFieldInjector(Helicopter3.class, Helicopter3.class, null,
                new NullComponentMonitor(), new NullLifecycleStrategy(), AlternativeInject.class, false));
        pico.addComponent(PogoStick.class, new PogoStick());
        Helicopter3 chopper = pico.getComponent(Helicopter3.class);
        assertNotNull(chopper);
        assertNotNull(chopper.pogo);
    }

    public static abstract class A {
        @Inject
        protected C c;
    }

    public static class B extends A {
    }

    public static class C {
    }

    @Test
    public void testThatSuperClassCanHaveAnnotatedFields() {
        MutablePicoContainer container = new PicoBuilder().withAnnotatedFieldInjection().build();
        container.addComponent(C.class);
        container.addComponent(B.class);

        B b = container.getComponent(B.class);
        assertNotNull(b);
        assertNotNull(b.c);
    }

    public static abstract class A2 {
        @Inject
        protected D2 d2;
    }

    public static abstract class B2 extends A2 {
    }

    public static class C2 extends B2 {
    }

    public static class D2 {
    }


    @Test
    public void testThatEvenMoreSuperClassCanHaveAnnotatedFields() {
        MutablePicoContainer container = new PicoBuilder().withAnnotatedFieldInjection().build();
        container.addComponent(D2.class);
        container.addComponent(C2.class);

        C2 c2 = container.getComponent(C2.class);
        assertNotNull(c2);
        assertNotNull(c2.d2);
    }

}