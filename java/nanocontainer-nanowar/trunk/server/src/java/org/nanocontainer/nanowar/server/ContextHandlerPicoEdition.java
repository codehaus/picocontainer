/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.nanocontainer.nanowar.server;

import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.Server;
import org.picocontainer.PicoContainer;

/**
 * @deprecated - to be replaced by forthcoming 'Jervlet' release
 */
public class ContextHandlerPicoEdition {

    private final ContextHandler context;
    private final Server server;
    private final PicoContainer parentContainer;
    ServletHandlerPicoEdition handler;

    public static final int DEFAULT=0;
    public static final int REQUEST=1;
    public static final int FORWARD=2;
    public static final int INCLUDE=4;
    public static final int ERROR=8;
    public static final int ALL=15;    

    public ContextHandlerPicoEdition(ContextHandler context, Server server, PicoContainer parentContainer) {
        this.context = context;
        this.server = server;
        this.parentContainer = parentContainer;
    }

    public ServletHandlerPicoEdition addServletWithMapping(Class servletClass, String pathMapping) {
        ServletHandlerPicoEdition handler = getHandler();
        handler.addServletWithMapping(servletClass, pathMapping);
        return handler;
    }

    private synchronized ServletHandlerPicoEdition getHandler() {
        if (handler == null) {
            handler = new ServletHandlerPicoEdition(parentContainer);
            context.addHandler(handler);
            handler.setServer(server);
        }
        return handler;
    }

    public ServletHandlerPicoEdition addFilterWithMapping(Class filterClass, String pathMapping, int dispatchers) {
        ServletHandlerPicoEdition handler = getHandler();
        handler.addFilterWithMapping(filterClass, pathMapping, dispatchers);
        return handler;

    }
}
