package org.nanocontainer.script.groovy;

import org.nanocontainer.script.AbstractScriptedContainerBuilderTestCase;
import org.nanocontainer.integrationkit.PicoCompositionException;
import org.mozilla.javascript.JavaScriptException;
import org.picocontainer.PicoContainer;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class NanoGroovyBuilder2TestCase extends AbstractScriptedContainerBuilderTestCase {

    public void testNothing() {

    }

    public void doNOT_testInstantiateBasicScriptable() throws IOException, ClassNotFoundException, PicoCompositionException, JavaScriptException {

        // Hmmm NanoGroovyBuilder like this

        Reader script = new StringReader("" +
                "foo = 'eee'\n" +
//                "import org.nanocontainer.script.groovy.Xxx\n" +
//                "Xxx.reset()\n" +
//                "builder = new org.nanocontainer.script.groovy.NanoGroovyBuilder()\n" +
//                "pico = builder.container {\n" +
//                "    component(Xxx$A)\n" +
                "}");

        PicoContainer pico = buildContainer(new GroovyContainerBuilder(script, getClass().getClassLoader()), null);
        pico.start();
        pico.stop();

        assertEquals("Should match the expression", "<A!A", Xxx.componentRecorder);
    }

}
