/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.lifecycle;

import org.picocontainer.PicoException;

/**
 * Subclass of {@link PicoException} that is thrown when there is a problem 
 * invoking lifecycle methods via reflection.
 * 
 * @author Paul Hammant
 * @author Mauro Talevi
 * @version $Revision: 1.3 $
 * @since 1.2
 */
public class ReflectionLifecycleException extends PicoException {

    /**
     * Construct a new exception with the specified cause and the specified detail message.
     *
     * @param message the message detailing the exception.
     * @param cause   the exception that caused this one.
     */
    protected ReflectionLifecycleException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
}
