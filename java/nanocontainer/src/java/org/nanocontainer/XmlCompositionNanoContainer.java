/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer;

import org.picoextras.script.xml.XMLContainerBuilder;

/**
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @author Ward Cunningham
 * @version $Revision$
 * @deprecated To replace with a NanoContainer instance that is given a
 * {@link XMLContainerBuilder} instance in its ctor.
 */
public class XmlCompositionNanoContainer /*extends NanoContainer*/ {

//    private final DocumentBuilder documentBuilder;
//    private Reader composition;
//
//    public XmlCompositionNanoContainer(Reader composition)
//            throws ParserConfigurationException, PicoAssemblyException {
//        this(DocumentBuilderFactory.newInstance().newDocumentBuilder(), composition, new ConsoleNanoContainerMonitor());
//    }
//
//    public XmlCompositionNanoContainer(Reader composition, NanoContainerMonitor monitor)
//            throws ParserConfigurationException, PicoAssemblyException {
//        this(DocumentBuilderFactory.newInstance().newDocumentBuilder(), composition, monitor);
//    }
//
//    public XmlCompositionNanoContainer(DocumentBuilder documentBuilder, Reader composition, NanoContainerMonitor monitor)
//            throws PicoAssemblyException {
//        super(monitor);
//        this.documentBuilder = documentBuilder;
//        this.composition = composition;
//        init();
//    }
//
//    private Element getRootElement(InputSource inputSource) throws SAXException, IOException {
//        Document document = documentBuilder.parse(inputSource);
//        return document.getDocumentElement();
//    }
//
//    protected PicoContainer createPicoContainer()
//            throws PicoAssemblyException {
//        final InputSource is = new InputSource(composition);
//        try {
//            Element rootElement = getRootElement(is);
//            String xmlFrontEndClassName = rootElement.getAttribute("xmlfrontend");
//            XmlFrontEnd xmlFrontEnd = null;
//            if (xmlFrontEndClassName != null && !xmlFrontEndClassName.equals("")) {
//                xmlFrontEnd = createXmlFrontEnd(xmlFrontEndClassName);
//            } else {
//                xmlFrontEnd = new XMLContainerBuilder();
//            }
//            return xmlFrontEnd.createPicoContainer(rootElement);
//        } catch (SAXException e) {
//            throw new PicoAssemblyException(e);
//        } catch (ClassNotFoundException e) {
//            throw new PicoAssemblyException(e);
//        } catch (IOException e) {
//            throw new PicoAssemblyException(e);
//        }
//    }
//
//    private XmlFrontEnd createXmlFrontEnd(String xmlFrontEndClassName) throws ClassNotFoundException {
//        XmlFrontEnd xmlFrontEnd;
//        try {
//            xmlFrontEnd = (XmlFrontEnd) this.getClass().getClassLoader().loadClass(xmlFrontEndClassName).newInstance();
//        } catch (InstantiationException e) {
//            throw new ClassNotFoundException("InstantiationException in XmlCompositionNanoContainer - " + e.getMessage());
//        } catch (IllegalAccessException e) {
//            throw new ClassNotFoundException("IllegalAccessException in XmlCompositionNanoContainer - " + e.getMessage());
//        }
//        return xmlFrontEnd;
//    }
//
//    public static void main(String[] args) throws Exception {
//        String nanoContainerXml = args[0];
//        if (nanoContainerXml == null) {
//            nanoContainerXml = "config/nanocontainer.xml";
//        }
//        NanoContainer nano = new XmlCompositionNanoContainer(new FileReader(nanoContainerXml));
//        nano.addShutdownHook();
//    }
}
