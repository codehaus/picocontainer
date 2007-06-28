/*
 * Copyright (C) 2004 Elsag-Solutions AG.
 * Created on 25.11.2004 by Jörg Schaible.
 */
package org.nanocontainer.remoting.ejb;

import org.picocontainer.PicoException;


/**
 * Service necessary to create or invoke a component is currently not available. This exception can indicate a temporary
 * unavailability of the service.
 * @author J&ouml;rg Schaible
 */
public class ServiceUnavailableException
        extends PicoException {

    /**
     * Construct a ServiceUnavailableException.
     * @param message an explanation for the exception
     * @param cause the causing exception
     */
    public ServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Construct a ServiceUnavailableException.
     * @param cause the causing exception
     */
    public ServiceUnavailableException(Throwable cause) {
        this(null, cause);
    }
}
