package org.nanocontainer;

import junit.framework.TestCase;
import junit.framework.Assert;
import org.picocontainer.lifecycle.Lifecycle;
import org.picocontainer.lifecycle.LifecyclePicoAdapter;
import org.picocontainer.PicoContainer;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.Vector;

/**
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @author Ward Cunningham
 * @version $Revision$
 */
public class NanoContainerTestCase extends TestCase {
    private static String componentRecorder = "";
    private static String monitorRecorder = "";

    public abstract static class X implements Lifecycle {
        public void start() {
            componentRecorder += "<" + code();
        }

        public void stop() {
            componentRecorder += code() + ">";
        }

        public void dispose() {
            componentRecorder += "!" + code();
        }

        private String code() {
            String name = getClass().getName();
            return name.substring(name.length()-1);
        }
    }

    public static class MockMonitor implements NanoContainerMonitor {

        private String code(Object inst) {
            String name = inst.getClass().getName();
            return name.substring(name.length()-1);
        }

        public void componentsLifecycleEvent(String eventName, LifecyclePicoAdapter lpa) {
            monitorRecorder += ("+" + code(lpa.getPicoContainer().getComponentInstances().get(0)) + "_" + eventName) ;
        }

        public void componentsInstantiated(PicoContainer picoContainer) {
            monitorRecorder += "*" +code(picoContainer.getComponentInstances().get(0));
        }
    }

    public static class A extends X {}
    public static class B extends X {
        public B(A a) {
            Assert.assertNotNull(a);
        }
    }
    public static class C extends X {}

    public void testInstantiate() throws ClassNotFoundException, SAXException, ParserConfigurationException, IOException {
        NanoContainer nano = new NanoContainer(new StringReader("" +
                "<container>" +
                "      <component classname='org.nanocontainer.NanoContainerTestCase$A'/>" +
                "      <container>" +
                "          <component classname='org.nanocontainer.NanoContainerTestCase$C'/>" +
                "      </container>" +
                "      <component classname='org.nanocontainer.NanoContainerTestCase$B'/>" +
                "</container>"), new MockMonitor());
        nano.stopComponentsDepthFirst();
        nano.disposeComponentsDepthFirst();

        assertEquals("<A<B<CC>B>A>!C!B!A", componentRecorder);
        assertEquals("*A*C+A_started+C_started+C_stopped+A_stopped+C_disposed+A_disposed", monitorRecorder);
    }
}
