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

import org.nanocontainer.integrationkit.PicoCompositionException;
import org.nanocontainer.reflection.DefaultReflectionContainerAdapter;
import org.nanocontainer.reflection.ReflectionContainerAdapter;
import org.nanocontainer.reflection.DefaultSoftCompositionPicoContainer;
import org.nanocontainer.script.ScriptedContainerBuilder;
import org.nanocontainer.SoftCompositionPicoContainer;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
public class XMLContainerBuilder extends ScriptedContainerBuilder {

    private final Element rootElement;

    private final static String DEFAULT_INSTANCE_FACTORY = BeanXMLPseudoComponentFactory.class.getName();

    private final static String CONTAINER = "container";
    private final static String CLASSPATH = "classpath";
    private final static String COMPONENT = "component";
    private final static String COMPONENTADAPTERFACTORY = "componentadapterfactory";
    private final static String PSEUDOCOMPONENT = "pseudocomponent";
    private final static String CLASS = "class";
    private final static String ELEMENT = "element";
    private final static String FACTORY = "factory";
    private final static String FILE = "file";
    private final static String KEY = "key";
    private final static String PARAMETER = "parameter";
    private final static String URL = "url";

    private static final String EMPTY = "";
    
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
            String cafName = rootElement.getAttribute(COMPONENTADAPTERFACTORY);
            if (EMPTY.equals(cafName) || cafName == null) {
                cafName = DefaultComponentAdapterFactory.class.getName();
            }
            Class cfaClass = classLoader.loadClass(cafName);
            ComponentAdapterFactory componentAdapterFactory = (ComponentAdapterFactory) cfaClass.newInstance();
            DefaultSoftCompositionPicoContainer result = new DefaultSoftCompositionPicoContainer(classLoader, componentAdapterFactory,
                    parentContainer);
            registerComponentsAndChildContainers(result, rootElement);
            return result;
        } catch (ClassNotFoundException e) {
            throw new PicoCompositionException("Class Not Found:" + e.getMessage(),e);
        } catch (InstantiationException e) {
            throw new PicoCompositionException(e);
        } catch (IllegalAccessException e) {
            throw new PicoCompositionException(e);
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
            Node child = children.item(i);
            short type = child.getNodeType();
            if (type == Document.ELEMENT_NODE) {
                String name = child.getNodeName();
                if (name.equals(CLASSPATH)) {
                    registerClasspathElement(parentContainer, (Element) child);
                }
            }
        }
        int count = 0;
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            short type = child.getNodeType();
            if (type == Document.ELEMENT_NODE) {
                String name = child.getNodeName();
                if (name.equals(PSEUDOCOMPONENT)) {
                    registerPseudoComponent(parentContainer, (Element) child);
                    count++;
                } else if (name.equals(COMPONENT)) {
                    registerComponentImplementation(parentContainer, (Element) child);
                    count++;
                } else if (name.equals(CONTAINER)) {
                    SoftCompositionPicoContainer newChild = new DefaultSoftCompositionPicoContainer(parentContainer.getComponentClassLoader(), parentContainer);
                    parentContainer.addChildContainer(newChild);
                    registerComponentsAndChildContainers(newChild, (Element) child);
                    count++;
                } else if (name.equals(CLASSPATH)) {
                } else {
                    throw new PicoCompositionException("Unsupported element:" + name);
                }
            }
        }
        if (count == 0) {
            throw new EmptyCompositionException();
        }
    }

    private void registerClasspathElement(ReflectionContainerAdapter reflectionContainerAdapter, Element classpathElement) throws IOException {
        NodeList children = classpathElement.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            short type = child.getNodeType();
            if (type == Document.ELEMENT_NODE) {
                String name = child.getNodeName();
                if (name.equals(ELEMENT)) {
                    String fileName = ((Element) child).getAttribute(FILE);
                    String urlSpec = ((Element) child).getAttribute(URL);
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
                    reflectionContainerAdapter.addClassLoaderURL(url);
                }
            }
        }
    }

    private PicoContainer registerComponentImplementation(ReflectionContainerAdapter reflectionContainerAdapter, Element componentElement) throws ClassNotFoundException, IOException, SAXException {
        String className = componentElement.getAttribute(CLASS);
        if (EMPTY.equals(className)) {
            throw new SAXException("class attribute not specified for " + componentElement.getNodeName());
        }

        List parameterTypesList = new ArrayList();
        List parameterValuesList = new ArrayList();
        NodeList parameters = componentElement.getChildNodes();
        for (int i = 0; i < parameters.getLength(); i++) {
            final Node node = parameters.item(i);
            if (node.getNodeType() == Document.ELEMENT_NODE) {
                Element parameterElement = (Element) node;
                if (parameterElement.getNodeName().equals(PARAMETER)) {
                    String type = parameterElement.getAttribute(CLASS);
                    String value = parameterElement.getChildNodes().item(0).getNodeValue();
                    parameterTypesList.add(type);
                    parameterValuesList.add(value);
                }
            }
        }
        String[] parameterTypes = null;
        String[] parameterValues = null;
        if(!parameterTypesList.isEmpty()) {
            parameterTypes = (String[]) parameterTypesList.toArray(new String[parameterTypesList.size()]);
            parameterValues = (String[]) parameterValuesList.toArray(new String[parameterValuesList.size()]);
        }

        String key = componentElement.getAttribute(KEY);
        ComponentAdapter componentAdapter;
        if (key == null || key.equals(EMPTY)) {
            if(parameterTypes == null) {
                componentAdapter = reflectionContainerAdapter.registerComponentImplementation(className);
            } else {
                componentAdapter = reflectionContainerAdapter.registerComponentImplementation(className, parameterTypes, parameterValues);
            }
        } else {
            if(parameterTypes == null) {
                componentAdapter = reflectionContainerAdapter.registerComponentImplementation(key, className);
            } else {
                componentAdapter = reflectionContainerAdapter.registerComponentImplementation(key, className, parameterTypes, parameterValues);
            }
        }
        return null;
    }

    private void registerPseudoComponent(ReflectionContainerAdapter reflectionComponentAdapter, Element componentElement) throws ClassNotFoundException, PicoCompositionException {
        String factoryClass = componentElement.getAttribute(FACTORY);
        String key = componentElement.getAttribute(KEY);
        
        if (factoryClass == null || factoryClass.equals(EMPTY)) {
            factoryClass = DEFAULT_INSTANCE_FACTORY;
        }

        ReflectionContainerAdapter tempContainerAdapter = new DefaultReflectionContainerAdapter();
        tempContainerAdapter.registerComponentImplementation(XMLPseudoComponentFactory.class.getName(), factoryClass);
        XMLPseudoComponentFactory factory = (XMLPseudoComponentFactory) tempContainerAdapter.getPicoContainer().getComponentInstances().get(0);

        NodeList nl = componentElement.getChildNodes();
        Element childElement = null;
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i) instanceof Element) {
                childElement = (Element) nl.item(i);
                break;
            }
        }

        Object instance = factory.makeInstance(childElement);
        if ( key == null || key.equals(EMPTY) ){ 
            reflectionComponentAdapter.getPicoContainer().registerComponentInstance(instance);
        } else {
            reflectionComponentAdapter.getPicoContainer().registerComponentInstance(key, instance);            
        }
    }
}
