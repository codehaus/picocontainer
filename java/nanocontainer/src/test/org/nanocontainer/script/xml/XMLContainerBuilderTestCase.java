/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Aslak Hellesoy and Paul Hammant                          *
 *****************************************************************************/

package org.picoextras.script.xml;

import org.picocontainer.PicoContainer;
import org.picocontainer.extras.ImplementationHidingComponentAdapterFactory;
import org.picoextras.integrationkit.PicoAssemblyException;
import org.picoextras.script.AbstractScriptedAssemblingLifecycleContainerBuilderTestCase;
import org.picoextras.testmodel.DefaultWebServerConfig;
import org.picoextras.testmodel.WebServer;
import org.picoextras.testmodel.WebServerConfig;
import org.picoextras.testmodel.WebServerConfigComp;
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
public class XMLContainerBuilderTestCase extends AbstractScriptedAssemblingLifecycleContainerBuilderTestCase {

    public void testCreateSimpleContainer() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, PicoAssemblyException {
        Reader script = new StringReader("<container>" +
                "    <component class='org.picoextras.testmodel.DefaultWebServerConfig'/>" +
                "    <component key='org.picoextras.testmodel.WebServer' class='org.picoextras.testmodel.WebServerImpl'/>" +
                "</container>");

        PicoContainer pico = buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()));
        assertEquals(2, pico.getComponentInstances().size());
        assertNotNull(pico.getComponentInstance(DefaultWebServerConfig.class));
    }

    public void testAPicocontainerCanHostItself() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, PicoAssemblyException {
        Reader script = new StringReader("<component class='org.picocontainer.defaults.DefaultPicoContainer'>" +
                "    <component class='org.picoextras.testmodel.DefaultWebServerConfig'/>" +
                "    <component key='child1' class='org.picocontainer.defaults.DefaultPicoContainer'>" +
                "        <component key='org.picoextras.testmodel.WebServer' class='org.picoextras.testmodel.WebServerImpl'/>" +
                "    </component>" +
                "</component>");

        PicoContainer pico = buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()));
        assertNotNull(pico.getComponentInstance(DefaultWebServerConfig.class));

        PicoContainer childContainer = (PicoContainer) pico.getComponentInstance("child1");
        assertNotNull(childContainer.getComponentInstance(WebServer.class.getName()));
    }

    public void testClassLoaderHierarchy() throws ParserConfigurationException, ClassNotFoundException, SAXException, IOException, PicoAssemblyException {

        String testcompJarFileName = System.getProperty("testcomp.jar");
        // Paul's path to TestComp. PLEASE do not take out.
        //testcompJarFileName = "D:/DEV/nano/reflection/src/test-comp/TestComp.jar";

        assertNotNull("The testcomp.jar system property should point to nano/reflection/src/test-comp/TestComp.jar", testcompJarFileName);
        File testCompJar = new File(testcompJarFileName);
        File testCompJar2 = new File(testCompJar.getParentFile(), "TestComp2.jar");
        assertTrue(testCompJar.isFile());
        assertTrue(testCompJar2.isFile());

        Reader script = new StringReader("<container>" +
                "    <classpath>" +
                "        <element file='" + testCompJar.getCanonicalPath() + "'/>" +
                "    </classpath>" +
                "    <component key='foo' class='TestComp'/>" +
                "    <component key='child1' class='org.picocontainer.defaults.DefaultPicoContainer'>" +
                "        <classpath>" +
                "            <element file='" + testCompJar2.getCanonicalPath() + "'/>" +
                "        </classpath>" +
                "        <component key='bar' class='TestComp2'/>" +
                "    </component>" +
                "</container>");

        PicoContainer pico = buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()));

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

    public void testUnknownComponentClassThrowsAssemblyException() throws Exception, SAXException, ParserConfigurationException, IOException {

        try {
            Reader script = new StringReader("<container>" +
                    "      <component class='Foo'/>" +
                    "</container>");
            buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()));
            fail("Should have thrown a ClassNotFoundException");
        } catch (RuntimeException cnfe) {
        }
    }

    public void testUnknownComponentClassThrowsEmptyCompositionException() throws Exception, SAXException, ParserConfigurationException, IOException {

        try {
            Reader script = new StringReader("<container/>");
            buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()));
        } catch (EmptyCompositionException cnfe) {
        }
    }

    public void testPseudoComponentCreation() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, PicoAssemblyException {
        Reader script = new StringReader("<container>" +
                "    <pseudocomponent factory='org.picoextras.script.xml.XMLContainerBuilderTestCase$TestFactory'>" +
                "      <config-or-whatever/>" +
                "    </pseudocomponent>" +
                "</container>");

        PicoContainer pico = buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()));
        assertNotNull(pico.getComponentInstances().get(0));
        assertTrue(pico.getComponentInstances().get(0) instanceof String);
        assertEquals("Hello", pico.getComponentInstances().get(0).toString());

    }

    public static class TestFactory implements zXMLPseudoComponentFactory {
        public Object makeInstance(Element elem) throws SAXException, ClassNotFoundException {
            return "Hello";
        }
    }

    public void testInstantiationOfComponentsWithParams() throws IOException, SAXException, ClassNotFoundException, ParserConfigurationException {
        Reader script = new StringReader("<container>" +
                "  <component class='org.picoextras.testmodel.WebServerConfigComp'>" +
                "    <parameter class='java.lang.String'>localhost</parameter>" +
                "    <parameter class='int'>8080</parameter>" +
                "  </component>" +
                "  <component key='org.picoextras.testmodel.WebServer' class='org.picoextras.testmodel.WebServerImpl'/>" +
                "</container>");
        PicoContainer pico = buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()));
        assertNotNull(pico.getComponentInstance(WebServerConfigComp.class));
        WebServerConfigComp config = (WebServerConfigComp) pico.getComponentInstanceOfType(WebServerConfigComp.class);
        assertEquals("localhost", config.getHost());
        assertEquals(8080, config.getPort());
    }

    public void testComponentAdapterClassCanBeSpecifiedInContainerElement() throws IOException, ParserConfigurationException, SAXException {
        Reader script = new StringReader("<container componentadapterfactory='" + ImplementationHidingComponentAdapterFactory.class.getName() + "'>" +
                "    <component class='org.picoextras.testmodel.DefaultWebServerConfig'/>" +
                "</container>");

        PicoContainer pico = buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()));
        Object wsc = pico.getComponentInstanceOfType(WebServerConfig.class);

        assertTrue(wsc instanceof WebServerConfig);
        assertFalse(wsc instanceof DefaultWebServerConfig);

    }
}
