/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Aslak Hellesoy and Paul Hammant                          *
 *****************************************************************************/

package org.picoextras.script.xml;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picoextras.integrationkit.ContainerAssembler;
import org.picoextras.reflection.DefaultReflectionFrontEnd;
import org.picoextras.reflection.ReflectionFrontEnd;
import org.picoextras.script.PicoCompositionException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This class builds up a hierarchy of PicoContainers from an XML configuration file.
 * 
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Jeppe Cramon
 * @version $Revision$
 */
public class DefaultXmlFrontEnd implements ContainerAssembler {
    private Element rootElement;

    public DefaultXmlFrontEnd(Element rootElement) {
        this.rootElement = rootElement;
    }

    public void assembleContainer(MutablePicoContainer container, Object assemblyScope) {
        ReflectionFrontEnd reflectionFrontEnd = new DefaultReflectionFrontEnd(getClass().getClassLoader(),container);
        try {
            registerComponentsAndChildContainers(reflectionFrontEnd, rootElement);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
            // TODO throw new AssemblyException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
            // TODO throw new AssemblyException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
            // TODO throw new AssemblyException(e);
        }
    }

    private void registerComponentsAndChildContainers(ReflectionFrontEnd reflectionFrontEnd, Element containerElement) throws ClassNotFoundException, IOException, PicoCompositionException, SAXException {

        NodeList children = containerElement.getChildNodes();
        // register classpath first, regardless of order in the document.
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            short type = child.getNodeType();
            if (type == Document.ELEMENT_NODE) {
                String name = child.getNodeName();
                if (name.equals("classpath")) {
                    registerClasspathElement(reflectionFrontEnd, (Element) child);
                }
            }
        }
        int componentCount = 0;
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            short type = child.getNodeType();
            if (type == Document.ELEMENT_NODE) {
                String name = child.getNodeName();
                if (name.equals("pseudocomponent")) {
                    registerPseudoComponent(reflectionFrontEnd, (Element) child);
                    componentCount++;
                } else if (name.equals("component")) {
                    registerComponentImplementation(reflectionFrontEnd, (Element) child);
                    componentCount++;
                } else if (name.equals("classpath")) {
                } else {
                    throw new PicoCompositionException("Unsupported element:" + name);
                }
            }
        }
        if (componentCount == 0) {
            throw new EmptyCompositionException();
        }
    }

    private void registerClasspathElement(ReflectionFrontEnd reflectionFrontEnd, Element classpathElement) throws IOException {
        NodeList children = classpathElement.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            short type = child.getNodeType();
            if (type == Document.ELEMENT_NODE) {
                String name = child.getNodeName();
                if (name.equals("element")) {
                    String fileName = ((Element) child).getAttribute("file");
                    String urlSpec = ((Element) child).getAttribute("url");
                    URL url = null;
                    if (urlSpec != null && !"".equals(urlSpec)) {
                        url = new URL(urlSpec);
                    } else {
                        File file = new File(fileName);
                        if (!file.exists()) {
                            throw new IOException(file.getAbsolutePath() + " doesn't exist");
                        }
                        url = file.toURL();
                    }
                    reflectionFrontEnd.addClassLoaderURL(url);
                }
            }
        }
    }

    private PicoContainer registerComponentImplementation(ReflectionFrontEnd reflectionFrontEnd, Element componentElement) throws ClassNotFoundException, IOException, SAXException {
        String className = componentElement.getAttribute("class");
        if ("".equals(className)) {
            throw new SAXException("class attribute not specified for " + componentElement.getNodeName());
        }

        List parameterTypesList = new ArrayList();
        List parameterValuesList = new ArrayList();
        NodeList parameters = componentElement.getChildNodes();
        for (int i = 0; i < parameters.getLength(); i++) {
            final Node node = parameters.item(i);
            if (node.getNodeType() == Document.ELEMENT_NODE) {
                Element parameterElement = (Element) node;
                if (parameterElement.getNodeName().equals("parameter")) {
                    String type = parameterElement.getAttribute("class");
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

        String key = componentElement.getAttribute("key");
        ComponentAdapter componentAdapter;
        if (key == null || key.equals("")) {
            if(parameterTypes == null) {
                componentAdapter = reflectionFrontEnd.registerComponentImplementation(className);
            } else {
                componentAdapter = reflectionFrontEnd.registerComponentImplementation(className, parameterTypes, parameterValues);
            }
        } else {
            if(parameterTypes == null) {
                componentAdapter = reflectionFrontEnd.registerComponentImplementation(key, className);
            } else {
                componentAdapter = reflectionFrontEnd.registerComponentImplementation(key, className, parameterTypes, parameterValues);
            }
        }
        if (PicoContainer.class.isAssignableFrom(componentAdapter.getComponentImplementation())) {
            // the component was actually a container. Recurse further down.
            MutablePicoContainer childContainer = (MutablePicoContainer) componentAdapter.getComponentInstance();
            ReflectionFrontEnd childFrontEnd = new DefaultReflectionFrontEnd(reflectionFrontEnd, childContainer);
            registerComponentsAndChildContainers(childFrontEnd, componentElement);
            return childContainer;
        } else {
            return null;
        }
    }

    private void registerPseudoComponent(ReflectionFrontEnd pico, Element componentElement) throws ClassNotFoundException, PicoCompositionException {
        String factoryClass = componentElement.getAttribute("factory");

        if (factoryClass == null || factoryClass.equals("")) {
            throw new java.lang.IllegalArgumentException("factory attribute should be specified for pseudocomponent element");
            // unless we provide a default.
        }

        ReflectionFrontEnd tempContainer = new DefaultReflectionFrontEnd();
        tempContainer.registerComponentImplementation(XmlPseudoComponentFactory.class.getName(), factoryClass);
        XmlPseudoComponentFactory factory = (XmlPseudoComponentFactory) tempContainer.getPicoContainer().getComponentInstances().get(0);

        NodeList nl = componentElement.getChildNodes();
        Element childElement = null;
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i) instanceof Element) {
                childElement = (Element) nl.item(i);
                break;
            }
        }

        try {
            Object pseudoComp = factory.makeInstance(childElement);
            pico.getPicoContainer().registerComponentInstance(pseudoComp);
        } catch (final SAXException e) {
            throw new PicoCompositionException(e);
        }
    }
}
