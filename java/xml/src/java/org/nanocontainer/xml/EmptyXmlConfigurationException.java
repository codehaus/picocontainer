package org.nanocontainer.xml;

public class EmptyXmlConfigurationException extends Exception {
    public EmptyXmlConfigurationException() {
    }

    public String getMessage() {
        return "No components in the XML configuration";
    }
}
