/*****************************************************************************
 * Copyright (Cc) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/

package nanocontainer;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import picocontainer.PicoContainer;
import picocontainer.PicoRegistrationException;
import picocontainer.PicoIntrospectionException;
import picocontainer.defaults.NullContainer;
import nanocontainer.reflection.StringToObjectConverter;

public class DomRegistrationNanoContainer extends StringRegistrationNanoContainerImpl
        implements InputSourceRegistrationNanoContainer{

    private final DocumentBuilder documentBuilder;

    public DomRegistrationNanoContainer(DocumentBuilder documentBuilder, PicoContainer parentContainer, ClassLoader classLoader) {
        super(parentContainer, classLoader, new StringToObjectConverter());
        this.documentBuilder = documentBuilder;
    }

    public static class Default extends DomRegistrationNanoContainer {
        public Default() throws ParserConfigurationException {
            super(DocumentBuilderFactory.newInstance().newDocumentBuilder(), new NullContainer(), DomRegistrationNanoContainer.class.getClassLoader() );
        }
    }

    public static class WithParentContainer extends DomRegistrationNanoContainer {
        public WithParentContainer(PicoContainer parentContainer) throws ParserConfigurationException {
            super(DocumentBuilderFactory.newInstance().newDocumentBuilder(), parentContainer, DomRegistrationNanoContainer.class.getClassLoader());
        }
    }

    public static class WithCustomDocumentBuilder extends DomRegistrationNanoContainer {
        public WithCustomDocumentBuilder(DocumentBuilder documentBuilder) {
            super(documentBuilder, new NullContainer(), DomRegistrationNanoContainer.class.getClassLoader());
        }
    }

    public static class WithClassLoader extends DomRegistrationNanoContainer {
        public WithClassLoader(ClassLoader classLoader) throws ParserConfigurationException {
            super(DocumentBuilderFactory.newInstance().newDocumentBuilder(), new NullContainer(), classLoader);
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
                addParameters(clazz.getNodeValue(), components.item(i));
            }
        } catch (SAXException e) {
            throw new NanoTextRegistrationException("SAXException:" + e.getMessage());
        } catch (IOException e) {
            throw new NanoTextRegistrationException("IOException:" + e.getMessage());
        } catch (PicoIntrospectionException e) {

        }
    }

    private void addParameters(String className, Node node) throws ClassNotFoundException, PicoIntrospectionException {
        if (node instanceof Element) {
            Element element = (Element) node;
            NodeList paramElements = element.getElementsByTagName("param");
            for (int i = 0; i < paramElements.getLength(); i++) {
                Node paramNode = paramElements.item(i);
                String type = paramNode.getAttributes().getNamedItem("type").getNodeValue();
                String value = paramNode.getFirstChild().getNodeValue();
                addParameterToComponent(className, type, value);
            }
        }

    }
}
