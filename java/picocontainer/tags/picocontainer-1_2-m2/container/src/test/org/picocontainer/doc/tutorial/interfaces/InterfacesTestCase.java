package org.picocontainer.doc.tutorial.interfaces;

import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

public class InterfacesTestCase extends TestCase {
    public void testKissing() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation(Boy.class);
        pico.registerComponentImplementation(Girl.class);

        Girl girl = (Girl) pico.getComponentInstance(Girl.class);
        girl.kissSomeone();
    }
}
