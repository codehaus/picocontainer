package org.nanocontainer;

import org.picocontainer.PicoConfigurationException;
import org.xml.sax.SAXException;

public class SAXConfigurationException extends PicoConfigurationException{
    private final SAXException se;

    public SAXConfigurationException(SAXException se) {
        this.se = se;
    }

    public SAXException getSe() {
        return se;
    }
}
