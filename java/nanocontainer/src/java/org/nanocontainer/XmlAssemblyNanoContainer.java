/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer;

import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.nanocontainer.xml.InputSourceFrontEnd;
import org.picocontainer.PicoContainer;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileReader;
import java.io.Reader;
import java.io.IOException;

/**
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @author Ward Cunningham
 * @version $Revision$
 */
public class XmlAssemblyNanoContainer extends NanoContainer {

    public XmlAssemblyNanoContainer(Reader nanoContainerXml) throws Exception, ParserConfigurationException, ClassNotFoundException, SAXException {
        this(nanoContainerXml, new ConsoleNanoContainerMonitor());
    }

    public XmlAssemblyNanoContainer(Reader nanoContainerXml, NanoContainerMonitor monitor) throws Exception, ParserConfigurationException, ClassNotFoundException, SAXException {
        super(nanoContainerXml, monitor);
    }

    protected void configure(Reader nanoContainerXml) throws ParserConfigurationException, IOException, SAXException, ClassNotFoundException {
        InputSource is = new InputSource(nanoContainerXml);
        InputSourceFrontEnd isfe = new InputSourceFrontEnd();
        final PicoContainer rootContainer = isfe.createPicoContainer(is);

        instantiateComponentsBreadthFirst(rootContainer);
        startComponentsBreadthFirst();
    }


    public static void main(String[] args) throws Exception {
        String nanoContainerXml = args[0];
        if (nanoContainerXml == null) {
            nanoContainerXml = "config/nanocontainer.xml";
        }
        NanoContainer nano = new XmlAssemblyNanoContainer(new FileReader(nanoContainerXml));
        addShutdownHook(nano);
    }
}
