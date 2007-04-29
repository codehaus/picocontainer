package org.picocontainer;

import junit.framework.TestCase;
import com.thoughtworks.xstream.XStream;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.componentadapters.CachingAndConstructorComponentAdapterFactory;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.lifecycle.StartableLifecycleStrategy;
import org.picocontainer.lifecycle.ReflectionLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.monitors.ConsoleComponentMonitor;
import org.picocontainer.alternatives.EmptyPicoContainer;

public class PicoBuilderTestCase extends TestCase {


    public void testBasic() {
        MutablePicoContainer mpc = new PicoBuilder().build();
        XStream xs = new XStream();
        String foo = xs.toXML(mpc);
        assertTrue(foo.contains(DefaultPicoContainer.class.getName()));
        assertTrue(foo.contains(NullLifecycleStrategy.class.getName()));
        assertTrue(foo.contains(CachingAndConstructorComponentAdapterFactory.class.getName()));
        assertTrue(foo.contains(NullComponentMonitor.class.getName()));
        assertTrue(foo.contains(EmptyPicoContainer.class.getName())); // parent
    }

    public void testWithStartableLifecycle() {
        MutablePicoContainer mpc = new PicoBuilder().withStartableLifecycle().build();
        XStream xs = new XStream();
        String foo = xs.toXML(mpc);
        assertTrue(foo.contains(DefaultPicoContainer.class.getName()));
        assertTrue(foo.contains(StartableLifecycleStrategy.class.getName()));
        assertTrue(foo.contains(CachingAndConstructorComponentAdapterFactory.class.getName()));
        assertTrue(foo.contains(NullComponentMonitor.class.getName()));
        assertTrue(foo.contains(EmptyPicoContainer.class.getName())); // parent
    }

    public void testWithReflectionLifecycle() {
        MutablePicoContainer mpc = new PicoBuilder().withReflectionLifecycle().build();
        XStream xs = new XStream();
        String foo = xs.toXML(mpc);
        assertTrue(foo.contains(DefaultPicoContainer.class.getName()));
        assertTrue(foo.contains(ReflectionLifecycleStrategy.class.getName()));
        assertTrue(foo.contains(CachingAndConstructorComponentAdapterFactory.class.getName()));
        assertTrue(foo.contains(NullComponentMonitor.class.getName()));
        assertTrue(foo.contains(EmptyPicoContainer.class.getName())); // parent
    }


    public void testWithConsoleMonitor() {
        MutablePicoContainer mpc = new PicoBuilder().withConsoleMonitor().build();
        XStream xs = new XStream();
        String foo = xs.toXML(mpc);
        assertTrue(foo.contains(DefaultPicoContainer.class.getName()));
        assertTrue(foo.contains(NullLifecycleStrategy.class.getName()));
        assertTrue(foo.contains(CachingAndConstructorComponentAdapterFactory.class.getName()));
        assertTrue(foo.contains(ConsoleComponentMonitor.class.getName()));
        assertTrue(foo.contains(EmptyPicoContainer.class.getName())); // parent
    }




}
