/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/

package org.nanocontainer;

import org.nanocontainer.reflection.StringToObjectConverter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.PicoContainer;
import org.picocontainer.internals.ComponentRegistry;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.defaults.DefaultComponentRegistry;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Set;
import java.util.List;
import java.util.Collection;

public class DomRegistrationNanoContainer  implements InputSourceRegistrationNanoContainer, Serializable
{

    private StringRegistrationNanoContainerImpl stringRegistrationNanoContainer;
    private final DocumentBuilder documentBuilder;

    public DomRegistrationNanoContainer(DocumentBuilder documentBuilder, ClassLoader classLoader,
                                        ComponentRegistry componentRegistry)
    {
        stringRegistrationNanoContainer = new StringRegistrationNanoContainerImpl(classLoader, new StringToObjectConverter(), componentRegistry);
        this.documentBuilder = documentBuilder;
    }

    public static class Default extends DomRegistrationNanoContainer
    {
        public Default() throws ParserConfigurationException
        {
            super(DocumentBuilderFactory.newInstance().newDocumentBuilder(),
                DomRegistrationNanoContainer.class.getClassLoader(),
                new DefaultComponentRegistry());
        }
    }

    public static class WithCustomDocumentBuilder extends DomRegistrationNanoContainer
    {
        public WithCustomDocumentBuilder(DocumentBuilder documentBuilder)
        {
            super(documentBuilder, DomRegistrationNanoContainer.class.getClassLoader(), new DefaultComponentRegistry());
        }
    }

    public static class WithClassLoader extends DomRegistrationNanoContainer
    {
        public WithClassLoader(ClassLoader classLoader) throws ParserConfigurationException
        {
            super(DocumentBuilderFactory.newInstance().newDocumentBuilder(), classLoader,
                new DefaultComponentRegistry());
        }
    }

    public static class WithComponentRegistry extends DomRegistrationNanoContainer
    {
        public WithComponentRegistry(ComponentRegistry componentRegistry) throws ParserConfigurationException
        {
            super(DocumentBuilderFactory.newInstance().newDocumentBuilder(),
                DomRegistrationNanoContainer.class.getClassLoader(),
                componentRegistry);
        }
    }

    public void registerComponents(InputSource registration) throws PicoRegistrationException, ClassNotFoundException
    {
        try
        {
            Document doc = documentBuilder.parse(registration);
            NodeList components = doc.getElementsByTagName("component");
            for (int i = 0; i < components.getLength(); i++)
            {
                NamedNodeMap attributes = components.item(i).getAttributes();

                Node type = attributes.getNamedItem("type");
                Node clazz = attributes.getNamedItem("class");
                if (type != null)
                {
                    stringRegistrationNanoContainer.registerComponent(type.getNodeValue(), clazz.getNodeValue());
                }
                else
                {
                    stringRegistrationNanoContainer.registerComponent(clazz.getNodeValue());
                }
                addParameters(clazz.getNodeValue(), components.item(i));
            }
        }
        catch (SAXException e)
        {
            throw new NanoTextRegistrationException("SAXException:" + e.getMessage());
        }
        catch (IOException e)
        {
            throw new NanoTextRegistrationException("IOException:" + e.getMessage());
        }
        catch (PicoIntrospectionException e)
        {

        }
    }

    private void addParameters(String className, Node node) throws ClassNotFoundException, PicoIntrospectionException
    {
        Element element = (Element) node;
        NodeList paramElements = element.getElementsByTagName("param");
        for (int i = 0; i < paramElements.getLength(); i++)
        {
            Node paramNode = paramElements.item(i);
            String type = paramNode.getAttributes().getNamedItem("type").getNodeValue();
            String value = paramNode.getFirstChild().getNodeValue();
            stringRegistrationNanoContainer.addParameterToComponent(className, type, value);
        }
    }

    public boolean hasComponent(Object componentKey) {
        return stringRegistrationNanoContainer.hasComponent(componentKey);
    }

    public Object getComponent(Object componentKey) {
        return stringRegistrationNanoContainer.getComponent(componentKey);
    }

    public Collection getComponents() {
        return stringRegistrationNanoContainer.getComponents();
    }

    public Collection getComponentKeys() {
        return stringRegistrationNanoContainer.getComponentKeys();
    }

    public void instantiateComponents() throws PicoInitializationException {
        stringRegistrationNanoContainer.instantiateComponents();
    }

    public Object getCompositeComponent() {
        return stringRegistrationNanoContainer.getCompositeComponent();
    }

    public Object getCompositeComponent(boolean callInInstantiationOrder, boolean callUnmanagedComponents) {
        return stringRegistrationNanoContainer.getCompositeComponent();
    }
}
