/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Aslak Hellesoy and Paul Hammant                          *
 *****************************************************************************/

package org.nanocontainer.xml;

import junit.framework.TestCase;
import org.nanocontainer.testmodel.DefaultWebServerConfig;
import org.picocontainer.PicoContainer;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class DefaultXmlFrontEndTestCase extends TestCase {

    private Element getRootElement(InputSource is) throws ParserConfigurationException, IOException, SAXException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is).getDocumentElement();
    }

    public void testCreateSimpleContainer() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, EmptyXmlConfigurationException {
        InputSource inputSource = new InputSource(new StringReader(
                "<container>" +
                "    <component classname='org.nanocontainer.testmodel.DefaultWebServerConfig'/>" +
                "    <component key='org.nanocontainer.testmodel.WebServer' classname='org.nanocontainer.testmodel.WebServerImpl'/>" +
                "</container>"));

        XmlFrontEnd inputSourceContainerFactory = new DefaultXmlFrontEnd();
        PicoContainer picoContainer = inputSourceContainerFactory.createPicoContainer(getRootElement(inputSource));
        assertNotNull(picoContainer.getComponentInstance(DefaultWebServerConfig.class));
    }

    public void testPicoInPico() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, EmptyXmlConfigurationException {
        InputSource inputSource = new InputSource(new StringReader(
                "<container>" +
                "    <component classname='org.nanocontainer.testmodel.DefaultWebServerConfig'/>" +
                "    <container>" +
                "        <component key='org.nanocontainer.testmodel.WebServer' classname='org.nanocontainer.testmodel.WebServerImpl'/>" +
                "    </container>" +
                "</container>"));

        XmlFrontEnd inputSourceFrontEnd = new DefaultXmlFrontEnd();
        PicoContainer rootContainer = inputSourceFrontEnd.createPicoContainer(getRootElement(inputSource));
        assertNotNull(rootContainer.getComponentInstance(DefaultWebServerConfig.class));

        PicoContainer childContainer = (PicoContainer) rootContainer.getChildContainers().iterator().next();
        assertNotNull(childContainer.getComponentInstance("org.nanocontainer.testmodel.WebServer"));
    }

    public void testClassLoaderHierarchy() throws ParserConfigurationException, ClassNotFoundException, SAXException, IOException, EmptyXmlConfigurationException {

        String testcompJarFileName = System.getProperty("testcomp.jar");
        // Paul's path to TestComp. PLEASE do not take out.
        //testcompJarFileName = "D:\\DEV\\nano\\reflection\\src\\test-comp\\TestComp.jar";

        assertNotNull("The testcomp.jar system property should point to nano/reflection/src/test-comp/TestComp.jar", testcompJarFileName);
        File testCompJar = new File(testcompJarFileName);
        assertTrue(testCompJar.isFile());

        InputSource inputSource = new InputSource(new StringReader(
                "<container>" +
                "    <classpath>" +
                "        <element file='" + testCompJar.getAbsolutePath() + "'/>" +
                "    </classpath>" +
                "    <component key='foo' classname='TestComp'/>" +
                "    <container>" +
                "        <component key='bar' classname='TestComp'/>" +
                "    </container>" +
                "</container>"));

        XmlFrontEnd inputSourceFrontEnd = new DefaultXmlFrontEnd();
        PicoContainer rootContainer = inputSourceFrontEnd.createPicoContainer(getRootElement(inputSource));
        Object fooTestComp = rootContainer.getComponentInstance("foo");
        assertNotNull(fooTestComp);

        PicoContainer childContainer = (PicoContainer) rootContainer.getChildContainers().iterator().next();
        Object barTestComp = childContainer.getComponentInstance("bar");
        assertNotNull(barTestComp);

        assertNotSame(fooTestComp, barTestComp);
    }

    public void testInstantiateXmlWithMissingComponent() throws Exception, SAXException, ParserConfigurationException, IOException {

        try {
            InputSource inputSource = new InputSource(new StringReader(
                    "<container>" +
                    "      <component classname='Foo'/>" +
                    "</container>"));
            PicoContainer rootContainer = new DefaultXmlFrontEnd().createPicoContainer(getRootElement(inputSource));
            fail("Should have thrown a ClassNotFoundException");
        } catch (ClassNotFoundException cnfe) {
        }

    }

    public void testInstantiateEmptyXml() throws Exception, SAXException, ParserConfigurationException, IOException {

        try {
            InputSource inputSource = new InputSource(new StringReader(
                    "<container>" +
                    "</container>"));
            PicoContainer rootContainer = new DefaultXmlFrontEnd().createPicoContainer(getRootElement(inputSource));
            fail("Should have thrown a EmptyXmlConfigurationException");
        } catch (EmptyXmlConfigurationException cnfe) {
        }
    }
}
