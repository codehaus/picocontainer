package org.nanocontainer;

import junit.framework.TestCase;
import org.picocontainer.lifecycle.Lifecycle;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.Vector;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class NanoContainerTestCase extends TestCase {
    private static final Vector forOldTimesSake = new Vector();

    public abstract static class X implements Lifecycle {
        public void start() {
            forOldTimesSake.addElement("start:" + getClass().getName());
        }

        public void stop() {
            forOldTimesSake.addElement("stop:" + getClass().getName());
        }

        public void dispose() {
            forOldTimesSake.addElement("dispose:" + getClass().getName());
        }
    }

    public static class A extends X {}
    public static class B extends X {
        public B(A a) {
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
                "</container>"));
        nano.stopComponentsDepthFirst();
        nano.disposeComponentsDepthFirst();

        assertEquals("start:org.nanocontainer.NanoContainerTestCase$A", forOldTimesSake.elementAt(0));
        assertEquals("start:org.nanocontainer.NanoContainerTestCase$B", forOldTimesSake.elementAt(1));
        assertEquals("start:org.nanocontainer.NanoContainerTestCase$C", forOldTimesSake.elementAt(2));
        assertEquals("stop:org.nanocontainer.NanoContainerTestCase$C", forOldTimesSake.elementAt(3));
        assertEquals("stop:org.nanocontainer.NanoContainerTestCase$B", forOldTimesSake.elementAt(4));
        assertEquals("stop:org.nanocontainer.NanoContainerTestCase$A", forOldTimesSake.elementAt(5));
        assertEquals("dispose:org.nanocontainer.NanoContainerTestCase$C", forOldTimesSake.elementAt(6));
        assertEquals("dispose:org.nanocontainer.NanoContainerTestCase$B", forOldTimesSake.elementAt(7));
        assertEquals("dispose:org.nanocontainer.NanoContainerTestCase$A", forOldTimesSake.elementAt(8));
    }
}
