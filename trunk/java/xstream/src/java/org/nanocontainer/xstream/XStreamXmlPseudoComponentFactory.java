/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.xstream;

import org.nanocontainer.xml.XmlPseudoComponentFactory;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import com.thoughtworks.xstream.xml.dom.DomXMLReader;
import com.thoughtworks.xstream.XStream;

/**
 * @author Paul Hammant
 * @author Marcos Tarruella
 */
public class XStreamXmlPseudoComponentFactory implements XmlPseudoComponentFactory {
    public Object makeInstance(Element elem) throws SAXException, ClassNotFoundException {
        XStream xs = new XStream();
        Object result = xs.fromXML(new DomXMLReader(elem));
        return result;
    }
}
