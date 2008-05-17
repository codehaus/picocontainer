/*
 * Copyright (C) PicoContainer Organization. All rights reserved.            
 * ------------------------------------------------------------------------- 
 * The software in this package is published under the terms of the BSD      
 * style license a copy of which has been included with this distribution in 
 * the LICENSE.txt file.                                                     
 */ 
package org.picocontainer.logging.store;

/**
 * Thrown when a logger is not found
 * 
 * @author Mauro Talevi
 */
@SuppressWarnings("serial")
public class LoggerNotFoundException extends RuntimeException {

    public LoggerNotFoundException(String message) {
        super(message);
    }

}
