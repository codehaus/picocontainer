/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer;

import org.picoextras.script.xml.DefaultXmlFrontEnd;
import org.picoextras.script.xml.XmlFrontEnd;
import org.picocontainer.PicoCompositionException;
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
public class XmlCompositionNanoContainer extends NanoContainer {

    private DocumentBuilder documentBuilder;

    public XmlCompositionNanoContainer(Reader nanoContainerXml)
            throws ParserConfigurationException, ClassNotFoundException, IOException, PicoCompositionException {
        this(DocumentBuilderFactory.newInstance().newDocumentBuilder(), nanoContainerXml, new ConsoleNanoContainerMonitor());
    }

    public XmlCompositionNanoContainer(Reader nanoContainerConfig, NanoContainerMonitor monitor)
            throws ParserConfigurationException, ClassNotFoundException, IOException, PicoCompositionException {
        this(DocumentBuilderFactory.newInstance().newDocumentBuilder(), nanoContainerConfig, monitor);
    }

    public XmlCompositionNanoContainer(DocumentBuilder documentBuilder, Reader composition, NanoContainerMonitor monitor)
            throws ClassNotFoundException, IOException, PicoCompositionException {
        super(monitor);
        this.documentBuilder = documentBuilder;
        compose(composition);
    }

    protected Element getRootElement(InputSource inputSource) throws SAXException, IOException {
        Document document = documentBuilder.parse(inputSource);
        return document.getDocumentElement();
    }

    protected void compose(Reader nanoContainerXml)
            throws IOException, ClassNotFoundException, PicoCompositionException, SAXCompositionException {
        final InputSource is = new InputSource(nanoContainerXml);
        try {
            Element rootElement = getRootElement(is);
            String xmlFrontEndClassName = rootElement.getAttribute("xmlfrontend");
            XmlFrontEnd xmlFrontEnd = null;
            if (xmlFrontEndClassName != null && !xmlFrontEndClassName.equals("")) {
                try {
                    xmlFrontEnd = (XmlFrontEnd) this.getClass().getClassLoader().loadClass(xmlFrontEndClassName).newInstance();
                } catch (InstantiationException e) {
                    throw new ClassNotFoundException("InstantiationException in XmlCompositionNanoContainer - " + e.getMessage());
                } catch (IllegalAccessException e) {
                    throw new ClassNotFoundException("IllegalAccessException in XmlCompositionNanoContainer - " + e.getMessage());
                }
            } else {
                xmlFrontEnd = new DefaultXmlFrontEnd();
            }
            rootContainer = xmlFrontEnd.createPicoContainer(rootElement);
            instantiateComponentsBreadthFirst(rootContainer);
            startComponentsBreadthFirst();
        } catch (SAXException e) {
            throw new SAXCompositionException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        String nanoContainerXml = args[0];
        if (nanoContainerXml == null) {
            nanoContainerXml = "config/nanocontainer.xml";
        }
        NanoContainer nano = new XmlCompositionNanoContainer(new FileReader(nanoContainerXml));
        addShutdownHook(nano);
    }
}
