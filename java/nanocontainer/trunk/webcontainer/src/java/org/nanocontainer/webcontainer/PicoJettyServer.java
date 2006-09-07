/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.nanocontainer.webcontainer;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.RequestLog;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.RequestLogHandler;
import org.mortbay.jetty.handler.ErrorHandler;
import org.mortbay.jetty.nio.BlockingChannelConnector;
import org.picocontainer.PicoContainer;
import org.picocontainer.alternatives.EmptyPicoContainer;

public class PicoJettyServer extends EmptyPicoContainer implements PicoContainer {

    private final Server server;
    private final PicoContainer parentContainer;
    private ErrorHandler errorHandler;

    public PicoJettyServer(PicoContainer parentContainer) {
        this.parentContainer = parentContainer;
        server = new Server();
        server.setHandler(new HandlerList());
    }

    public PicoJettyServer(String host, int port, PicoContainer parentContainer) {
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

    public PicoContextHandler createContext(String contextPath, boolean withSessionHandler) {
        ContextHandler context = new ContextHandler();
        context.setContextPath(contextPath);
        server.addHandler(context);
        return new PicoContextHandler(context, server, parentContainer, withSessionHandler);
    }


    public PicoWebAppContext addWebApplication(String contextPath, String warFile) {
        PicoWebAppContext wah = new PicoWebAppContext(parentContainer);
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

    public void addRequestLog(RequestLog requestLog) {
        RequestLogHandler requestLogHandler = new RequestLogHandler();
        requestLogHandler.setRequestLog(requestLog);
        server.addHandler(requestLogHandler);

    }

    //public void setErrorHandler() {
    //    setErrorHandler(new WebAppContext.WebAppErrorHandler());
    //}

    public void setErrorHandler(ErrorHandler errorHandler) {
        if (errorHandler!=null) {
            errorHandler.setServer(server);
        }
        server.getContainer().update(this, null, errorHandler, "errorHandler");
        this.errorHandler = errorHandler;
    }


}
