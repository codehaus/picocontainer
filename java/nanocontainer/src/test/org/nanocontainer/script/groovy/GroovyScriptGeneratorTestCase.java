package org.nanocontainer.script.groovy;

import java.io.StringReader;
import java.util.ArrayList;
import junit.framework.TestCase;
import org.nanocontainer.SoftCompositionPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class GroovyScriptGeneratorTestCase extends TestCase {
    public void testShouldWriteAGroovyScriptThatAllowsToRecreateASimilarContainer() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation(ArrayList.class);
        pico.registerComponentInstance("Hello", "World");

        GroovyScriptGenerator groovyScriptGenerator = new GroovyScriptGenerator();
        String script = groovyScriptGenerator.generateScript(pico);

//        System.out.println(script);

        GroovyContainerBuilder groovyContainerBuilder = new GroovyContainerBuilder(new StringReader(script), getClass().getClassLoader());
        SoftCompositionPicoContainer newPico = groovyContainerBuilder.createContainerFromScript(null, null);

        assertNotNull(newPico.getComponentInstanceOfType(ArrayList.class));
        assertEquals("World", newPico.getComponentInstance("Hello"));
    }
}