/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer;

/**
 * Superclass for all Exceptions in PicoContainer for lazy people
 * who want to catch only one Exception type.
 *
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public abstract class PicoException extends RuntimeException {
    private Throwable cause;

    protected PicoException() {
    }

    protected PicoException(String message) {
        super(message);
    }

    protected PicoException(Throwable cause) {
        this.cause = cause;
    }

    protected PicoException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    public Throwable getCause() {
        return cause;
    }
}
