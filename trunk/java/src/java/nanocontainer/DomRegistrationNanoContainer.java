/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/

package nanocontainer;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import picocontainer.Container;
import picocontainer.DummyContainer;
import picocontainer.PicoRegistrationException;

public class DomRegistrationNanoContainer extends StringRegistrationNanoContainerImpl
        implements InputSourceRegistrationNanoContainer{

    private final DocumentBuilder documentBuilder;

    public DomRegistrationNanoContainer(DocumentBuilder documentBuilder, Container parentContainer) {
        super(parentContainer);
        this.documentBuilder = documentBuilder;
    }

    public static class Default extends DomRegistrationNanoContainer {
        public Default() throws ParserConfigurationException {
            super(DocumentBuilderFactory.newInstance().newDocumentBuilder(), new DummyContainer());
        }
    }

    public static class WithParentContainer extends DomRegistrationNanoContainer {
        public WithParentContainer(Container parentContainer) throws ParserConfigurationException {
            super(DocumentBuilderFactory.newInstance().newDocumentBuilder(), parentContainer);
        }
    }

    public static class WithCustomDocumentBuilder extends DomRegistrationNanoContainer {
        public WithCustomDocumentBuilder(DocumentBuilder documentBuilder) {
            super(documentBuilder, new DummyContainer());
        }
    }

    public void registerComponents(InputSource registration) throws PicoRegistrationException, ClassNotFoundException {
        try {
            Document doc = documentBuilder.parse(registration);
            NodeList components = doc.getElementsByTagName("component");
            for (int i = 0; i < components.getLength(); i++) {
                NamedNodeMap attributes = components.item(i).getAttributes();

                Node type = attributes.getNamedItem("type");
                Node clazz = attributes.getNamedItem("class");
                if (type != null) {
                    registerComponent(type.getNodeValue(),clazz.getNodeValue());
                } else {
                    registerComponent(clazz.getNodeValue());
                }
            }
        } catch (SAXException e) {
            throw new NanoTextRegistrationException("SAXException:" + e.getMessage());
        } catch (IOException e) {
            throw new NanoTextRegistrationException("IOException:" + e.getMessage());
        }
    }
}
