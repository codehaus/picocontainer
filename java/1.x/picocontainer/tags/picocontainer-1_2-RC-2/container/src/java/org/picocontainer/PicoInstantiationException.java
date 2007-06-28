/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer;


/**
 * Subclass of {@link PicoException} that is thrown when there is a problem creating an instance of a container or some
 * other part of the PicoContainer api, for example, when an invocation through the reflection api fails.
 * 
 * @version $Revision$
 * @since 1.0
 * @deprecated since 1.2; it was actually not instantiated anywhere
 */
public class PicoInstantiationException extends PicoInitializationException {
    /**
     * Construct a new exception with the specified cause and the specified detail message.
     *
     * @param message the message detailing the exception.
     * @param cause   the exception that caused this one.
     * @deprecated since 1.2; it was actually not instantiated anywhere
     */
    protected PicoInstantiationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
