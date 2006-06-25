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
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * @deprecated - to be replaced by forthcoming 'Jervlet' release
 */

public class ContextHandlerPicoEdition {

    private final ContextHandler context;
    private final Server server;

    public ContextHandlerPicoEdition(ContextHandler context, Server server) {
        this.context = context;
        this.server = server;
    }

    public ServletHandlerPicoEdition addServletWithMapping(Class servletClass, String pathMapping,
                                      DefaultPicoContainer parentContainer) {
        ServletHandlerPicoEdition handler = new ServletHandlerPicoEdition(parentContainer);
        handler.addServletWithMapping(servletClass, pathMapping);
        context.setHandler(handler);

        // this is a problem - need 'add' rather than 'set' here
        handler.setServer(server);
        return handler;
    }
}