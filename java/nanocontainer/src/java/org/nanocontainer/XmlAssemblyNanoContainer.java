/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer;

import org.nanocontainer.xml.DefaultXmlFrontEnd;
import org.nanocontainer.xml.EmptyXmlConfigurationException;
import org.nanocontainer.xml.XmlFrontEnd;
import org.picocontainer.PicoConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @author Ward Cunningham
 * @version $Revision$
 */
public class XmlAssemblyNanoContainer extends NanoContainer {

    private DocumentBuilder documentBuilder;

    public XmlAssemblyNanoContainer(Reader nanoContainerXml)
            throws ParserConfigurationException, ClassNotFoundException, IOException, PicoConfigurationException {
        this(DocumentBuilderFactory.newInstance().newDocumentBuilder(), nanoContainerXml, new ConsoleNanoContainerMonitor());
    }

    public XmlAssemblyNanoContainer(Reader nanoContainerConfig, NanoContainerMonitor monitor)
            throws ParserConfigurationException, ClassNotFoundException, IOException, PicoConfigurationException {
        this(DocumentBuilderFactory.newInstance().newDocumentBuilder(), nanoContainerConfig, monitor);
    }

    public XmlAssemblyNanoContainer(DocumentBuilder documentBuilder, Reader nanoContainerConfig, NanoContainerMonitor monitor)
            throws ClassNotFoundException, IOException, PicoConfigurationException {
        super(monitor);
        this.documentBuilder = documentBuilder;
        configure(nanoContainerConfig);
    }

    protected Element getRootElement(InputSource inputSource) throws SAXException, IOException {
        Document document = documentBuilder.parse(inputSource);
        return document.getDocumentElement();
    }

    protected void configure(Reader nanoContainerXml)
            throws IOException, ClassNotFoundException, PicoConfigurationException, SAXConfigurationException {
        final InputSource is = new InputSource(nanoContainerXml);
        try {
            Element rootElement = getRootElement(is);
            String xmlFrontEndClassName = rootElement.getAttribute("xmlfrontend");
            XmlFrontEnd xmlFrontEnd = null;
            if (xmlFrontEndClassName != null && !xmlFrontEndClassName.equals("")) {
                try {
                    xmlFrontEnd = (XmlFrontEnd) this.getClass().getClassLoader().loadClass(xmlFrontEndClassName).newInstance();
                } catch (InstantiationException e) {
                    throw new ClassNotFoundException("InstantiationException in XmlAssemblyNanoContainer - " + e.getMessage());
                } catch (IllegalAccessException e) {
                    throw new ClassNotFoundException("IllegalAccessException in XmlAssemblyNanoContainer - " + e.getMessage());
                }
            } else {
                xmlFrontEnd = new DefaultXmlFrontEnd();
            }
            rootContainer = xmlFrontEnd.createPicoContainer(rootElement);
            instantiateComponentsBreadthFirst(rootContainer);
            startComponentsBreadthFirst();
        } catch (SAXException e) {
            throw new SAXConfigurationException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        String nanoContainerXml = args[0];
        if (nanoContainerXml == null) {
            nanoContainerXml = "config/nanocontainer.xml";
        }
        NanoContainer nano = new XmlAssemblyNanoContainer(new FileReader(nanoContainerXml));
        addShutdownHook(nano);
    }
}
