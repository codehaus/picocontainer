/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.script.xml;

import org.w3c.dom.Element;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomReader;

/**
 * Implementation of XMLPseudoComponentFactory that uses XStream to unmarshal
 * DOM elements.
 * 
 * @author Paul Hammant
 * @author Marcos Tarruella
 */
public class XStreamXMLPseudoComponentFactory implements XMLPseudoComponentFactory {
    /**
     * {@inheritDoc}
     * @see XMLPseudoComponentFactory#makeInstance(Element)
     */
    public Object makeInstance(Element element) throws ClassNotFoundException {
        XStream xs = new XStream();
        return xs.unmarshal(new DomReader(element));
    }
}
