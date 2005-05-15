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
public class UnmanagedInstanceException extends PicoException {
    private Object instance;

    public UnmanagedInstanceException(Object instance) {
        this.instance = instance;
    }

    public Object getInstance() {
        return instance;
    }
}
