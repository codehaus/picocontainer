/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer;

public class PicoInstantiationException extends PicoInitializationException {

    public PicoInstantiationException(String message) {
        super(message);
    }

    public PicoInstantiationException(String message, Throwable cause) {
        super(message, cause);
    }
}
