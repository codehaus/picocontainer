package org.picocontainer.injectors;

import org.picocontainer.Inject;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.monitors.ConsoleComponentMonitor;

import junit.framework.TestCase;

public class FieldAnnotationInjectionAdapterTestCase extends TestCase {

    public static class Helicopter {
        @Inject
        private PogoStick pogo;

        public Helicopter() {
        }
    }

    public static class PogoStick {
    }

    public void testFieldInjection() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(new FieldAnnotationInjector(Helicopter.class, Helicopter.class, null,
                                                    new ConsoleComponentMonitor(), NullLifecycleStrategy.getInstance()));
        pico.addComponent(PogoStick.class, new PogoStick());
        Helicopter chopper = pico.getComponent(Helicopter.class);
        assertNotNull(chopper);
        assertNotNull(chopper.pogo);
    }

    public void testFieldDeosNotHappenWithoutRightInjector() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(new SetterInjector(Helicopter.class, Helicopter.class, null,
                                                    new ConsoleComponentMonitor(), NullLifecycleStrategy.getInstance()));
        pico.addComponent(PogoStick.class, new PogoStick());
        Helicopter chopper = pico.getComponent(Helicopter.class);
        assertNotNull(chopper);
        assertNull(chopper.pogo);
    }




}
