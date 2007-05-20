package org.picocontainer;

import junit.framework.TestCase;
import com.thoughtworks.xstream.XStream;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.adapters.AnyInjectionComponentAdapterFactory;
import org.picocontainer.adapters.ImplementationHidingComponentAdapterFactory;
import org.picocontainer.adapters.SetterInjectionComponentAdapterFactory;
import org.picocontainer.adapters.AnnotationInjectionComponentAdapterFactory;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.lifecycle.StartableLifecycleStrategy;
import org.picocontainer.lifecycle.ReflectionLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.monitors.ConsoleComponentMonitor;
import org.picocontainer.alternatives.EmptyPicoContainer;

import java.util.HashMap;

public class PicoBuilderTestCase extends TestCase {

    XStream xs = new XStream();

    public void testBasic() {
        MutablePicoContainer mpc = new PicoBuilder().build();
        String foo = xs.toXML(mpc);
        assertTrue(foo.contains(DefaultPicoContainer.class.getName()));
        assertTrue(foo.contains(NullLifecycleStrategy.class.getName()));
        assertTrue(foo.contains(AnyInjectionComponentAdapterFactory.class.getName()));
        assertTrue(foo.contains(NullComponentMonitor.class.getName()));
        assertTrue(foo.contains(EmptyPicoContainer.class.getName())); // parent
    }

    public void testWithStartableLifecycle() {
        MutablePicoContainer mpc = new PicoBuilder().withLifecycle().build();
        String foo = xs.toXML(mpc);
        assertTrue(foo.contains(DefaultPicoContainer.class.getName()));
        assertTrue(foo.contains(StartableLifecycleStrategy.class.getName()));
        assertTrue(foo.contains(AnyInjectionComponentAdapterFactory.class.getName()));
        assertTrue(foo.contains(NullComponentMonitor.class.getName()));
        assertTrue(foo.contains(EmptyPicoContainer.class.getName())); // parent
    }

    public void testWithReflectionLifecycle() {
        MutablePicoContainer mpc = new PicoBuilder().withReflectionLifecycle().build();
        String foo = xs.toXML(mpc);
        assertTrue(foo.contains(DefaultPicoContainer.class.getName()));
        assertTrue(foo.contains(ReflectionLifecycleStrategy.class.getName()));
        assertTrue(foo.contains(AnyInjectionComponentAdapterFactory.class.getName()));
        assertTrue(foo.contains(NullComponentMonitor.class.getName()));
        assertTrue(foo.contains(EmptyPicoContainer.class.getName())); // parent
    }


    public void testWithConsoleMonitor() {
        MutablePicoContainer mpc = new PicoBuilder().withConsoleMonitor().build();
        String foo = xs.toXML(mpc);
        assertTrue(foo.contains(DefaultPicoContainer.class.getName()));
        assertTrue(foo.contains(NullLifecycleStrategy.class.getName()));
        assertTrue(foo.contains(AnyInjectionComponentAdapterFactory.class.getName()));
        assertTrue(foo.contains(ConsoleComponentMonitor.class.getName()));
        assertTrue(foo.contains(EmptyPicoContainer.class.getName())); // parent
    }

    public void testWithCustomMonitorByClass() {
        MutablePicoContainer mpc = new PicoBuilder().withMonitor(ConsoleComponentMonitor.class).build();
        String foo = xs.toXML(mpc);
        assertTrue(foo.contains(DefaultPicoContainer.class.getName()));
        assertTrue(foo.contains(NullLifecycleStrategy.class.getName()));
        assertTrue(foo.contains(AnyInjectionComponentAdapterFactory.class.getName()));
        assertTrue(foo.contains(ConsoleComponentMonitor.class.getName()));
        assertTrue(foo.contains(EmptyPicoContainer.class.getName())); // parent
    }
    
    public void testWithBogusCustomMonitorByClass() {
        try {
            new PicoBuilder().withMonitor(HashMap.class).build();
            fail("should have barfed");
        } catch (AssignabilityRegistrationException e) {
            // expected
        }
    }

    public void testWithImplementationHiding() {
        MutablePicoContainer mpc = new PicoBuilder().withHiddenImplementations().build();
        String foo = xs.toXML(mpc);
        assertTrue(foo.contains(DefaultPicoContainer.class.getName()));
        assertTrue(foo.contains(NullLifecycleStrategy.class.getName()));
        assertTrue(foo.contains(ImplementationHidingComponentAdapterFactory.class.getName()));
        assertTrue(foo.contains(AnyInjectionComponentAdapterFactory.class.getName()));
        assertTrue(foo.contains(NullComponentMonitor.class.getName()));
        assertTrue(foo.contains(EmptyPicoContainer.class.getName())); // parent
    }

    public static class CustomParentcontainer extends EmptyPicoContainer {}

    public void testWithCustomParentContainer() {
        MutablePicoContainer mpc = new PicoBuilder(new CustomParentcontainer()).build();
        String foo = xs.toXML(mpc);
        assertTrue(foo.contains(DefaultPicoContainer.class.getName()));
        assertTrue(foo.contains(NullLifecycleStrategy.class.getName()));
        assertTrue(foo.contains(AnyInjectionComponentAdapterFactory.class.getName()));
        assertTrue(foo.contains(NullComponentMonitor.class.getName()));
        assertTrue(foo.contains(CustomParentcontainer.class.getName())); // parent
    }

    public void testWithBogusParentContainer() {
        try {
            new PicoBuilder(null).build();
            fail("should have barfed");
        } catch (NullPointerException e) {
            //expected
        }
    }


    public void testWithSetterDI() {
        MutablePicoContainer mpc = new PicoBuilder().withSetterInjection().build();
        String foo = xs.toXML(mpc);
        assertTrue(foo.contains(DefaultPicoContainer.class.getName()));
        assertTrue(foo.contains(NullLifecycleStrategy.class.getName()));
        assertTrue(foo.contains(SetterInjectionComponentAdapterFactory.class.getName()));
        assertFalse(foo.contains(AnyInjectionComponentAdapterFactory.class.getName()));
        assertTrue(foo.contains(NullComponentMonitor.class.getName()));
        assertTrue(foo.contains(EmptyPicoContainer.class.getName())); // parent
    }

    public void testWithAnnotationDI() {
        MutablePicoContainer mpc = new PicoBuilder().withAnnotationInjection().build();
        String foo = xs.toXML(mpc);
        assertTrue(foo.contains(DefaultPicoContainer.class.getName()));
        assertTrue(foo.contains(NullLifecycleStrategy.class.getName()));
        assertTrue(foo.contains(AnnotationInjectionComponentAdapterFactory.class.getName()));
        assertFalse(foo.contains(AnyInjectionComponentAdapterFactory.class.getName()));
        assertTrue(foo.contains(NullComponentMonitor.class.getName()));
        assertTrue(foo.contains(EmptyPicoContainer.class.getName())); // parent
    }


}
