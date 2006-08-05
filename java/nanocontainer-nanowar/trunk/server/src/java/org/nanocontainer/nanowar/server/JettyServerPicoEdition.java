/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.nanocontainer.nanowar.server;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.nio.BlockingChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.picocontainer.PicoException;
import org.picocontainer.PicoLifecycleException;
import org.picocontainer.PicoContainer;

/**
 * @deprecated - to be replaced by forthcoming 'Jervlet' release
 */
public class JettyServerPicoEdition {

    private final Server server;
    private final PicoContainer parentContainer;

    public JettyServerPicoEdition(PicoContainer parentContainer) {
        this.parentContainer = parentContainer;
        server = new Server();
        server.setHandler(new HandlerList());
    }

    public JettyServerPicoEdition(String host, int port, PicoContainer parentContainer) {
        this(parentContainer);
        createBlockingChannelConnector(host, port);
    }

    public Connector createBlockingChannelConnector(String host, int port) {
        BlockingChannelConnector connector = new BlockingChannelConnector();
        connector.setHost(host);
        connector.setPort(port);
        server.addConnector(connector);
        return connector;
    }

    public ContextHandlerPicoEdition createContext(String contextPath) {
        ContextHandler context = new ContextHandler();
        context.setContextPath(contextPath);
        server.addHandler(context);
        return new ContextHandlerPicoEdition(context, server, parentContainer);
    }


    public WebAppContextPicoEdition addWebApplication(String contextPath, String warFile) {
        WebAppContextPicoEdition wah = new WebAppContextPicoEdition(parentContainer);
        wah.setContextPath(contextPath);
        wah.setExtractWAR(true);
        wah.setWar(warFile);
        wah.setParentLoaderPriority(true);
        server.addHandler(wah);
        return wah;
    }


    public void start() {
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
            throw new JettyServerLifecycleException("Jetty couldn't start", e);
        }
    }

    public void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            throw new JettyServerLifecycleException("Jetty couldn't stop", e);
        }
    }
}
