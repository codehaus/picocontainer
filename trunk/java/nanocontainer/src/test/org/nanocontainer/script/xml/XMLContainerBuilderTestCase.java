/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Aslak Hellesoy and Paul Hammant                          *
 *****************************************************************************/

package org.nanocontainer.script.xml;

import org.nanocontainer.integrationkit.PicoCompositionException;
import org.nanocontainer.script.AbstractScriptedContainerBuilderTestCase;
import org.nanocontainer.testmodel.DefaultWebServerConfig;
import org.nanocontainer.testmodel.WebServer;
import org.nanocontainer.testmodel.WebServerConfig;
import org.nanocontainer.testmodel.WebServerConfigComp;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapterFactory;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Jeppe Cramon
 * @version $Revision$
 */
public class XMLContainerBuilderTestCase extends AbstractScriptedContainerBuilderTestCase {

    public void testCreateSimpleContainer() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, PicoCompositionException {
        Reader script = new StringReader("" +
                "<container>" +
                "  <component class='org.nanocontainer.testmodel.DefaultWebServerConfig'/>" +
                "  <component key='org.nanocontainer.testmodel.WebServer' class='org.nanocontainer.testmodel.WebServerImpl'/>" +
                "</container>");

        PicoContainer pico = buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()), null);
        assertEquals(2, pico.getComponentInstances().size());
        assertNotNull(pico.getComponentInstance(DefaultWebServerConfig.class));
    }

    public void testAPicocontainerCanHostItself() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, PicoCompositionException {
        Reader script = new StringReader("" +
                "<component class='org.picocontainer.defaults.DefaultPicoContainer'>" +
                "  <component class='org.nanocontainer.testmodel.DefaultWebServerConfig'/>" +
                "  <component key='child1' class='org.picocontainer.defaults.DefaultPicoContainer'>" +
                "    <component key='org.nanocontainer.testmodel.WebServer' class='org.nanocontainer.testmodel.WebServerImpl'/>" +
                "  </component>" +
                "</component>");

        PicoContainer pico = buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()), null);
        assertNotNull(pico.getComponentInstance(DefaultWebServerConfig.class));

        PicoContainer childContainer = (PicoContainer) pico.getComponentInstance("child1");
        assertNotNull(childContainer.getComponentInstance(WebServer.class.getName()));
    }

    public void testClassLoaderHierarchy() throws ParserConfigurationException, ClassNotFoundException, SAXException, IOException, PicoCompositionException {

        String testcompJarFileName = System.getProperty("testcomp.jar");
        // Paul's path to TestComp. PLEASE do not take out.
        //testcompJarFileName = "D:/DEV/nano/reflection/src/test-comp/TestComp.jar";

        assertNotNull("The testcomp.jar system property should point to nanocontainer/src/test-comp/TestComp.jar", testcompJarFileName);
        File testCompJar = new File(testcompJarFileName);
        File testCompJar2 = new File(testCompJar.getParentFile(), "TestComp2.jar");
        assertTrue(testCompJar.isFile());
        assertTrue(testCompJar2.isFile());

        Reader script = new StringReader("" +
                "<container>" +
                "  <classpath>" +
                "    <element file='" + testCompJar.getCanonicalPath() + "'/>" +
                "  </classpath>" +
                "  <component key='foo' class='TestComp'/>" +
                "  <component key='child1' class='org.picocontainer.defaults.DefaultPicoContainer'>" +
                "    <classpath>" +
                "      <element file='" + testCompJar2.getCanonicalPath() + "'/>" +
                "    </classpath>" +
                "    <component key='bar' class='TestComp2'/>" +
                "  </component>" +
                "</container>");

        PicoContainer pico = buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()), null);

        Object fooTestComp = pico.getComponentInstance("foo");
        assertNotNull("Container should have a 'foo' component", fooTestComp);

        PicoContainer childContainer = (PicoContainer) pico.getComponentInstance("child1");
        Object barTestComp = childContainer.getComponentInstance("bar");
        assertNotNull("Container should have a 'bar' component", barTestComp);

        ClassLoader fooLoader = fooTestComp.getClass().getClassLoader();
        ClassLoader barLoader = barTestComp.getClass().getClassLoader();
        assertSame("foo classloader should be parent of bar", fooLoader,
                barLoader.getParent());

    }

    public void testUnknownclassThrowsAssemblyException() throws Exception, SAXException, ParserConfigurationException, IOException {

        try {
            Reader script = new StringReader("" +
                    "<container>" +
                    "  <component class='Foo'/>" +
                    "</container>");
            buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()), null);
            fail("Should have thrown a ClassNotFoundException");
        } catch (RuntimeException cnfe) {
        }
    }

    public void testUnknownclassThrowsEmptyCompositionException() throws Exception, SAXException, ParserConfigurationException, IOException {

        try {
            Reader script = new StringReader("<container/>");
            buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()), null);
        } catch (EmptyCompositionException cnfe) {
        }
    }

    public void testPseudocreation() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, PicoCompositionException {
        Reader script = new StringReader("" +
                "<container>" +
                "  <pseudocomponent factory='org.nanocontainer.script.xml.XMLContainerBuilderTestCase$TestFactory'>" +
                "    <config-or-whatever/>" +
                "  </pseudocomponent>" +
                "</container>");

        PicoContainer pico = buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()), null);
        assertNotNull(pico.getComponentInstances().get(0));
        assertTrue(pico.getComponentInstances().get(0) instanceof String);
        assertEquals("Hello", pico.getComponentInstances().get(0).toString());

    }

    public static class TestFactory implements XMLPseudoComponentFactory {
        public Object makeInstance(Element elem) throws SAXException, ClassNotFoundException {
            return "Hello";
        }
    }

    public void testInstantiationOfComponentsWithParams() throws IOException, SAXException, ClassNotFoundException, ParserConfigurationException {
        Reader script = new StringReader("" +
                "<container>" +
                "  <component class='org.nanocontainer.testmodel.WebServerConfigComp'>" +
                "    <parameter class='java.lang.String'>localhost</parameter>" +
                "    <parameter class='int'>8080</parameter>" +
                "  </component>" +
                "  <component key='org.nanocontainer.testmodel.WebServer' class='org.nanocontainer.testmodel.WebServerImpl'/>" +
                "</container>");
        PicoContainer pico = buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()), null);
        assertNotNull(pico.getComponentInstance(WebServerConfigComp.class));
        WebServerConfigComp config = (WebServerConfigComp) pico.getComponentInstanceOfType(WebServerConfigComp.class);
        assertEquals("localhost", config.getHost());
        assertEquals(8080, config.getPort());
    }

    // This is of little value given that nested adapters can't be specified in XML.
    public void testComponentAdapterClassCanBeSpecifiedInContainerElement() throws IOException, ParserConfigurationException, SAXException {
        Reader script = new StringReader("" +
                "<container componentadapterfactory='" + ConstructorInjectionComponentAdapterFactory.class.getName() + "'>" +
                "  <component class='org.nanocontainer.testmodel.DefaultWebServerConfig'/>" +
                "</container>");

        PicoContainer pico = buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()), null);
        Object wsc1 = pico.getComponentInstanceOfType(WebServerConfig.class);
        Object wsc2 = pico.getComponentInstanceOfType(WebServerConfig.class);

        assertNotSame(wsc1, wsc2);
    }
}
