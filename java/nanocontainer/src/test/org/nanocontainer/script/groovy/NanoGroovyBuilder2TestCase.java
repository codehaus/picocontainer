package org.nanocontainer.script.groovy;

import java.io.Reader;
import java.io.StringReader;

import org.nanocontainer.integrationkit.PicoCompositionException;
import org.nanocontainer.script.AbstractScriptedContainerBuilderTestCase;
import org.picocontainer.PicoContainer;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class NanoGroovyBuilder2TestCase extends AbstractScriptedContainerBuilderTestCase {

    public void testInstantiateBasicScriptable() throws PicoCompositionException {
        Reader script = new StringReader("" +
                "import org.nanocontainer.script.groovy.Xxx\n" +
                "import org.nanocontainer.script.groovy.Xxx$A\n" +
                "Xxx.reset()\n" +
                "builder = new org.nanocontainer.script.groovy.NanoGroovyBuilder()\n" +
                "pico = builder.container {\n" +
                "    component(Xxx$A)\n" +
                "}");

        PicoContainer pico = buildContainer(new GroovyContainerBuilder(script, getClass().getClassLoader()), null);
        // LifecyleContainerBuilder starts the container
        pico.dispose();

        assertEquals("Should match the expression", "<A!A", Xxx.componentRecorder);
    }
    
    public void testComponentInstances() throws PicoCompositionException {
        Reader script = new StringReader("" +
                "import org.nanocontainer.script.groovy.Xxx$A\n" +
                "builder = new org.nanocontainer.script.groovy.NanoGroovyBuilder()\n" +
                "pico = builder.container {\n" +
                "    component(key:'a', instance:'apple')\n" +
                "    component(key:'b', instance:'banana')\n" +
                "    component(instance:'noKeySpecified')\n" +
                "}");

        PicoContainer pico = buildContainer(new GroovyContainerBuilder(script, getClass().getClassLoader()), null);
        assertEquals("apple", pico.getComponentInstance("a"));
        assertEquals("banana", pico.getComponentInstance("b"));
        assertEquals("noKeySpecified", pico.getComponentInstance(String.class));
    }
    
    public void testNeitherClassNorInstanceSpecified() {
        Reader script = new StringReader("" +
                "import org.nanocontainer.script.groovy.Xxx$A\n" +
                "builder = new org.nanocontainer.script.groovy.NanoGroovyBuilder()\n" +
                "pico = builder.container {\n" +
                "    component(key:'a')\n" +
                "}");
        
        try {
            buildContainer(new GroovyContainerBuilder(script, getClass().getClassLoader()), null);
            fail("PicoBuilderException should have been raised");
        } catch (PicoBuilderException e) {
            // expected
        }
    }

}
