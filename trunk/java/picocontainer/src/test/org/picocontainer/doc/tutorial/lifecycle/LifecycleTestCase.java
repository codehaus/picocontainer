package org.picocontainer.doc.tutorial.lifecycle;

import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.doc.tutorial.interfaces.Boy;

public class LifecycleTestCase extends TestCase {

    public void testStartStopDispose() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation(Boy.class);
        pico.registerComponentImplementation(Girl.class);

// START SNIPPET: start
        pico.start();
// END SNIPPET: start

// START SNIPPET: stop-dispose
        pico.stop();
        pico.dispose();
// END SNIPPET: stop-dispose
    }


}
