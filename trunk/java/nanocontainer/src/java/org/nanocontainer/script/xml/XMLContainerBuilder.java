/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Aslak Hellesoy and Paul Hammant                          *
 *****************************************************************************/

package org.nanocontainer.script.xml;

import org.nanocontainer.SoftCompositionPicoContainer;
import org.nanocontainer.integrationkit.ContainerPopulator;
import org.nanocontainer.integrationkit.PicoCompositionException;
import org.nanocontainer.reflection.DefaultReflectionContainerAdapter;
import org.nanocontainer.reflection.DefaultSoftCompositionPicoContainer;
import org.nanocontainer.reflection.ReflectionContainerAdapter;
import org.nanocontainer.script.ScriptedContainerBuilder;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.ComponentParameter;
import org.picocontainer.defaults.ConstantParameter;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This class builds up a hierarchy of PicoContainers from an XML configuration file.
 *
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Jeppe Cramon
 * @author Mauro Talevi
 * @version $Revision$
 */
public class XMLContainerBuilder extends ScriptedContainerBuilder implements ContainerPopulator {

    private final static String DEFAULT_INSTANCE_FACTORY = XStreamComponentInstanceFactory.class.getName();
    private static final String DEFAULT_COMPONENT_ADAPTER_FACTORY = DefaultComponentAdapterFactory.class.getName();

    private final static String CONTAINER = "container";
    private final static String CLASSPATH = "classpath";
    private final static String COMPONENT = "component";
    private final static String COMPONENT_IMPLEMENTATION = "component-implementation";
    private final static String COMPONENT_INSTANCE = "component-instance";
    private final static String COMPONENT_ADAPTER = "component-adapter";
    private final static String COMPONENT_ADAPTER_FACTORY = "component-adapter-factory";
    private final static String CLASS = "class";
    private final static String FACTORY = "factory";
    private final static String FILE = "file";
    private final static String KEY = "key";
    private final static String PARAMETER = "parameter";
    private final static String URL = "url";

    private static final String EMPTY = "";

    private final Element rootElement;

