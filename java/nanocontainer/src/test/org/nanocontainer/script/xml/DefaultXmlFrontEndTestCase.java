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
import org.picocontainer.PicoContainer;
import org.picoextras.script.PicoCompositionException;
import org.picoextras.testmodel.DefaultWebServerConfig;
import org.picoextras.testmodel.WebServer;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class DefaultXmlFrontEndTestCase extends TestCase {

    private Element getRootElement(InputSource is) throws ParserConfigurationException, IOException, SAXException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is).getDocumentElement();
    }

    public void testCreateSimpleContainer() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, PicoCompositionException {
        InputSource inputSource = new InputSource(new StringReader("<container>" +
                "    <component class='org.picoextras.testmodel.DefaultWebServerConfig'/>" +
                "    <component key='org.picoextras.testmodel.WebServer' class='org.picoextras.testmodel.WebServerImpl'/>" +
                "</container>"));

        XmlFrontEnd inputSourceContainerFactory = new DefaultXmlFrontEnd();
        PicoContainer picoContainer = inputSourceContainerFactory.createPicoContainer(getRootElement(inputSource));
        assertNotNull(picoContainer.getComponentInstance(DefaultWebServerConfig.class));
    }

    public void testPicoInPico() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, PicoCompositionException {
        InputSource inputSource = new InputSource(new StringReader("<container>" +
                "    <component class='org.picoextras.testmodel.DefaultWebServerConfig'/>" +
                "    <container>" +
                "        <component key='org.picoextras.testmodel.WebServer' class='org.picoextras.testmodel.WebServerImpl'/>" +
                "    </container>" +
                "</container>"));

        XmlFrontEnd inputSourceFrontEnd = new DefaultXmlFrontEnd();
        PicoContainer rootContainer = inputSourceFrontEnd.createPicoContainer(getRootElement(inputSource));
        assertNotNull(rootContainer.getComponentInstance(DefaultWebServerConfig.class));

        PicoContainer childContainer = (PicoContainer) rootContainer.getChildContainers().iterator().next();
        assertNotNull(childContainer.getComponentInstance(WebServer.class));
    }

    private URL findResource(String resourcePath) {
        URL resource = getClass().getResource("/" + resourcePath);
        assertNotNull("add " + resourcePath + " to the class-path", resource);
        return resource;
    }

    public void testClassLoaderHierarchy() throws ParserConfigurationException, ClassNotFoundException, SAXException, IOException, PicoCompositionException {

        URL testCompJar = findResource("TestComp.jar");
        URL testCompJar2 = findResource("TestComp2.jar");

        InputSource inputSource = new InputSource(new StringReader("<container>" +
                "    <classpath>" +
                "        <element url='" + testCompJar.toExternalForm() + "'/>" +
                "    </classpath>" +
                "    <component key='foo' class='TestComp'/>" +
                "    <container>" +
                "        <classpath>" +
                "            <element url='" + testCompJar2.toExternalForm() + "'/>" +
                "        </classpath>" +
                "        <component key='bar' class='TestComp2'/>" +
                "    </container>" +
                "</container>"));

        XmlFrontEnd inputSourceFrontEnd = new DefaultXmlFrontEnd();
        PicoContainer rootContainer = inputSourceFrontEnd.createPicoContainer(getRootElement(inputSource));

        Object fooTestComp = rootContainer.getComponentInstance("foo");
        assertNotNull("Container should have a 'foo' component", fooTestComp);

        PicoContainer childContainer = (PicoContainer) rootContainer.getChildContainers().iterator().next();
        Object barTestComp = childContainer.getComponentInstance("bar");
        assertNotNull("Container should have a 'bar' component", barTestComp);

        assertEquals("foo classloader should be parent of bar",
                fooTestComp.getClass().getClassLoader(), barTestComp.getClass().getClassLoader().getParent());

    }

    public void testInstantiateXmlWithMissingComponent() throws Exception, SAXException, ParserConfigurationException, IOException {

        try {
            InputSource inputSource = new InputSource(new StringReader("<container>" +
                    "      <component class='Foo'/>" +
                    "</container>"));
            PicoContainer rootContainer = new DefaultXmlFrontEnd().createPicoContainer(getRootElement(inputSource));
            fail("Should have thrown a ClassNotFoundException");
        } catch (ClassNotFoundException cnfe) {
        }
    }

    public void testInstantiateEmptyXml() throws Exception, SAXException, ParserConfigurationException, IOException {

        try {
            InputSource inputSource = new InputSource(new StringReader("<container>" +
                    "</container>"));
            PicoContainer rootContainer = new DefaultXmlFrontEnd().createPicoContainer(getRootElement(inputSource));
            fail("Should have thrown a EmptyCompositionException");
        } catch (EmptyCompositionException cnfe) {
        }
    }

    public void testPseudoComponentCreation() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, PicoCompositionException {
        InputSource inputSource = new InputSource(new StringReader("<container>" +
                "    <pseudocomponent factory='org.picoextras.script.xml.DefaultXmlFrontEndTestCase$TestFactory'>" +
                "      <config-or-whatever/>" +
                "    </pseudocomponent>" +
                "</container>"));

        XmlFrontEnd inputSourceContainerFactory = new DefaultXmlFrontEnd();
        PicoContainer picoContainer = inputSourceContainerFactory.createPicoContainer(getRootElement(inputSource));
        assertNotNull(picoContainer.getComponentInstances().get(0));
        assertTrue(picoContainer.getComponentInstances().get(0) instanceof String);
        assertEquals("Hello", picoContainer.getComponentInstances().get(0).toString());

    }

    public static class TestFactory implements XmlPseudoComponentFactory {
        public Object makeInstance(Element elem) throws SAXException, ClassNotFoundException {
            return "Hello";
        }
    }

}
