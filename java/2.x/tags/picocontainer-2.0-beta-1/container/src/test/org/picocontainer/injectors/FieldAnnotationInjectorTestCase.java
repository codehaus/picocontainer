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

import org.picocontainer.annotations.Inject;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;

import junit.framework.TestCase;

public class FieldAnnotationInjectorTestCase extends TestCase {

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

    public void testFieldInjection() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(new FieldAnnotationInjector(Helicopter.class, Helicopter.class, null,
                                                    new NullComponentMonitor(), new NullLifecycleStrategy()));
        pico.addComponent(PogoStick.class, new PogoStick());
        Helicopter chopper = pico.getComponent(Helicopter.class);
        assertNotNull(chopper);
        assertNotNull(chopper.pogo);
    }

    public void testFieldInjectionWithoutAnnotationDoesNotWork() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(new FieldAnnotationInjector(Helicopter2.class, Helicopter2.class, null,
                                                    new NullComponentMonitor(), new NullLifecycleStrategy()));
        pico.addComponent(PogoStick.class, new PogoStick());
        Helicopter2 chopper = pico.getComponent(Helicopter2.class);
        assertNotNull(chopper);
        assertNull(chopper.pogo);
    }

    public void testFieldDeosNotHappenWithoutRightInjectorDoesNotWork() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(new SetterInjector(Helicopter.class, Helicopter.class, null,
                                                    new NullComponentMonitor(), new NullLifecycleStrategy()));
        pico.addComponent(PogoStick.class, new PogoStick());
        Helicopter chopper = pico.getComponent(Helicopter.class);
        assertNotNull(chopper);
        assertNull(chopper.pogo);
    }




}