    public XMLContainerBuilder(Reader script, ClassLoader classLoader) {
        super(script, classLoader);
        InputSource inputSource = new InputSource(script);
        try {
            rootElement = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputSource).getDocumentElement();
        } catch (SAXException e) {
            throw new PicoCompositionException("SAXException : " + e.getMessage(), e);
        } catch (IOException e) {
            throw new PicoCompositionException("IOException : " + e.getMessage(), e);
        } catch (ParserConfigurationException e) {
            throw new PicoCompositionException("PArserConfigurationException :" + e.getMessage(), e);
        }
    }

    protected PicoContainer createContainerFromScript(PicoContainer parentContainer, Object assemblyScope) {
        try {
            String cafName = rootElement.getAttribute(COMPONENT_ADAPTER_FACTORY);
            if (EMPTY.equals(cafName) || cafName == null) {
                cafName = DEFAULT_COMPONENT_ADAPTER_FACTORY;
            }
            ComponentAdapterFactory componentAdapterFactory = createComponentAdapterFactory(cafName);
            SoftCompositionPicoContainer container = new DefaultSoftCompositionPicoContainer(classLoader, componentAdapterFactory, parentContainer);
            populateContainer(container);
            return container;
        } catch (ClassNotFoundException e) {
            throw new PicoCompositionException("Class not found:" + e.getMessage(), e);
        }
    }

    public void populateContainer(SoftCompositionPicoContainer container) {
        try {
            registerComponentsAndChildContainers(container, rootElement);
        } catch (ClassNotFoundException e) {
            throw new PicoCompositionException("Class not found:" + e.getMessage(), e);
        } catch (IOException e) {
            throw new PicoCompositionException(e);
        } catch (SAXException e) {
            throw new PicoCompositionException(e);
        }
    }

    private void registerComponentsAndChildContainers(SoftCompositionPicoContainer parentContainer, Element containerElement) throws ClassNotFoundException, IOException, SAXException {

        NodeList children = containerElement.getChildNodes();
// register classpath first, regardless of order in the document.
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i) instanceof Element) {
                Element childElement = (Element) children.item(i);
                String name = childElement.getNodeName();
                if (CLASSPATH.equals(name)) {
                    registerClasspath(parentContainer, childElement);
                }
            }
        }
        int count = 0;
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i) instanceof Element) {
                Element childElement = (Element) children.item(i);
                String name = childElement.getNodeName();
                if (CONTAINER.equals(name)) {
                    SoftCompositionPicoContainer childContainer = new DefaultSoftCompositionPicoContainer(parentContainer.getComponentClassLoader(), parentContainer);
                    parentContainer.addChildContainer(childContainer);
                    registerComponentsAndChildContainers(childContainer, childElement);
                    count++;
                } else if (CLASSPATH.equals(name)) {
// already registered
                } else if (COMPONENT_IMPLEMENTATION.equals(name)
                        || COMPONENT.equals(name)) {
                    registerComponentImplementation(parentContainer, childElement);
                    count++;
                } else if (COMPONENT_INSTANCE.equals(name)) {
                    registerComponentInstance(parentContainer, childElement);
                    count++;
                } else if (COMPONENT_ADAPTER.equals(name)) {
                    registerComponentAdapter(parentContainer, childElement);
                    count++;
                } else {
                    throw new PicoCompositionException("Unsupported element:" + name);
                }
            }
        }
        if (count == 0) {
            throw new EmptyCompositionException();
        }
    }

    private void registerClasspath(SoftCompositionPicoContainer container, Element classpathElement) throws IOException {
        NodeList children = classpathElement.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i) instanceof Element) {
                Element childElement = (Element) children.item(i);

                String fileName = childElement.getAttribute(FILE);
                String urlSpec = childElement.getAttribute(URL);
                URL url = null;
                if (urlSpec != null && !EMPTY.equals(urlSpec)) {
                    url = new URL(urlSpec);
                } else {
                    File file = new File(fileName);
                    if (!file.exists()) {
                        throw new IOException(file.getAbsolutePath() + " doesn't exist");
                    }
                    url = file.toURL();
                }
                container.addClassLoaderURL(url);
            }
        }
    }

    private PicoContainer registerComponentImplementation(SoftCompositionPicoContainer container, Element element) throws ClassNotFoundException, SAXException {
        String className = element.getAttribute(CLASS);
        if (EMPTY.equals(className)) {
            throw new SAXException("'" + CLASS + "' attribute not specified for " + element.getNodeName());
        }

        Parameter[] parameters = createChildParameters(container, element);
        Object key = element.getAttribute(KEY);
        Class clazz = classLoader.loadClass(className);
        if (key == null || key.equals(EMPTY)) {
            if (parameters == null) {
                container.getPicoContainer().registerComponentImplementation(clazz);
            } else {
                container.getPicoContainer().registerComponentImplementation(clazz, clazz, parameters);
            }
        } else {
            if (parameters == null) {
                container.getPicoContainer().registerComponentImplementation(key, clazz);
            } else {
                container.getPicoContainer().registerComponentImplementation(key, clazz, parameters);
            }
        }
        return null;
    }

    private Parameter[] createChildParameters(SoftCompositionPicoContainer container, Element element) throws ClassNotFoundException {
        List parametersList = new ArrayList();
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i) instanceof Element) {
                Element childElement = (Element) children.item(i);
                if (PARAMETER.equals(childElement.getNodeName())) {
                    String key = childElement.getAttribute(KEY);
                    if (key != null && !EMPTY.equals(key)) {
                        parametersList.add(new ComponentParameter(key));
                    } else {
                        parametersList.add(createConstantParameter(container, childElement));
                    }
                }
            }
        }

        Parameter[] parameters = null;
        if (!parametersList.isEmpty()) {
            parameters = (Parameter[]) parametersList.toArray(new Parameter[parametersList.size()]);
        }
        return parameters;
    }

    private Parameter createConstantParameter(PicoContainer pico, Element element) throws ClassNotFoundException {
        NodeList children = element.getChildNodes();
        Element childElement = null;
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i) instanceof Element) {
                childElement = (Element) children.item(i);
                break;
            }
        }

        XMLComponentInstanceFactory factory = createComponentInstanceFactory(element.getAttribute(FACTORY));
        Object instance = factory.makeInstance(pico, childElement);
        return new ConstantParameter(instance);
    }

    private void registerComponentInstance(SoftCompositionPicoContainer container, Element element) throws ClassNotFoundException, PicoCompositionException {
        NodeList children = element.getChildNodes();
        Element childElement = null;
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i) instanceof Element) {
                childElement = (Element) children.item(i);
                break;
            }
        }

        XMLComponentInstanceFactory factory = createComponentInstanceFactory(element.getAttribute(FACTORY));
        Object instance = factory.makeInstance(container, childElement);

        String key = element.getAttribute(KEY);
        if (key == null || key.equals(EMPTY)) {
            container.getPicoContainer().registerComponentInstance(instance);
        } else {
            container.getPicoContainer().registerComponentInstance(key, instance);
        }
    }

    private XMLComponentInstanceFactory createComponentInstanceFactory(String factoryClass) throws ClassNotFoundException {
        if (factoryClass == null || factoryClass.equals(EMPTY)) {
            factoryClass = DEFAULT_INSTANCE_FACTORY;
        }

        ReflectionContainerAdapter adapter = new DefaultReflectionContainerAdapter();
        adapter.registerComponentImplementation(XMLComponentInstanceFactory.class.getName(), factoryClass);
        return (XMLComponentInstanceFactory) adapter.getPicoContainer().getComponentInstances().get(0);
    }

    private void registerComponentAdapter(SoftCompositionPicoContainer container, Element element) throws ClassNotFoundException, PicoCompositionException {
        String factoryName = element.getAttribute(FACTORY);
        if (EMPTY.equals(factoryName)) {
            factoryName = DEFAULT_COMPONENT_ADAPTER_FACTORY;
        }
        Object key = element.getAttribute(KEY);
        if (EMPTY.equals(key)) {
            throw new PicoCompositionException("'" + KEY + "' attribute not specified for " + element.getNodeName());
        }
        String className = element.getAttribute(CLASS);
        if (EMPTY.equals(className)) {
            throw new PicoCompositionException("'" + CLASS + "' attribute not specified for " + element.getNodeName());
        }
        Class implementationClass = classLoader.loadClass(className);
        Parameter[] parameters = createChildParameters(container, element);
        ComponentAdapterFactory componentAdapterFactory = createComponentAdapterFactory(factoryName);
        container.registerComponent(componentAdapterFactory.createComponentAdapter(key, implementationClass, parameters));
    }

    private ComponentAdapterFactory createComponentAdapterFactory(String factoryName) throws ClassNotFoundException, PicoCompositionException {
        Class factoryClass = classLoader.loadClass(factoryName);
        try {
            return (ComponentAdapterFactory) factoryClass.newInstance();
        } catch (InstantiationException e) {
            throw new PicoCompositionException(e);
        } catch (IllegalAccessException e) {
            throw new PicoCompositionException(e);
        }
    }
}
