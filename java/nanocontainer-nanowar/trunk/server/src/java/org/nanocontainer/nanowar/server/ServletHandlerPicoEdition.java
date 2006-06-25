/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.nanocontainer.nanowar.server;

import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;
import org.picocontainer.PicoContainer;

/**
 * @deprecated - to be replaced by forthcoming 'Jervlet' release
 */
public class ServletHandlerPicoEdition extends ServletHandler {

    private final PicoContainer parentContainer;

    public ServletHandlerPicoEdition(PicoContainer parentContainer) {
        this.parentContainer = parentContainer;
    }
    public ServletHolder newServletHolder(Class clazz) {
        return new ServletHolderPicoEdition(clazz, parentContainer);
    }
}