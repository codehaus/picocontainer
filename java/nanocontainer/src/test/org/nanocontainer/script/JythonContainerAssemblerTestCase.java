package org.picoextras.script;

import junit.framework.TestCase;
import org.picocontainer.PicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.UnsatisfiableDependenciesException;
import org.picoextras.integrationkit.ContainerAssembler;
import org.picoextras.integrationkit.PicoAssemblyException;
import org.picoextras.testmodel.WebServer;
import org.picoextras.script.jython.JythonContainerAssembler;

import java.io.Reader;
import java.io.StringReader;
import java.io.IOException;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class JythonContainerAssemblerTestCase extends TestCase {
    private PicoContainer assemble(Reader script) {
        MutablePicoContainer pico = new DefaultPicoContainer();
        ContainerAssembler assembler = new JythonContainerAssembler(script);
        assembler.assembleContainer(pico, null);
        return pico;
    }

    public void testSimpleConfigurationIsPossible() {
        Reader script = new StringReader("rootContainer.registerComponentImplementation('org.picoextras.testmodel.WebServerImpl')\n" +
                "rootContainer.registerComponentImplementation('org.picoextras.testmodel.DefaultWebServerConfig')\n");

        PicoContainer pico = assemble(script);
        assertNotNull(pico.getComponentInstanceOfType(WebServer.class));
    }

    public void testDependenciesAreUnsatisfiableByChildContainers() throws IOException, ClassNotFoundException, PicoAssemblyException {
        try {
            Reader script = new StringReader("" +
                    "rootContainer.registerComponentImplementation('org.picoextras.testmodel.WebServerImpl')\n" +
                    "childContainer = DefaultReflectionFrontEnd(rootContainer)\n" +
                    "childContainer.registerComponentImplementation('org.picoextras.testmodel.DefaultWebServerConfig')\n");
            PicoContainer pico = assemble(script);
            pico.getComponentInstanceOfType(WebServer.class);
            fail();
        } catch (UnsatisfiableDependenciesException e) {
        }
    }

    public void testDependenciesAreSatisfiableByParentContainer() throws IOException, ClassNotFoundException, PicoAssemblyException {
        Reader script = new StringReader("" +
                "rootContainer.registerComponentImplementation('org.picoextras.testmodel.DefaultWebServerConfig')\n" +
                "rootContainer.registerComponentImplementation('child', 'org.picocontainer.defaults.DefaultPicoContainer')\n" +
                "childContainer = DefaultReflectionFrontEnd(rootContainer.getPicoContainer().getComponentInstance('child'))\n" +
                "childContainer.registerComponentImplementation('org.picoextras.testmodel.WebServerImpl')\n");
        PicoContainer pico = assemble(script);
        PicoContainer child = (PicoContainer) pico.getComponentInstance("child");
        assertNotNull(child.getComponentInstanceOfType(WebServer.class));
    }

}
