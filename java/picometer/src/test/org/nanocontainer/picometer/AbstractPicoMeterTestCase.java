package org.picoextras.picometer;

import junit.framework.TestCase;

import java.io.File;
import java.net.URL;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class AbstractPicoMeterTestCase extends TestCase {
    protected URL source;

    protected void setUp() throws Exception {
        String picometerHome = System.getProperty("picometer.home");
        assertNotNull(picometerHome);
        source = new File(picometerHome, "src/test/org/picoextras/picometer/AbstractPicoMeterTestCase.java").toURL();
    }

    public static class Dummy {
    }

    public static class InstantiatesOne {
        Exception e = new Exception();
    }

    public static class InstantiatesThree {
        Dummy dummy1 =
                new Dummy();

        public InstantiatesThree() {
            Dummy dummy2 = new Dummy();
        }

        private void doIt() {
            Dummy dummy = new Dummy();
        }
    }

    public static class OneInjection {
        Dummy dummy;

        public OneInjection(Dummy dummy) {
            this.dummy = dummy;
        }
    }
}
