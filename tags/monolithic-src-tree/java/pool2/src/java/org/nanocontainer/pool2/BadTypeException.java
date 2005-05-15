/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.pool2;

import org.picocontainer.PicoException;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class BadTypeException extends PicoException {
    private final Class expected;
    private final Class actual;

    public BadTypeException(Class expected, Class actual) {
        this.expected = expected;
        this.actual = actual;
    }

    public String getMessage() {
        return "Expected " + expected.getName() + ", but got " + actual.getName();
    }

    public Class getExpected() {
        return expected;
    }

    public Class getActual() {
        return actual;
    }
}
