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
import org.picoextras.script.PicoCompositionException;
import org.picocontainer.PicoContainer;
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

    private final DocumentBuilder documentBuilder;
    private Reader composition;

    public XmlCompositionNanoContainer(Reader composition)
            throws ParserConfigurationException, PicoCompositionException {
        this(DocumentBuilderFactory.newInstance().newDocumentBuilder(), composition, new ConsoleNanoContainerMonitor());
    }

    public XmlCompositionNanoContainer(Reader composition, NanoContainerMonitor monitor)
            throws ParserConfigurationException, PicoCompositionException {
        this(DocumentBuilderFactory.newInstance().newDocumentBuilder(), composition, monitor);
    }

    public XmlCompositionNanoContainer(DocumentBuilder documentBuilder, Reader composition, NanoContainerMonitor monitor)
            throws PicoCompositionException {
        super(monitor);
        this.documentBuilder = documentBuilder;
        this.composition = composition;
        init();
    }

    private Element getRootElement(InputSource inputSource) throws SAXException, IOException {
        Document document = documentBuilder.parse(inputSource);
        return document.getDocumentElement();
    }

    protected PicoContainer createPicoContainer()
            throws PicoCompositionException {
        final InputSource is = new InputSource(composition);
        try {
            Element rootElement = getRootElement(is);
            String xmlFrontEndClassName = rootElement.getAttribute("xmlfrontend");
            XmlFrontEnd xmlFrontEnd = null;
            if (xmlFrontEndClassName != null && !xmlFrontEndClassName.equals("")) {
                xmlFrontEnd = createXmlFrontEnd(xmlFrontEndClassName);
            } else {
                xmlFrontEnd = new DefaultXmlFrontEnd();
            }
            return xmlFrontEnd.createPicoContainer(rootElement);
        } catch (SAXException e) {
            throw new PicoCompositionException(e);
        } catch (ClassNotFoundException e) {
            throw new PicoCompositionException(e);
        } catch (IOException e) {
            throw new PicoCompositionException(e);
        }
    }

    private XmlFrontEnd createXmlFrontEnd(String xmlFrontEndClassName) throws ClassNotFoundException {
        XmlFrontEnd xmlFrontEnd;
        try {
            xmlFrontEnd = (XmlFrontEnd) this.getClass().getClassLoader().loadClass(xmlFrontEndClassName).newInstance();
        } catch (InstantiationException e) {
            throw new ClassNotFoundException("InstantiationException in XmlCompositionNanoContainer - " + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new ClassNotFoundException("IllegalAccessException in XmlCompositionNanoContainer - " + e.getMessage());
        }
        return xmlFrontEnd;
    }

    public static void main(String[] args) throws Exception {
        String nanoContainerXml = args[0];
        if (nanoContainerXml == null) {
            nanoContainerXml = "config/nanocontainer.xml";
        }
        NanoContainer nano = new XmlCompositionNanoContainer(new FileReader(nanoContainerXml));
        nano.addShutdownHook();
    }
}
