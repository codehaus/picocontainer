/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/

package org.nanocontainer.xml;

import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.PicoException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.nanocontainer.StringRegistrationNanoContainer;
import org.nanocontainer.DefaultStringRegistrationNanoContainer;
import org.nanocontainer.NanoTextRegistrationException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;

public class DomRegistrationNanoContainer implements InputSourceRegistrationNanoContainer, Serializable {

    private final DocumentBuilder documentBuilder;
    private final StringRegistrationNanoContainer stringRegistrationNanoContainer;

    public DomRegistrationNanoContainer(DocumentBuilder documentBuilder, StringRegistrationNanoContainer stringRegistrationNanoContainer) {
        this.documentBuilder = documentBuilder;
        this.stringRegistrationNanoContainer = stringRegistrationNanoContainer;
    }

    public static class Default extends DomRegistrationNanoContainer {
        public Default() throws ParserConfigurationException {
            super(
                    DocumentBuilderFactory.newInstance().newDocumentBuilder(),
                    new DefaultStringRegistrationNanoContainer()
            );
        }
    }

    public static class WithDocumentBuilder extends DomRegistrationNanoContainer {
        public WithDocumentBuilder(DocumentBuilder documentBuilder) {
            super(
                    documentBuilder,
                    new DefaultStringRegistrationNanoContainer()
            );
        }
    }

    public static class WithDelegate extends DomRegistrationNanoContainer {
        public WithDelegate(StringRegistrationNanoContainer delegate) throws ParserConfigurationException {
            super(
                    DocumentBuilderFactory.newInstance().newDocumentBuilder(),
                    delegate
            );
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
                type = type != null ? type : clazz;

                NodeList paramElements = ((Element)components.item(i)).getElementsByTagName("param");
                String[] parameterTypes = new String[paramElements.getLength()];
                String[] parameterValues = new String[paramElements.getLength()];
                for (int p = 0; p < paramElements.getLength(); p++) {
                    Node paramNode = paramElements.item(p);
                    parameterTypes[p] = paramNode.getAttributes().getNamedItem("type").getNodeValue();
                    parameterValues[p] = paramNode.getFirstChild().getNodeValue();
                }

                if (parameterValues.length == 0) {
                    stringRegistrationNanoContainer.registerComponent(type.getNodeValue(), clazz.getNodeValue());
                } else {
                    stringRegistrationNanoContainer.registerComponent(type.getNodeValue(), clazz.getNodeValue(), parameterTypes, parameterValues);
                }
            }
        } catch (SAXException e) {
            throw new NanoTextRegistrationException("SAXException:" + e.getMessage());
        } catch (IOException e) {
            throw new NanoTextRegistrationException("IOException:" + e.getMessage());
        } catch (PicoIntrospectionException e) {
            throw new NanoTextRegistrationException("PicoIntrospectionException:" + e.getMessage());
        } catch (PicoRegistrationException e) {
            throw new NanoTextRegistrationException("PicoRegistrationException:" + e.getMessage());
        }
    }

    public boolean hasComponent(Object componentKey) {
        return stringRegistrationNanoContainer.hasComponent(componentKey);
    }

    public Object getComponentInstance(Object componentKey) throws PicoException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        return stringRegistrationNanoContainer.getComponentInstance(componentKey);
    }

    public Collection getComponentInstances() throws PicoException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        return stringRegistrationNanoContainer.getComponentInstances();
    }

    public Collection getComponentKeys() {
        return stringRegistrationNanoContainer.getComponentKeys();
    }

    public Object getComponentMulticaster() throws PicoException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        return stringRegistrationNanoContainer.getComponentMulticaster();
    }

    public Object getComponentMulticaster(boolean callInInstantiationOrder, boolean callUnmanagedComponents) throws PicoException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        return stringRegistrationNanoContainer.getComponentMulticaster();
    }
}
