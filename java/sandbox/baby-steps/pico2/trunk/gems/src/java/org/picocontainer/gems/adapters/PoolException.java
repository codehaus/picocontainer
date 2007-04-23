/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Aslak Hellesoy & Joerg Schaible                                       *
 *****************************************************************************/
package org.picocontainer.gems.adapters;

import org.picocontainer.PicoIntrospectionException;


/**
 * Exception thrown from the PoolingComponentAdapter. Only thrown if the interaction with the internal pool fails.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.2
 */
public class PoolException extends PicoIntrospectionException {

    private static final long serialVersionUID = 1L;

    /**
     * Construct a PoolException with an explaining message and a originalting cause.
     * 
     * @param message the explaining message
     * @param cause the originating cause
     * @since 1.2
     */
    public PoolException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construct a PoolException with an explaining message.
     * 
     * @param message the explaining message
     * @since 1.2
     */
    public PoolException(String message) {
        super(message);
    }

}
