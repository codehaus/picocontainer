package org.picoextras.script.jython;

import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.UnsatisfiableDependenciesException;
import org.picoextras.integrationkit.PicoAssemblyException;
import org.picoextras.script.AbstractScriptedAssemblingLifecycleContainerBuilderTestCase;
import org.picoextras.testmodel.WebServer;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class JythonContainerBuilderTestCase extends AbstractScriptedAssemblingLifecycleContainerBuilderTestCase {

    public void testSimpleConfigurationIsPossible() {
        Reader script = new StringReader(
                "from org.picoextras.testmodel import *\n" +
                "pico = DefaultPicoContainer()\n" +
                "pico.registerComponentImplementation(WebServerImpl)\n" +
                "pico.registerComponentImplementation(DefaultWebServerConfig)\n");

        PicoContainer pico = buildContainer(new JythonContainerBuilder(script, getClass().getClassLoader()));
        assertNotNull(pico.getComponentInstanceOfType(WebServer.class));
    }

    public void testDependenciesAreUnsatisfiableByChildContainers() throws IOException, ClassNotFoundException, PicoAssemblyException {
        try {
            Reader script = new StringReader("" +
                    "from org.picoextras.testmodel import *\n" +
                    "pico = DefaultPicoContainer()\n" +
                    "pico.registerComponentImplementation(WebServerImpl)\n" +
                    "childContainer = DefaultPicoContainer(pico)\n" +
                    "childContainer.registerComponentImplementation(DefaultWebServerConfig)\n");
            PicoContainer pico = buildContainer(new JythonContainerBuilder(script, getClass().getClassLoader()));
            pico.getComponentInstanceOfType(WebServer.class);
            fail();
        } catch (UnsatisfiableDependenciesException e) {
        }
    }

    public void testDependenciesAreSatisfiableByParentContainer() throws IOException, ClassNotFoundException, PicoAssemblyException {
        Reader script = new StringReader("" +
                "from org.picoextras.testmodel import *\n" +
                "pico = DefaultPicoContainer()\n" +
                "pico.registerComponentImplementation(DefaultWebServerConfig)\n" +
                "pico.registerComponentInstance('child', DefaultPicoContainer(pico))\n" +
                "child = pico.getComponentInstance('child')\n" +
                "child.registerComponentImplementation(WebServerImpl)\n");
        PicoContainer pico = buildContainer(new JythonContainerBuilder(script, getClass().getClassLoader()));
        PicoContainer child = (PicoContainer) pico.getComponentInstance("child");
        assertNotNull(child.getComponentInstanceOfType(WebServer.class));
    }

}
