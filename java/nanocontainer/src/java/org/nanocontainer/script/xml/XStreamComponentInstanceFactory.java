/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.script.xml;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomReader;
import org.picocontainer.PicoContainer;
import org.w3c.dom.Element;

/**
 * Implementation of XMLComponentInstanceFactory that uses XStream to unmarshal
 * DOM elements.
 *
 * @author Paul Hammant
 * @author Marcos Tarruella
 */
public class XStreamComponentInstanceFactory implements XMLComponentInstanceFactory {
    /**
     * {@inheritDoc}
     *
     * @see XMLComponentInstanceFactory#makeInstance(PicoContainer, Element)
     */
    public Object makeInstance(PicoContainer pico, Element element) throws ClassNotFoundException {
        XStream xs = new XStream();
        return xs.unmarshal(new DomReader(element));
    }
}
