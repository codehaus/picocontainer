/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package org.nanocontainer.script.groovy;

import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.nanocontainer.script.AbstractScriptedComposingLifecycleContainerBuilderTestCase;

import java.io.Reader;
import java.io.StringReader;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class GroovyContainerBuilderTestCase extends AbstractScriptedComposingLifecycleContainerBuilderTestCase {

    public void testContainerCanBeBuiltWithParent() {
        // * imports are not supported by groovy yet, so the GroovyContainerBuilder won't either.
        Reader script = new StringReader("" +
                "// No need to extend anything, but we must have a method with this signature\n" +
                "class MyContainerBuilder {\n" +
                "    buildContainer(parent, assemblyScope) {\n" +
                "        pico = new org.picocontainer.defaults.DefaultPicoContainer(parent)\n" +
                "        pico.registerComponentInstance(\"hello\", \"Groovy\")\n" +
                "        return pico\n" +
                "    }\n" +
                "}");
        PicoContainer parent = new DefaultPicoContainer();
        PicoContainer pico = buildContainer(new GroovyContainerBuilder(script, getClass().getClassLoader()), parent);
        assertSame(parent, pico.getParent());
        assertEquals("Groovy", pico.getComponentInstance("hello"));
    }

}
