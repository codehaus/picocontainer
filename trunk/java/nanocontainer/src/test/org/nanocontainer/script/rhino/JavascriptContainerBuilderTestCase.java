/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.script.rhino;

import org.mozilla.javascript.JavaScriptException;
import org.nanocontainer.integrationkit.PicoCompositionException;
import org.nanocontainer.script.AbstractScriptedContainerBuilderTestCase;
import org.nanocontainer.testmodel.WebServer;
import org.nanocontainer.testmodel.WebServerConfig;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class JavascriptContainerBuilderTestCase extends AbstractScriptedContainerBuilderTestCase {

    public void testInstantiateBasicScriptable() throws IOException, ClassNotFoundException, PicoCompositionException, JavaScriptException {

        Reader script = new StringReader("" +
                "var pico = new DefaultPicoContainer()\n" +
                "pico.registerComponentImplementation(Packages.org.nanocontainer.testmodel.DefaultWebServerConfig)\n");

        PicoContainer pico = buildContainer(new JavascriptContainerBuilder(script, getClass().getClassLoader()), null);

        assertNotNull(pico.getComponentInstanceOfType(WebServerConfig.class).getClass());
    }

    public void testInstantiateWithBespokeComponentAdapter() throws IOException, ClassNotFoundException, PicoCompositionException, JavaScriptException {

        Reader script = new StringReader("" +
                "var pico = new DefaultPicoContainer(new ConstructorInjectionComponentAdapterFactory())\n" +
                "pico.registerComponentImplementation(Packages.org.nanocontainer.testmodel.DefaultWebServerConfig)\n" +
                "pico.registerComponentImplementation(Packages.org.nanocontainer.testmodel.WebServerImpl)\n");

        PicoContainer pico = buildContainer(new JavascriptContainerBuilder(script, getClass().getClassLoader()), null);

        Object wsc = pico.getComponentInstanceOfType(WebServerConfig.class);
        Object ws1 = pico.getComponentInstanceOfType(WebServer.class);
        Object ws2 = pico.getComponentInstanceOfType(WebServer.class);

        assertNotSame(ws1, ws2);

        assertEquals("ClassLoader should be the same for both components", ws1.getClass().getClassLoader(), wsc.getClass().getClassLoader());
    }

    public void testClassLoaderHierarchy() throws ClassNotFoundException, IOException, PicoCompositionException, JavaScriptException {

        String testcompJarFileName = System.getProperty("testcomp.jar");
        // Paul's path to TestComp. PLEASE do not take out.
        //testcompJarFileName = "D:/DEV/nano/reflection/src/test-comp/TestComp.jar";

        assertNotNull("The testcomp.jar system property should point to TestComp.jar", testcompJarFileName);
        File testCompJar = new File(testcompJarFileName);
        assertTrue(testCompJar.isFile());

        final String testCompJarPath = testCompJar.getCanonicalPath().replace('\\', '/');
        Reader script = new StringReader(
                "var pico = new DefaultPicoContainer()\n" +
                "pico.registerComponentImplementation('parentComponent', Packages." + FooTestComp.class.getName() + ")\n" +
                "child = new SoftCompositionPicoContainer(pico)\n" +
                "url = new File('" + testCompJarPath + "').toURL()\n" +
                "child.addClassLoaderURL(url)\n" +
                "child.registerComponentImplementation('childComponent','TestComp')\n" +
                "pico.registerComponentInstance('child1', child)"); // ugly hack for testing
        PicoContainer pico = buildContainer(new JavascriptContainerBuilder(script, getClass().getClassLoader()), null);

        Object parentComponent = pico.getComponentInstance("parentComponent");

        PicoContainer childContainer = (PicoContainer) pico.getComponentInstance("child1");
        Object childComponent = childContainer.getComponentInstance("childComponent");

        assertNotSame(parentComponent.getClass().getClassLoader(), childComponent.getClass().getClassLoader());
        /*
        system cl -> loads FooTestComp
          parent container cl
            child container cl -> loads TestComp
        */
        assertSame(parentComponent.getClass().getClassLoader(), childComponent.getClass().getClassLoader().getParent());
    }

    public void testRegisterComponentInstance() throws JavaScriptException, IOException {
        Reader script = new StringReader("" +
                "var pico = new DefaultPicoContainer()\n" +
                "pico.registerComponentInstance( new Packages." + FooTestComp.class.getName() + "())\n" +
                "pico.registerComponentInstance( 'foo', new Packages." + FooTestComp.class.getName() + "())\n");

        PicoContainer pico = buildContainer(new JavascriptContainerBuilder(script, getClass().getClassLoader()), null);

        assertEquals(FooTestComp.class, pico.getComponentInstances().get(0).getClass());
        assertEquals(FooTestComp.class, pico.getComponentInstances().get(1).getClass());
    }

    public static class FooTestComp {

    }

    public void testContainerCanBeBuiltWithParent() {
        Reader script = new StringReader("" +
                "var pico = new DefaultPicoContainer(parent)\n");
        PicoContainer parent = new DefaultPicoContainer();
        PicoContainer pico = buildContainer(new JavascriptContainerBuilder(script, getClass().getClassLoader()), parent);
        assertSame(parent, pico.getParent());
    }
}
