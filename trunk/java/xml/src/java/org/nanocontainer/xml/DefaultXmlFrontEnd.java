/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Aslak Hellesoy and Paul Hammant                          *
 *****************************************************************************/

package org.nanocontainer.xml;

import org.nanocontainer.reflection.ReflectionFrontEnd;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * This class builds up a hierarchy of PicoContainers from an XML configuration file.
 *
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class DefaultXmlFrontEnd implements XmlFrontEnd {

    public PicoContainer createPicoContainer(Element rootElement) throws IOException, SAXException, ClassNotFoundException, EmptyXmlConfigurationException {
        MutablePicoContainer mutablePicoContainer = getMutablePicoContainerFromContainerAttribute(rootElement);
        return createPicoContainer(rootElement, mutablePicoContainer);
    }

    public PicoContainer createPicoContainer(Element rootElement, MutablePicoContainer mutablePicoContainer)
            throws IOException, SAXException, ClassNotFoundException, EmptyXmlConfigurationException {

        ReflectionFrontEnd rootReflectionFrontEnd = new ReflectionFrontEnd(mutablePicoContainer);
        registerComponentsAndChildContainers(rootReflectionFrontEnd, rootElement);

        return rootReflectionFrontEnd.getPicoContainer();
    }

    private void registerComponentsAndChildContainers(ReflectionFrontEnd reflectionFrontEnd, Element containerElement) throws ClassNotFoundException, IOException, EmptyXmlConfigurationException {

        NodeList children = containerElement.getChildNodes();
        // register classpath first, regardless of order in the document.
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            short type = child.getNodeType();
            if (type == Document.ELEMENT_NODE) {
                String name = child.getNodeName();
                if (name.equals("classpath")) {
                    registerClasspath(reflectionFrontEnd, (Element) child);
                }
            }
        }
        int componentCount = 0;
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            short type = child.getNodeType();
            if (type == Document.ELEMENT_NODE) {
                String name = child.getNodeName();
                if (name.equals("component")) {
                    registerComponent(reflectionFrontEnd, (Element) child);
                    componentCount++;
                } else if (name.equals("container")) {

                    MutablePicoContainer mutablePicoContainer = null;

                    mutablePicoContainer = getMutablePicoContainerFromContainerAttribute(child);

                    ReflectionFrontEnd childFrontEnd = new ReflectionFrontEnd(reflectionFrontEnd, mutablePicoContainer);
                    registerComponentsAndChildContainers(childFrontEnd, (Element) child);
                }
            }
        }
        if (componentCount == 0) {
            throw new EmptyXmlConfigurationException();
        }
    }

    private MutablePicoContainer getMutablePicoContainerFromContainerAttribute(Node child) throws ClassNotFoundException {
        MutablePicoContainer mutablePicoContainer;
        String picoContainerClassName = ((Element) child).getAttribute("container");

        if (picoContainerClassName == null || picoContainerClassName.equals("")) {
            mutablePicoContainer = new DefaultPicoContainer();
        } else {
            try {
                mutablePicoContainer = (MutablePicoContainer) this.getClass().getClassLoader().loadClass(picoContainerClassName).newInstance();
            } catch (InstantiationException e) {
                throw new ClassNotFoundException("InstantiationException in DefaultXmlFrontEnd - " + e.getMessage());
            } catch (IllegalAccessException e) {
                throw new ClassNotFoundException("IllegalAccessException in DefaultXmlFrontEnd - " + e.getMessage());
            }
        }
        return mutablePicoContainer;
    }

    private void registerClasspath(ReflectionFrontEnd reflectionFrontEnd, Element classpathElement) throws IOException {
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

    private void registerComponent(ReflectionFrontEnd pico, Element componentElement) throws ClassNotFoundException {
        String className = componentElement.getAttribute("classname");
        String key = componentElement.getAttribute("key");
        if (key == null || key.equals("")) {
            key = className;
        }
        pico.registerComponent(key, className);
    }
}
