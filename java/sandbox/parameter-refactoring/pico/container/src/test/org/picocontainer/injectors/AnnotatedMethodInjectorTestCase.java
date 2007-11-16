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

import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.annotations.Inject;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.injectors.AnnotatedMethodInjector;
import org.picocontainer.injectors.SetterInjector;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

public class AnnotatedMethodInjectorTestCase extends TestCase {

    public static class AnnotatedBurp {

        private Wind wind;

        @Inject
        public void windyWind(Wind wind) {
            this.wind = wind;
        }
    }

    public static class SetterBurp {

        private Wind wind;

        public void setWind(Wind wind) {
            this.wind = wind;
        }
    }

    public static class Wind {
    }

    public void testSetterMethodInjectionToContrastWithThatBelow() {

        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(new SetterInjector(SetterBurp.class, SetterBurp.class, Parameter.DEFAULT, new NullComponentMonitor(), new NullLifecycleStrategy(),
                                           "set", false));
        pico.addComponent(Wind.class, new Wind());
        SetterBurp burp = pico.getComponent(SetterBurp.class);
        assertNotNull(burp);
        assertNotNull(burp.wind);
    }

    public void testNonSetterMethodInjection() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(new AnnotatedMethodInjector(AnnotatedBurp.class, AnnotatedBurp.class, Parameter.DEFAULT,
                                               new NullComponentMonitor(), new NullLifecycleStrategy(),
                                               Inject.class, false));
        pico.addComponent(Wind.class, new Wind());
        AnnotatedBurp burp = pico.getComponent(AnnotatedBurp.class);
        assertNotNull(burp);
        assertNotNull(burp.wind);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(value={ ElementType.METHOD, ElementType.FIELD})
    public @interface AlternativeInject {
    }

    public static class AnotherAnnotatedBurp {
        private Wind wind;
        @AlternativeInject
        public void windyWind(Wind wind) {
            this.wind = wind;
        }
    }

    
    public void testNonSetterMethodInjectionWithAlternativeAnnotation() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(new AnnotatedMethodInjector(AnotherAnnotatedBurp.class, AnotherAnnotatedBurp.class, Parameter.DEFAULT,
                                               new NullComponentMonitor(), new NullLifecycleStrategy(),
                                               AlternativeInject.class, false));
        pico.addComponent(Wind.class, new Wind());
        AnotherAnnotatedBurp burp = pico.getComponent(AnotherAnnotatedBurp.class);
        assertNotNull(burp);
        assertNotNull(burp.wind);
    }


}
