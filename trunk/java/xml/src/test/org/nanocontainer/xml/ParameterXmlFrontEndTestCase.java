/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Jeppe Cramon                                             *
 *****************************************************************************/

package org.nanocontainer.xml;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.nanocontainer.testmodel.DefaultWebServerConfig;
import org.nanocontainer.testmodel.WebServerConfigComp;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Jeppe Cramon
 * @author Paul Hammant
 */
public class ParameterXmlFrontEndTestCase extends TestCase {
	
	private Element getRootElement(InputSource is) throws ParserConfigurationException, IOException, SAXException {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is).getDocumentElement();
	}	

    public void testSingleDefaultContainer() throws XmlFrontEndException, IOException, SAXException, ClassNotFoundException, ParserConfigurationException {
        InputSource inputSource = new InputSource(new StringReader(
                "<container>" +
                "  <component classname='org.nanocontainer.testmodel.DefaultWebServerConfig'/>" +
                "  <component key='org.nanocontainer.testmodel.WebServer' classname='org.nanocontainer.testmodel.WebServerImpl'/>" +
                "</container>"));
        ParameterXmlFrontEnd inputSourceContainerFactory = new ParameterXmlFrontEnd();
        PicoContainer picoContainer = inputSourceContainerFactory.createPicoContainer(getRootElement(inputSource));
        assertNotNull(picoContainer.getComponentInstance(DefaultWebServerConfig.class));
    }

    public void testContainerOverride() throws XmlFrontEndException, IOException, SAXException, ClassNotFoundException, ParserConfigurationException {
        OverriddenPicoContainer.used = false;
        InputSource inputSource = new InputSource(new StringReader(
                "<container classname='"+OverriddenPicoContainer.class.getName()+"'>" +
                "  <component classname='org.nanocontainer.testmodel.DefaultWebServerConfig'/>" +
                "  <component key='org.nanocontainer.testmodel.WebServer' classname='org.nanocontainer.testmodel.WebServerImpl'/>" +
                "</container>"));
        ParameterXmlFrontEnd inputSourceContainerFactory = new ParameterXmlFrontEnd();
        PicoContainer picoContainer = inputSourceContainerFactory.createPicoContainer(getRootElement(inputSource));
        assertNotNull(picoContainer.getComponentInstance(DefaultWebServerConfig.class));
        assertTrue("OverriddenPicoContainer should have been used", OverriddenPicoContainer.used);
    }

	public void testPicoInPico() throws XmlFrontEndException, IOException, SAXException, ClassNotFoundException, ParserConfigurationException {
		InputSource inputSource = new InputSource(new StringReader(
				"<container>" +
				"  <component classname='org.nanocontainer.testmodel.DefaultWebServerConfig'/>" +
				"  <container>" +
				"    <component key='org.nanocontainer.testmodel.WebServer' classname='org.nanocontainer.testmodel.WebServerImpl'/>" +
				"  </container>" +
				"</container>"));

		ParameterXmlFrontEnd inputSourceContainerFactory = new ParameterXmlFrontEnd();
		PicoContainer rootContainer = inputSourceContainerFactory.createPicoContainer(getRootElement(inputSource));
		assertNotNull(rootContainer.getComponentInstance(DefaultWebServerConfig.class));

		PicoContainer childContainer = (PicoContainer) rootContainer.getChildContainers().iterator().next();
		assertNotNull(childContainer.getComponentInstance("org.nanocontainer.testmodel.WebServer"));
	}

	public void testInstantiationOfComponentsWithParams() throws XmlFrontEndException, IOException, SAXException, ClassNotFoundException, ParserConfigurationException {
		InputSource inputSource = new InputSource(new StringReader(
				"<container>" +
				"  <component classname='org.nanocontainer.testmodel.WebServerConfigComp'>" +
				"    <parameter classname='String'>localhost</parameter>" +
				"    <parameter classname='int'>8080</parameter>" +
				"  </component>" +
				"  <component key='org.nanocontainer.testmodel.WebServer' classname='org.nanocontainer.testmodel.WebServerImpl'/>" +
				"</container>"));
		ParameterXmlFrontEnd inputSourceContainerFactory = new ParameterXmlFrontEnd();
		PicoContainer picoContainer = inputSourceContainerFactory.createPicoContainer(getRootElement(inputSource));
		assertNotNull(picoContainer.getComponentInstance(WebServerConfigComp.class));
		WebServerConfigComp config = (WebServerConfigComp) picoContainer.getComponentInstance(WebServerConfigComp.class);
		assertEquals("localhost", config.getHost());
		assertEquals(8080, config.getPort());
	}

	public void testInstantiationOfComponentsWithDefaultParamHandling() throws XmlFrontEndException, IOException, SAXException, ClassNotFoundException, ParserConfigurationException {
		InputSource inputSource = new InputSource(new StringReader(
				"<container>" +
				"  <component classname='org.nanocontainer.testmodel.WebServerConfigComp'>" +
				"    <parameter>localhost</parameter>" +
				"    <parameter classname='int'>8080</parameter>" +
				"  </component>" +
				"  <component key='org.nanocontainer.testmodel.WebServer' classname='org.nanocontainer.testmodel.WebServerImpl'/>" +
				"</container>"));
		ParameterXmlFrontEnd inputSourceContainerFactory = new ParameterXmlFrontEnd();
		PicoContainer picoContainer = inputSourceContainerFactory.createPicoContainer(getRootElement(inputSource));
		assertNotNull(picoContainer.getComponentInstance(WebServerConfigComp.class));
		WebServerConfigComp config = (WebServerConfigComp) picoContainer.getComponentInstance(WebServerConfigComp.class);
		assertEquals("localhost", config.getHost());
		assertEquals(8080, config.getPort());
	}

    public static class OverriddenPicoContainer extends DefaultPicoContainer {
        public static boolean used;
        public OverriddenPicoContainer() {
            used = true;
        }
    }
}
