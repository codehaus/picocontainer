package org.nanocontainer.xml;

import org.picocontainer.PicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.nanocontainer.reflection.ReflectionFrontEnd;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLClassLoader;

/**
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class InputSourceFrontEnd {
    private DocumentBuilder documentBuilder;

    public InputSourceFrontEnd(DocumentBuilder documentBuilder) {
        this.documentBuilder = documentBuilder;
    }

    public InputSourceFrontEnd() throws ParserConfigurationException {
        this(DocumentBuilderFactory.newInstance().newDocumentBuilder());
    }

    public PicoContainer createPicoContainer(InputSource inputSource) throws IOException, SAXException, ClassNotFoundException {
        Document document = documentBuilder.parse(inputSource);
        Element containerElement = document.getDocumentElement();
        MutablePicoContainer pico = new DefaultPicoContainer();
        ReflectionFrontEnd reflectionFrontEnd = new ReflectionFrontEnd(pico);
        registerComponentsAndChildContainers(reflectionFrontEnd, containerElement);

        return pico;
    }

    private void registerComponentsAndChildContainers(ReflectionFrontEnd reflectionFrontEnd, Element containerElement) throws ClassNotFoundException, MalformedURLException {
        NodeList children = containerElement.getChildNodes();
        for(int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            short type = child.getNodeType();
            if(type == Document.ELEMENT_NODE) {
                String name = child.getNodeName();
                if(name.equals("component")) {
                    registerComponent(reflectionFrontEnd, (Element)child);
                } else if(name.equals("container")) {
                    registerContainer(reflectionFrontEnd, (Element)child);
                }
            }
        }
    }


    private void registerClassLoaders(ReflectionFrontEnd reflectionFrontEnd, Element containerElement, ClassLoader parentClassLoader) throws MalformedURLException {
        List jarNames = new ArrayList();
        NodeList children = containerElement.getChildNodes();
        for(int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            short type = child.getNodeType();
            if(type == Document.ELEMENT_NODE) {
                String name = child.getNodeName();
                if(name.equals("jarfile")) {
                    String jarName = ((Element)child).getAttribute("location");
                    jarNames.add(jarName);
                }
            }
        }
        URL[] urls = new URL[jarNames.size()];
        for (int i=0; i < urls.length; i++) {
            urls[i] = new File((String)jarNames.get(i)).toURL();
        }
        ClassLoader classLoader = new URLClassLoader(urls, parentClassLoader);
        reflectionFrontEnd.addComponentClassLoader(classLoader);
    }

    private void registerComponent(ReflectionFrontEnd pico, Element componentElement) throws ClassNotFoundException {
        String className = componentElement.getAttribute("classname");
        String key = componentElement.getAttribute("key");
        if(key == null || key.equals("")) {
            key = className;
        }
        pico.registerComponent(key, className);
    }

    private void registerContainer(ReflectionFrontEnd reflectionFrontEnd, Element element) throws ClassNotFoundException, MalformedURLException {
        MutablePicoContainer parent = reflectionFrontEnd.getPicoContainer();
        DefaultPicoContainer delegatingPicoContainer = new DefaultPicoContainer();
        delegatingPicoContainer.addParent(parent);
        ReflectionFrontEnd childFrontEnd = new ReflectionFrontEnd(delegatingPicoContainer);
        registerClassLoaders(childFrontEnd, element, parent.getClass().getClassLoader());
        registerComponentsAndChildContainers(childFrontEnd, element);
    }
}
