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

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.mozilla.javascript.JavaScriptException;
import org.nanocontainer.SoftCompositionPicoContainer;
import org.nanocontainer.integrationkit.PicoCompositionException;
import org.nanocontainer.reflection.DefaultSoftCompositionPicoContainer;
import org.nanocontainer.script.AbstractScriptedContainerBuilderTestCase;
import org.nanocontainer.testmodel.WebServer;
import org.nanocontainer.testmodel.WebServerConfig;

public class JavascriptContainerBuilderTestCase extends AbstractScriptedContainerBuilderTestCase {

    public void testInstantiateBasicScriptable() throws IOException, ClassNotFoundException, PicoCompositionException, JavaScriptException {

        Reader script = new StringReader("" +
                "var pico = new DefaultSoftCompositionPicoContainer()\n" +
                "pico.registerComponentImplementation(Packages.org.nanocontainer.testmodel.DefaultWebServerConfig)\n");

        SoftCompositionPicoContainer pico = buildContainer(new JavascriptContainerBuilder(script, getClass().getClassLoader()), null);

        assertNotNull(pico.getComponentInstanceOfType(WebServerConfig.class).getClass());
    }

    public void testInstantiateWithBespokeComponentAdapter() throws IOException, ClassNotFoundException, PicoCompositionException, JavaScriptException {

        Reader script = new StringReader("" +
                "var pico = new DefaultSoftCompositionPicoContainer(new ConstructorInjectionComponentAdapterFactory())\n" +
                "pico.registerComponentImplementation(Packages.org.nanocontainer.testmodel.DefaultWebServerConfig)\n" +
                "pico.registerComponentImplementation(Packages.org.nanocontainer.testmodel.WebServerImpl)\n");

        SoftCompositionPicoContainer pico = buildContainer(new JavascriptContainerBuilder(script, getClass().getClassLoader()), null);

        Object wsc = pico.getComponentInstanceOfType(WebServerConfig.class);
        Object ws1 = pico.getComponentInstanceOfType(WebServer.class);
        Object ws2 = pico.getComponentInstanceOfType(WebServer.class);

        assertNotSame(ws1, ws2);

        assertEquals("ClassLoader should be the same for both components", ws1.getClass().getClassLoader(), wsc.getClass().getClassLoader());
    }

    public void testClassLoaderHierarchy() throws ClassNotFoundException, IOException, PicoCompositionException, JavaScriptException {

        String testcompJarFileName = System.getProperty("testcomp.jar");
        // Paul's path to TestComp. PLEASE do not take out.
        //testcompJarFileName = "D:/OSS/PN/java/nanocontainer/src/test-comp/TestComp.jar";

        assertNotNull("The testcomp.jar system property should point to TestComp.jar", testcompJarFileName);
        File testCompJar = new File(testcompJarFileName);
        assertTrue(testCompJar.isFile());

        final String testCompJarPath = testCompJar.getCanonicalPath().replace('\\', '/');
        Reader script = new StringReader(
                "var pico = new DefaultSoftCompositionPicoContainer()\n" +
                "pico.registerComponentImplementation('parentComponent', Packages." + FooTestComp.class.getName() + ")\n" +
                "child = new DefaultSoftCompositionPicoContainer(pico)\n" +
                "pico.addChildContainer(child)\n" +
                "url = new File('" + testCompJarPath + "').toURL()\n" +
                "child.addClassLoaderURL(url)\n" +
                "child.registerComponentImplementation('childComponent','TestComp')\n" +
                "pico.registerComponentInstance('wayOfPassingSomethingToTestEnv', child.getComponentInstance('childComponent'))"); // ugly hack for testing
        JavascriptContainerBuilder builder = new JavascriptContainerBuilder(script, getClass().getClassLoader());
        SoftCompositionPicoContainer pico = buildContainer(builder, null);

        Object parentComponent = pico.getComponentInstance("parentComponent");

        Object childComponent = pico.getComponentInstance("wayOfPassingSomethingToTestEnv");

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
                "var pico = new DefaultSoftCompositionPicoContainer()\n" +
                "pico.registerComponentInstance( new Packages." + FooTestComp.class.getName() + "())\n" +
                "pico.registerComponentInstance( 'foo', new Packages." + FooTestComp.class.getName() + "())\n");

        SoftCompositionPicoContainer pico = buildContainer(new JavascriptContainerBuilder(script, getClass().getClassLoader()), null);

        assertEquals(FooTestComp.class, pico.getComponentInstances().get(0).getClass());
        assertEquals(FooTestComp.class, pico.getComponentInstances().get(1).getClass());
    }

    public static class FooTestComp {

    }

    public void testContainerCanBeBuiltWithParent() {
        Reader script = new StringReader("" +
                "var pico = new DefaultSoftCompositionPicoContainer(parent)\n");
        SoftCompositionPicoContainer parent = new DefaultSoftCompositionPicoContainer();
        SoftCompositionPicoContainer pico = buildContainer(new JavascriptContainerBuilder(script, getClass().getClassLoader()), parent);
        //PicoContainer.getParent() is now ImmutablePicoContainer
        assertNotSame(parent, pico.getParent());
    }
}
