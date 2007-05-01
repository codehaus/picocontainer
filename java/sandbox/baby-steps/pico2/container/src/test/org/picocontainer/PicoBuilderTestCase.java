package org.picocontainer;

import junit.framework.TestCase;
import com.thoughtworks.xstream.XStream;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.componentadapters.CachingAndConstructorComponentAdapterFactory;
import org.picocontainer.componentadapters.ImplementationHidingComponentAdapter;
import org.picocontainer.componentadapters.ImplementationHidingComponentAdapterFactory;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.lifecycle.StartableLifecycleStrategy;
import org.picocontainer.lifecycle.ReflectionLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.monitors.ConsoleComponentMonitor;
import org.picocontainer.alternatives.EmptyPicoContainer;

public class PicoBuilderTestCase extends TestCase {

    XStream xs = new XStream();



    public void testBasic() {
        MutablePicoContainer mpc = new PicoBuilder().build();
        String foo = xs.toXML(mpc);
        assertTrue(foo.contains(DefaultPicoContainer.class.getName()));
        assertTrue(foo.contains(NullLifecycleStrategy.class.getName()));
        assertTrue(foo.contains(CachingAndConstructorComponentAdapterFactory.class.getName()));
        assertTrue(foo.contains(NullComponentMonitor.class.getName()));
        assertTrue(foo.contains(EmptyPicoContainer.class.getName())); // parent
    }

    public void testWithStartableLifecycle() {
        MutablePicoContainer mpc = new PicoBuilder().withStartableLifecycle().build();
        String foo = xs.toXML(mpc);
        assertTrue(foo.contains(DefaultPicoContainer.class.getName()));
        assertTrue(foo.contains(StartableLifecycleStrategy.class.getName()));
        assertTrue(foo.contains(CachingAndConstructorComponentAdapterFactory.class.getName()));
        assertTrue(foo.contains(NullComponentMonitor.class.getName()));
        assertTrue(foo.contains(EmptyPicoContainer.class.getName())); // parent
    }

    public void testWithReflectionLifecycle() {
        MutablePicoContainer mpc = new PicoBuilder().withReflectionLifecycle().build();
        String foo = xs.toXML(mpc);
        assertTrue(foo.contains(DefaultPicoContainer.class.getName()));
        assertTrue(foo.contains(ReflectionLifecycleStrategy.class.getName()));
        assertTrue(foo.contains(CachingAndConstructorComponentAdapterFactory.class.getName()));
        assertTrue(foo.contains(NullComponentMonitor.class.getName()));
        assertTrue(foo.contains(EmptyPicoContainer.class.getName())); // parent
    }


    public void testWithConsoleMonitor() {
        MutablePicoContainer mpc = new PicoBuilder().withConsoleMonitor().build();
        String foo = xs.toXML(mpc);
        assertTrue(foo.contains(DefaultPicoContainer.class.getName()));
        assertTrue(foo.contains(NullLifecycleStrategy.class.getName()));
        assertTrue(foo.contains(CachingAndConstructorComponentAdapterFactory.class.getName()));
        assertTrue(foo.contains(ConsoleComponentMonitor.class.getName()));
        assertTrue(foo.contains(EmptyPicoContainer.class.getName())); // parent
    }

    public void testWithImplementationHiding() {
        MutablePicoContainer mpc = new PicoBuilder().withImplementationHiding().build();
        String foo = xs.toXML(mpc);
        System.err.println("-->" + foo);
        assertTrue(foo.contains(DefaultPicoContainer.class.getName()));
        assertTrue(foo.contains(NullLifecycleStrategy.class.getName()));
        assertTrue(foo.contains(ImplementationHidingComponentAdapterFactory.class.getName()));
        assertTrue(foo.contains(CachingAndConstructorComponentAdapterFactory.class.getName()));
        assertTrue(foo.contains(NullComponentMonitor.class.getName()));
        assertTrue(foo.contains(EmptyPicoContainer.class.getName())); // parent
    }

    public static class CustomParentcontainer extends EmptyPicoContainer {}

    public void testWithCustomParentContainer() {
        MutablePicoContainer mpc = new PicoBuilder(new CustomParentcontainer()).build();
        String foo = xs.toXML(mpc);
        System.err.println("-->" + foo);
        assertTrue(foo.contains(DefaultPicoContainer.class.getName()));
        assertTrue(foo.contains(NullLifecycleStrategy.class.getName()));
        assertTrue(foo.contains(CachingAndConstructorComponentAdapterFactory.class.getName()));
        assertTrue(foo.contains(NullComponentMonitor.class.getName()));
        assertTrue(foo.contains(CustomParentcontainer.class.getName().replace("$","-"))); // parent
    }


}
