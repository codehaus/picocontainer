package org.nanocontainer;

import org.nanocontainer.xml.XmlFrontEnd;
import org.nanocontainer.xml.DefaultXmlFrontEnd;
import org.nanocontainer.xml.EmptyXmlConfigurationException;
import org.picocontainer.PicoContainer;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;

public class BespokeXmlFrontEnd implements XmlFrontEnd {

    private DefaultXmlFrontEnd xmlFrontEnd;
    public static boolean used;

    public BespokeXmlFrontEnd() {
        xmlFrontEnd = new DefaultXmlFrontEnd();
    }

    public PicoContainer createPicoContainer(Element rootElement) throws IOException, SAXException, ClassNotFoundException, EmptyXmlConfigurationException {
        used = true;
        return xmlFrontEnd.createPicoContainer(rootElement);
    }
}
