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
import org.nanocontainer.reflection.DefaultReflectionFrontEnd;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * This class builds up a hierarchy of PicoContainers from an XML configuration file.
 *
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class DefaultXmlFrontEnd implements XmlFrontEnd {

    public PicoContainer createPicoContainer(Element rootElement) throws IOException, SAXException, ClassNotFoundException, PicoCompositionException {
        MutablePicoContainer mutablePicoContainer = getMutablePicoContainerFromContainerAttribute(rootElement);
        return createPicoContainer(rootElement, mutablePicoContainer);
    }

    public PicoContainer createPicoContainer(Element rootElement, MutablePicoContainer mutablePicoContainer)
            throws IOException, SAXException, ClassNotFoundException, PicoCompositionException {

        ReflectionFrontEnd rootReflectionFrontEnd = new DefaultReflectionFrontEnd(mutablePicoContainer);
        registerComponentsAndChildContainers(rootReflectionFrontEnd, rootElement);

        return rootReflectionFrontEnd.getPicoContainer();
    }

    private void registerComponentsAndChildContainers(ReflectionFrontEnd reflectionFrontEnd, Element containerElement) throws ClassNotFoundException, IOException, PicoCompositionException {

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
                if (name.equals("pseudocomponent")) {
                    registerPseudoComponent(reflectionFrontEnd, (Element) child);
                    componentCount++;
                } if (name.equals("component")) {
                    registerComponent(reflectionFrontEnd, (Element) child);
                    componentCount++;
                } else if (name.equals("container")) {

                    MutablePicoContainer mutablePicoContainer = getMutablePicoContainerFromContainerAttribute(child);

                    ReflectionFrontEnd childFrontEnd = new DefaultReflectionFrontEnd(reflectionFrontEnd, mutablePicoContainer);
                    registerComponentsAndChildContainers(childFrontEnd, (Element) child);
                }
            }
        }
        if (componentCount == 0) {
            throw new EmptyXmlCompositionException();
        }
    }

    private MutablePicoContainer getMutablePicoContainerFromContainerAttribute(Node child) throws ClassNotFoundException {
        String picoContainerClassName = ((Element) child).getAttribute("container");
        String componentAdaptorClassName = ((Element) child).getAttribute("componentadaptor");

        ReflectionFrontEnd tempContainer = new DefaultReflectionFrontEnd();

        if (componentAdaptorClassName != null && !componentAdaptorClassName.equals("")) {
            tempContainer.registerComponent(ComponentAdapterFactory.class, componentAdaptorClassName);
        }
        if (picoContainerClassName == null || picoContainerClassName.equals("")) {
            tempContainer.registerComponent(PicoContainer.class, DefaultPicoContainer.class.getName());
        } else {
            tempContainer.registerComponentImplementation(picoContainerClassName);
        }
        return (MutablePicoContainer) tempContainer.getPicoContainer().getComponentInstance(PicoContainer.class);
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
        String className = componentElement.getAttribute("impl");
        String stringKey = componentElement.getAttribute("stringkey");
        String typeKey = componentElement.getAttribute("typekey");

        ArrayList hints = new ArrayList();
        NodeList children = componentElement.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeName() == "hint") {
                hints.add(((Element) child).getAttribute("stringkey"));
            }
        }


        //TODO we need to have config elements parsing here, and register with params action...
        if (stringKey == null || stringKey.equals("")) {
            stringKey = className;
        }
        if (typeKey == null || typeKey.equals("")) {
            pico.registerComponent(stringKey, className);
        } else {
            pico.registerComponentWithClassKey(typeKey,className);
        }
    }

    private void registerPseudoComponent(ReflectionFrontEnd pico, Element componentElement) throws ClassNotFoundException, PicoCompositionException {
        String factoryClass = componentElement.getAttribute("factory");

        if (factoryClass == null || factoryClass.equals("")) {
            throw new java.lang.IllegalArgumentException("factory attribute should be specified for pseudocomponent element");
            // unless we provide a default.
        }

        ReflectionFrontEnd tempContainer = new DefaultReflectionFrontEnd();
        tempContainer.registerComponentWithClassKey(XmlPseudoComponentFactory.class.getName(), factoryClass);
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
            throw new PicoCompositionException() {
                public String getMessage() {
                    return "SAXException during creation of PseudoComponent :" + e.getMessage();
                }
            };
        }
    }
}
