package org.picocontainer.injectors;

import org.picocontainer.Inject;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;

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
                                                    NullComponentMonitor.getInstance(), NullLifecycleStrategy.getInstance()));
        pico.addComponent(PogoStick.class, new PogoStick());
        Helicopter chopper = pico.getComponent(Helicopter.class);
        assertNotNull(chopper);
        //TODO assertNotNull(chopper.pogo);
    }

}
