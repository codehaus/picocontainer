package org.nanocontainer.xml;

import org.picocontainer.PicoContainer;
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
import java.net.URL;

/**
 * This class builds up a hierarchy of PicoContainers from an XML configuration file.
 *
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

    public PicoContainer createPicoContainer(InputSource inputSource) throws IOException, SAXException, ClassNotFoundException, EmptyXmlConfigurationException {
        Document document = documentBuilder.parse(inputSource);
        Element containerElement = document.getDocumentElement();
        ReflectionFrontEnd rootReflectionFrontEnd = new ReflectionFrontEnd();
        registerComponentsAndChildContainers(rootReflectionFrontEnd, containerElement);

        return rootReflectionFrontEnd.getPicoContainer();
    }

    private void registerComponentsAndChildContainers(ReflectionFrontEnd reflectionFrontEnd, Element containerElement) throws ClassNotFoundException, IOException, EmptyXmlConfigurationException {
        NodeList children = containerElement.getChildNodes();
        // register classpath first, regardless of order in the document.
        for(int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            short type = child.getNodeType();
            if(type == Document.ELEMENT_NODE) {
                String name = child.getNodeName();
                if(name.equals("classpath")) {
                    registerClasspath(reflectionFrontEnd, (Element)child);
                }
            }
        }
        int componentCount = 0;
        for(int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            short type = child.getNodeType();
            if(type == Document.ELEMENT_NODE) {
                String name = child.getNodeName();
                if(name.equals("component")) {
                    registerComponent(reflectionFrontEnd, (Element)child);
                    componentCount++;
                } else if(name.equals("container")) {
                    ReflectionFrontEnd childFrontEnd = new ReflectionFrontEnd(reflectionFrontEnd);
                    registerComponentsAndChildContainers(childFrontEnd, (Element)child);
                }
            }
        }
        if(componentCount == 0) {
            throw new EmptyXmlConfigurationException();
        }
    }


    private void registerClasspath(ReflectionFrontEnd reflectionFrontEnd, Element classpathElement) throws IOException {
        NodeList children = classpathElement.getChildNodes();
        for(int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            short type = child.getNodeType();
            if(type == Document.ELEMENT_NODE) {
                String name = child.getNodeName();
                if(name.equals("element")) {
                    String fileName = ((Element)child).getAttribute("file");
                    String urlSpec = ((Element)child).getAttribute("url");
                    URL url = null;
                    if(urlSpec != null && !"".equals(urlSpec)) {
                        url = new URL(urlSpec);
                    } else {
                        File file = new File(fileName);
                        if(!file.exists()) {
                            throw new IOException(file.getAbsolutePath() + " doesn't exist" );
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
        if(key == null || key.equals("")) {
            key = className;
        }
        pico.registerComponent(key, className);
    }
}
