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
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public class PicoIntrospectionException extends PicoRegistrationException {
    public PicoIntrospectionException() {
    }

    protected PicoIntrospectionException(String message) {
        super(message);
    }

    public PicoIntrospectionException(Throwable cause) {
        super(cause);
    }

    public PicoIntrospectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
