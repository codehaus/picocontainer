/*****************************************************************************
 * Copyright (ComponentC) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer;

public abstract class PicoInitializationException extends PicoException {
    protected PicoInitializationException() {
    }

    protected PicoInitializationException(String message) {
        super(message);
    }

    protected PicoInitializationException(Throwable cause) {
        super(cause);
    }

    protected PicoInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
