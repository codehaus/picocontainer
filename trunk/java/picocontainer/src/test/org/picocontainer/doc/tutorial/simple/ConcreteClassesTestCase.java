package org.picocontainer.doc.tutorial.simple;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import junit.framework.TestCase;

public class ConcreteClassesTestCase extends TestCase {

    public void testAssembleComponentsAndInstantiateAndUseThem() {
        // START SNIPPET: assemble
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation(Boy.class);
        pico.registerComponentImplementation(Girl.class);
        // END SNIPPET: assemble

        // START SNIPPET: instantiate-and-use
        Girl girl = (Girl) pico.getComponentInstance(Girl.class);
        girl.kissSomeone();
        // END SNIPPET: instantiate-and-use
    }


}
