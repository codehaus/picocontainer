package org.nanocontainer;

import org.picocontainer.PicoConfigurationException;

public class InstantiationConfigurationException extends PicoConfigurationException {

    private final InstantiationException ie;

    public InstantiationConfigurationException(InstantiationException ie) {
        this.ie = ie;
    }

    public InstantiationException getIe() {
        return ie;
    }
}
