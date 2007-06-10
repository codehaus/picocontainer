package org.picocontainer.defaults.issues;

import org.jmock.MockObjectTestCase;
import org.jmock.Mock;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.Startable;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.DefaultPicoContainerTestCase;

import java.lang.reflect.Method;

public class Issue0265TestCase extends MockObjectTestCase {

    public void testCanReallyChangeMonitor() throws SecurityException, NoSuchMethodException {
        Method start = Startable.class.getMethod("start");
        Method stop = Startable.class.getMethod("stop");
        Mock mockMonitor1 = mock(ComponentMonitor.class, "Monitor1");
        Mock mockMonitor2 = mock(ComponentMonitor.class, "Monitor2");
        DefaultPicoContainer pico = new DefaultPicoContainer((ComponentMonitor) mockMonitor1.proxy());
        pico.addComponent(DefaultPicoContainerTestCase.MyStartable.class);
        mockMonitor1.expects(once()).method("instantiating");
        mockMonitor1.expects(once()).method("instantiated");
        mockMonitor1.expects(once()).method("invoking").with(eq(start), ANYTHING);
        mockMonitor1.expects(once()).method("invoked").with(eq(start), ANYTHING, ANYTHING);
        mockMonitor1.expects(once()).method("invoking").with(eq(stop), ANYTHING);
        mockMonitor1.expects(once()).method("invoked").with(eq(stop), ANYTHING, ANYTHING);
        pico.start();
        pico.stop();
        Startable startable = pico.getComponent(DefaultPicoContainerTestCase.MyStartable.class);
        assertNotNull(startable);
        pico.changeMonitor((ComponentMonitor) mockMonitor2.proxy());
        mockMonitor2.expects(once()).method("invoking").with(eq(start), ANYTHING);
        mockMonitor2.expects(once()).method("invoked").with(eq(start), ANYTHING, ANYTHING);
        mockMonitor2.expects(once()).method("invoking").with(eq(stop), ANYTHING);
        mockMonitor2.expects(once()).method("invoked").with(eq(stop), ANYTHING, ANYTHING);
        pico.start();
        pico.stop();
    }

}
