/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *                                                                           *
 *****************************************************************************/

package org.nanocontainer.script.xml;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.xml.dom.DomXMLReader;
import org.nanocontainer.script.ScriptedContainerBuilder;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.ComponentParameter;
import org.picocontainer.defaults.ConstantParameter;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.nanocontainer.integrationkit.PicoCompositionException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

/**
 * This class builds up a hierarchy of PicoContainers from an XML configuration file.
 *
 * @author Konstantin Pribluda
 * @version $Revision$
 */
public class XStreamContainerBuilder extends ScriptedContainerBuilder {
    private final Element rootElement;

    private final static String IMPLEMENTATION = "implementation";
    private final static String INSTANCE = "instance";
    private final static String CLASS = "class";
    private final static String KEY = "key";
    private final static String CONSTANT = "constant";
    private final static String DEPENDENCY = "dependency";

    public XStreamContainerBuilder(Reader script, ClassLoader classLoader) {
        super(script, classLoader);
        InputSource inputSource = new InputSource(script);
        try {
            rootElement = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputSource).getDocumentElement();
        } catch (SAXException e) {
            throw new PicoCompositionException(e);
        } catch (IOException e) {
            throw new PicoCompositionException(e);
        } catch (ParserConfigurationException e) {
            throw new PicoCompositionException(e);
        }
    }

    public void buildContainer(ObjectReference containerRef, ObjectReference parentContainerRef, Object assemblyScope) {

        MutablePicoContainer container = createContainer();
        try {
            containerRef.set(container);
            populateContainer(container, rootElement);

        } catch (ClassNotFoundException e) {
            throw new PicoCompositionException(e);
            //} catch (IOException e) {
            //    throw new PicoCompositionException(e);
        } catch (SAXException e) {
            throw new PicoCompositionException(e);
        }
    }

    /**
     * populate contaiber off root element. we process instance &amp; implementation
     * nodes here
     */
    protected void populateContainer(MutablePicoContainer container, Element rootElement) throws SAXException, ClassNotFoundException {
        NodeList children = rootElement.getChildNodes();
        Node child;
        String name;
        short type;
        for (int i = 0; i < children.getLength(); i++) {
            child = children.item(i);
            type = child.getNodeType();

            if (type == Document.ELEMENT_NODE) {
                name = child.getNodeName();
                if (IMPLEMENTATION.equals(name)) {
                    insertImplementation(container, (Element) child);
                } else if (INSTANCE.equals(name)) {
                    insertInstance(container, (Element) child);
                } else {
                    throw new PicoCompositionException("Unsupported element:" + name);
                }
            }
        }
    }

    /**
     * process implementation node
     */
    protected void insertImplementation(MutablePicoContainer container, Element rootElement) throws SAXException, ClassNotFoundException {
        String key = rootElement.getAttribute(KEY);
        String klass = rootElement.getAttribute(CLASS);
        if (klass == null || "".equals(klass)) {
            throw new PicoCompositionException("class specification is required for component implementation");
        }

        Class clazz = classLoader.loadClass(klass);

        ArrayList parameters = new ArrayList();

        NodeList children = rootElement.getChildNodes();
        Node child;
        String name;
        String dependencyKey;
        String dependencyClass;
        Object parseResult;

        for (int i = 0; i < children.getLength(); i++) {
            child = children.item(i);
            if (child.getNodeType() == Document.ELEMENT_NODE) {
                name = child.getNodeName();
                // constant parameter. it does not have any attributes.
                if (CONSTANT.equals(name)) {
                    // create constant with xstream
                    parseResult = parseElementChild((Element) child);
                    if (parseResult == null) {
                        throw new PicoCompositionException("could not parse constant parameter");
                    }
                    parameters.add(new ConstantParameter(parseResult));
                } else if (DEPENDENCY.equals(name)) {
                    // either key or class must be present. not both
                    // key has prececence
                    dependencyKey = ((Element) child).getAttribute(KEY);
                    if (dependencyKey == null || "".equals(dependencyKey)) {
                        dependencyClass = ((Element) child).getAttribute(CLASS);
                        if (dependencyClass == null || "".equals(dependencyClass)) {
                            throw new PicoCompositionException("either key or class must be present for dependecy");
                        } else {
                            parameters.add(new ComponentParameter(classLoader.loadClass(dependencyClass)));
                        }
                    } else {
                        parameters.add(new ComponentParameter(dependencyKey));
                    }
                }
            }
        }

        // ok , we processed our children. insert implementation
        Parameter[] parameterArray = (Parameter[]) parameters.toArray(new Parameter[parameters.size()]);

        if (key == null || "".equals(key)) {
            // without  key. clazz is our key
            container.registerComponentImplementation(clazz, clazz, parameterArray);
        } else {
            // with key
            container.registerComponentImplementation(key, clazz, parameterArray);
        }
    }

    /**
     * process instance node. we get key from atributes ( if any ) and leave content
     * to xstream. we allow only one child node inside. ( first  one wins )
     */
    protected void insertInstance(MutablePicoContainer container, Element rootElement) throws SAXException, ClassNotFoundException {
        String key = rootElement.getAttribute(KEY);
        Object result = parseElementChild(rootElement);
        if (result == null) {
            throw new PicoCompositionException("no content could be parsed in instance");
        }
        if (key != null && !"".equals(key)) {
            // insert with key
            container.registerComponentInstance(key, result);
        } else {
            // or without
            container.registerComponentInstance(result);
        }
    }

    /**
     * parse element child with xstream and provide object
     */
    protected Object parseElementChild(Element rootElement) throws SAXException, ClassNotFoundException {
        NodeList children = rootElement.getChildNodes();
        Node child;
        for (int i = 0; i < children.getLength(); i++) {
            child = children.item(i);
            if (child.getNodeType() == Document.ELEMENT_NODE) {
                return (new XStream()).fromXML(new DomXMLReader((Element) child));
            }
        }
        return null;
    }


    protected MutablePicoContainer createContainer() {
        try {
            String cafName = rootElement.getAttribute("componentadapterfactory");
            if ("".equals(cafName) || cafName == null) {
                cafName = DefaultComponentAdapterFactory.class.getName();
            }
            Class cfaClass = classLoader.loadClass(cafName);
            ComponentAdapterFactory componentAdapterFactory = (ComponentAdapterFactory) cfaClass.newInstance();
            return new DefaultPicoContainer(componentAdapterFactory);
        } catch (ClassNotFoundException e) {
            throw new PicoCompositionException(e);
        } catch (InstantiationException e) {
            throw new PicoCompositionException(e);
        } catch (IllegalAccessException e) {
            throw new PicoCompositionException(e);
        }
    }

}
