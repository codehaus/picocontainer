/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.xml;

import org.nanocontainer.reflection.DefaultReflectionFrontEnd;
import org.nanocontainer.reflection.ReflectionFrontEnd;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Paul Hammant
 * @author Marcos Tarruella
 */
public class BeanXmlPseudoComponentFactory implements XmlPseudoComponentFactory {

    public Object makeInstance(Element elem) throws SAXException, ClassNotFoundException {
        String className = elem.getNodeName();
        ReflectionFrontEnd rfe = new DefaultReflectionFrontEnd();
        rfe.registerComponentImplementation(className);
        Object o = rfe.getPicoContainer().getComponentInstances().get(0);

        NodeList nl = elem.getChildNodes();
        for (int i=0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            if (n instanceof Element) {
                Element e = (Element) n;
                setBeanProperty(o, e.getNodeName(), e.getChildNodes().item(0).getNodeValue());
            }
        }
        return o;
    }

    private void setBeanProperty(Object o, String nodeName, String nodeValue) {
        String methodName = "set" + nodeName.substring(0,1).toUpperCase() + nodeName.substring(1);
        Method[] methods = o.getClass().getMethods();
        for (int i=0; i < methods.length; i++) {
            Method method = methods[i];
            if (method.getName().equals(methodName)) {
                Class paramType = method.getParameterTypes()[0];
                try {
                    if (paramType == String.class) {
                        method.invoke(o, new Object[] {nodeValue});
                        return;
                    } else if (paramType == Integer.TYPE) {
                        method.invoke(o, new Object[] { new Integer(nodeValue)});
                        return;
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        throw new UnsupportedOperationException("Whoa - bad type!!");
    }
}
