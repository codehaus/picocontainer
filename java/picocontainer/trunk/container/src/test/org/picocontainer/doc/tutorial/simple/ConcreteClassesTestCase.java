package org.picocontainer.doc.tutorial.simple;

import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.doc.tutorial.interfaces.Kissable;
import org.picocontainer.defaults.DefaultPicoContainer;

public class ConcreteClassesTestCase extends TestCase {

    public void testAssembleComponentsAndInstantiateAndUseThem() {
        {
        // START SNIPPET: assemble
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation(Boy.class);
        pico.registerComponentImplementation(Girl.class);
        // END SNIPPET: assemble
        }
        {
        // START SNIPPET: assemble2
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation(Kissable.class, Boy.class);
        pico.registerComponentImplementation(Girl.class);
        // END SNIPPET: assemble2
        }
        MutablePicoContainer pico = null;
        // START SNIPPET: instantiate-and-use
        Girl girl = (Girl) pico.getComponentInstance(Girl.class);
        girl.kissSomeone();
        // END SNIPPET: instantiate-and-use
    }


}
