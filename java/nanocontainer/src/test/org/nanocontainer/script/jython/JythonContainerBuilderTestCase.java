/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.script.jython;

import org.nanocontainer.integrationkit.PicoCompositionException;
import org.nanocontainer.script.AbstractScriptedContainerBuilderTestCase;
import org.nanocontainer.testmodel.WebServer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.UnsatisfiableDependenciesException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class JythonContainerBuilderTestCase extends AbstractScriptedContainerBuilderTestCase {

    public void testSimpleConfigurationIsPossible() {
        Reader script = new StringReader("from org.nanocontainer.testmodel import *\n" +
                "pico = DefaultPicoContainer()\n" +
                "pico.registerComponentImplementation(WebServerImpl)\n" +
                "pico.registerComponentImplementation(DefaultWebServerConfig)\n");

        PicoContainer pico = buildContainer(new JythonContainerBuilder(script, getClass().getClassLoader()), null);
        assertNotNull(pico.getComponentInstanceOfType(WebServer.class));
    }

    public void testDependenciesAreUnsatisfiableByChildContainers() throws IOException, ClassNotFoundException, PicoCompositionException {
        try {
            Reader script = new StringReader("" +
                    "from org.nanocontainer.testmodel import *\n" +
                    "pico = DefaultPicoContainer()\n" +
                    "pico.registerComponentImplementation(WebServerImpl)\n" +
                    "childContainer = DefaultPicoContainer(pico)\n" +
                    "childContainer.registerComponentImplementation(DefaultWebServerConfig)\n");
            PicoContainer pico = buildContainer(new JythonContainerBuilder(script, getClass().getClassLoader()), null);
            pico.getComponentInstanceOfType(WebServer.class);
            fail();
        } catch (UnsatisfiableDependenciesException e) {
        }
    }

    public void testDependenciesAreSatisfiableByParentContainer() throws IOException, ClassNotFoundException, PicoCompositionException {
        Reader script = new StringReader("" +
                "from org.nanocontainer.testmodel import *\n" +
                "pico = DefaultPicoContainer()\n" +
                "pico.registerComponentImplementation(DefaultWebServerConfig)\n" +
                "pico.registerComponentInstance('child', DefaultPicoContainer(pico))\n" +
                "child = pico.getComponentInstance('child')\n" +
                "child.registerComponentImplementation(WebServerImpl)\n");
        PicoContainer pico = buildContainer(new JythonContainerBuilder(script, getClass().getClassLoader()), null);
        PicoContainer child = (PicoContainer) pico.getComponentInstance("child");
        assertNotNull(child.getComponentInstanceOfType(WebServer.class));
    }

    public void testContainerCanBeBuiltWithParent() {
        Reader script = new StringReader("" +
                "pico = DefaultPicoContainer(parent)\n");
        PicoContainer parent = new DefaultPicoContainer();
        PicoContainer pico = buildContainer(new JythonContainerBuilder(script, getClass().getClassLoader()), parent);
        assertSame(parent, pico.getParent());
    }

}
