/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.nanocontainer.webcontainer;

/**
 */
public class JettyServerLifecycleException extends RuntimeException {
    public JettyServerLifecycleException(String string, Throwable throwable) {
        super(string, throwable);
    }
}
