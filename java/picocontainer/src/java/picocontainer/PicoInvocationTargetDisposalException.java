/*****************************************************************************
 * Copyright (C) ClassRegistrationPicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package picocontainer;

public class PicoInvocationTargetDisposalException extends PicoDisposalException {
    private final Throwable cause;

    public PicoInvocationTargetDisposalException(Throwable cause) {
        if (cause == null) {
            throw new IllegalArgumentException("Cause must not be null");
        }
        this.cause = cause;
    }

    public Throwable getCause() {
        return cause;
    }

    public String getMessage() {
        return "InvocationTargetException: "
                + cause.getClass().getName()
                + " " + cause.getMessage();
    }
}
