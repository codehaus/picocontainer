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

import org.picocontainer.PicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.w3c.dom.Element;

import java.io.IOException;

/**
 * This interface builds up a hierarchy of PicoContainers from an XML configuration file.
 *
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */

public interface XmlFrontEnd {

    public PicoContainer createPicoContainer(Element rootElement, MutablePicoContainer mutablePicoContainer)
            throws IOException, SAXException, ClassNotFoundException, EmptyXmlConfigurationException;
    
    public PicoContainer createPicoContainer(Element rootElement)
            throws IOException, SAXException, ClassNotFoundException, EmptyXmlConfigurationException;
}
