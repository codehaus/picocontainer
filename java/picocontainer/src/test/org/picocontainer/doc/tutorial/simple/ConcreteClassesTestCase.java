package org.picocontainer.doc.tutorial.simple;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import junit.framework.TestCase;

public class ConcreteClassesTestCase extends TestCase {

    public void testAssembleComponentsAndInstantiateAndUseThem() {
        // START SNIPPET:
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation(Boy.class);
        pico.registerComponentImplementation(Girl.class);
        // END SNIPPET:

        // START SNIPPET:
        Girl girl = (Girl) pico.getComponentInstance(Girl.class);
        girl.kissSomeone();
        // END SNIPPET:
    }


}
