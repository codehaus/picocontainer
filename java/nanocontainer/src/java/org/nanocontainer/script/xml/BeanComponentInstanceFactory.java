/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.script.xml;

import java.util.Properties;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.defaults.BeanPropertyComponentAdapter;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Implementation of XMLComponentInstanceFactory that uses BeanPropertyComponentAdapter 
 * to create instances from DOM elements.
 * 
 * @author Paul Hammant
 * @author Marcos Tarruella
 * @author Mauro Talevi
 */
public class BeanComponentInstanceFactory implements XMLComponentInstanceFactory {
    
    /**
     * {@inheritDoc}
     * @see XMLComponentInstanceFactory#makeInstance(Element)
     */
    public Object makeInstance(Element element) throws ClassNotFoundException {
        String className = element.getNodeName();
        BeanPropertyComponentAdapter propertyComponentAdapter = 
            new BeanPropertyComponentAdapter(createComponentAdapter(className));
        propertyComponentAdapter.setProperties(createProperties(element.getChildNodes()));
        return propertyComponentAdapter.getComponentInstance();
    }

    private ComponentAdapter createComponentAdapter(String className) throws ClassNotFoundException{
        Class implementation = Class.forName(className);
        ComponentAdapterFactory factory = new DefaultComponentAdapterFactory();
        return factory.createComponentAdapter(className, implementation, new Parameter[]{});        
    }

    private Properties createProperties(NodeList nodeList){
        Properties properties = new Properties();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node n = nodeList.item(i);
            if (n instanceof Element) {
                Element e = (Element) n;
                properties.setProperty(e.getNodeName(), e.getChildNodes().item(0).getNodeValue());
            }
        }
        return properties;
    }
}
