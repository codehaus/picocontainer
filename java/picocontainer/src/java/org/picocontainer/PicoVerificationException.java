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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class PicoVerificationException extends PicoException {
    private List nestedExceptions = new ArrayList();

    public PicoVerificationException(List nestedExceptions) {
        this.nestedExceptions = nestedExceptions;
    }

    public List getNestedExceptions() {
        return nestedExceptions;
    }

    public String getMessage() {
        return nestedExceptions.toString();
    }
}
