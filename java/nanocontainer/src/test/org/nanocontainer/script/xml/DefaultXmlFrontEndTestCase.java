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

import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picoextras.integrationkit.ContainerAssembler;
import org.picoextras.integrationkit.PicoAssemblyException;
import org.picoextras.testmodel.DefaultWebServerConfig;
import org.picoextras.testmodel.WebServer;
import org.picoextras.testmodel.WebServerConfigComp;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

/**
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Jeppe Cramon
 * @version $Revision$
 */
public class DefaultXmlFrontEndTestCase extends TestCase {

    private Element getRootElement(InputSource is) throws ParserConfigurationException, IOException, SAXException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is).getDocumentElement();
    }

    public void testCreateSimpleContainer() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, PicoAssemblyException {
        InputSource inputSource = new InputSource(new StringReader(
                "<container>" +
                "    <component class='org.picoextras.testmodel.DefaultWebServerConfig'/>" +
                "    <component key='org.picoextras.testmodel.WebServer' class='org.picoextras.testmodel.WebServerImpl'/>" +
                "</container>"));

        MutablePicoContainer pico = createPicoContainer(inputSource);
        assertEquals(2, pico.getComponentInstances().size());
        assertNotNull(pico.getComponentInstance(DefaultWebServerConfig.class));
    }

    private MutablePicoContainer createPicoContainer(InputSource inputSource) throws ParserConfigurationException, IOException, SAXException {
        ContainerAssembler assembler = new DefaultXmlFrontEnd(getRootElement(inputSource));
        MutablePicoContainer pico = new DefaultPicoContainer();
        assembler.assembleContainer(pico, null);
        return pico;
    }

    public void testPicoInPico() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, PicoAssemblyException {
        InputSource inputSource = new InputSource(new StringReader(
                "<component class='org.picocontainer.defaults.DefaultPicoContainer'>" +
                "    <component class='org.picoextras.testmodel.DefaultWebServerConfig'/>" +
                "    <component key='child1' class='org.picocontainer.defaults.DefaultPicoContainer'>" +
                "        <component key='org.picoextras.testmodel.WebServer' class='org.picoextras.testmodel.WebServerImpl'/>" +
                "    </component>" +
                "</component>"));

        MutablePicoContainer pico = createPicoContainer(inputSource);
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

        InputSource inputSource = new InputSource(new StringReader(
                "<container>" +
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
                "</container>"));

        MutablePicoContainer pico = createPicoContainer(inputSource);

        Object fooTestComp = pico.getComponentInstance("foo");
        assertNotNull("Container should have a 'foo' component", fooTestComp);

        PicoContainer childContainer = (PicoContainer) pico.getComponentInstance("child1");
        Object barTestComp = childContainer.getComponentInstance("bar");
        assertNotNull("Container should have a 'bar' component", barTestComp);

        assertEquals("foo classloader should be parent of bar", fooTestComp.getClass().getClassLoader(),
                barTestComp.getClass().getClassLoader().getParent());

    }

    public void testUnknownComponentClassThrowsAssemblyException() throws Exception, SAXException, ParserConfigurationException, IOException {

        try {
            InputSource inputSource = new InputSource(new StringReader(
                    "<container>" +
                    "      <component class='Foo'/>" +
                    "</container>"));
            createPicoContainer(inputSource);
            fail("Should have thrown a ClassNotFoundException");
        } catch (RuntimeException cnfe) {
        }
    }

    public void testUnknownComponentClassThrowsEmptyCompositionException() throws Exception, SAXException, ParserConfigurationException, IOException {

        try {
            InputSource inputSource = new InputSource(new StringReader(
                    "<container>" +
                    "</container>"));
            createPicoContainer(inputSource);
        } catch (EmptyCompositionException cnfe) {
        }
    }

    public void testPseudoComponentCreation() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, PicoAssemblyException {
        InputSource inputSource = new InputSource(new StringReader(
                "<container>" +
                "    <pseudocomponent factory='org.picoextras.script.xml.DefaultXmlFrontEndTestCase$TestFactory'>" +
                "      <config-or-whatever/>" +
                "    </pseudocomponent>" +
                "</container>"));

        MutablePicoContainer pico = createPicoContainer(inputSource);
        assertNotNull(pico.getComponentInstances().get(0));
        assertTrue(pico.getComponentInstances().get(0) instanceof String);
        assertEquals("Hello", pico.getComponentInstances().get(0).toString());

    }

    public static class TestFactory implements XmlPseudoComponentFactory {
        public Object makeInstance(Element elem) throws SAXException, ClassNotFoundException {
            return "Hello";
        }
    }

    public void testInstantiationOfComponentsWithParams() throws XmlFrontEndException, IOException, SAXException, ClassNotFoundException, ParserConfigurationException {
        InputSource inputSource = new InputSource(new StringReader(
                "<container>" +
                "  <component class='org.picoextras.testmodel.WebServerConfigComp'>" +
                "    <parameter class='java.lang.String'>localhost</parameter>" +
                "    <parameter class='int'>8080</parameter>" +
                "  </component>" +
                "  <component key='org.picoextras.testmodel.WebServer' class='org.picoextras.testmodel.WebServerImpl'/>" +
                "</container>"));
        MutablePicoContainer pico = createPicoContainer(inputSource);
        assertNotNull(pico.getComponentInstance(WebServerConfigComp.class));
        WebServerConfigComp config = (WebServerConfigComp) pico.getComponentInstance(WebServerConfigComp.class);
        assertEquals("localhost", config.getHost());
        assertEquals(8080, config.getPort());
    }
}
