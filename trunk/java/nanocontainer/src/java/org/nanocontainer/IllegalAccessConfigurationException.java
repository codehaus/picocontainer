package org.nanocontainer;

import org.picocontainer.PicoConfigurationException;

public class IllegalAccessConfigurationException extends PicoConfigurationException {

    private final IllegalAccessException ie;

    public IllegalAccessConfigurationException(IllegalAccessException ie) {
        this.ie = ie;
    }

    public IllegalAccessException getIe() {
        return ie;
    }
}
