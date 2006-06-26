/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.nanocontainer.nanowar.server;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ContextHandler;

/**
 * @deprecated - to be replaced by forthcoming 'Jervlet' release
 */

public class JettyServerPicoEdition {

    private final Server server;

    public JettyServerPicoEdition(String host, int port) {
        server = new Server();
        server.setHandler(new HandlerList());
        SocketConnector connector = new SocketConnector();
        connector.setHost(host);
        connector.setPort(port);
        server.addConnector(connector);
    }

    public ContextHandlerPicoEdition createContext(String contextPath) {
        ContextHandler context = new ContextHandler();
        context.setContextPath(contextPath);
        server.addHandler(context);
        return new ContextHandlerPicoEdition(context, server);
    }

    public void start() {
        server.start();
    }
}
