package org.nanocontainer.xml;

import org.picocontainer.PicoConfigurationException;

public class EmptyXmlConfigurationException extends PicoConfigurationException {
    public EmptyXmlConfigurationException() {
    }

    public String getMessage() {
        return "No components in the XML configuration";
    }
}
