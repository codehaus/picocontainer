/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.nanocontainer.webcontainer.groovy;

import groovy.util.NodeBuilder;
import org.mortbay.jetty.Connector;
import org.nanocontainer.webcontainer.PicoContextHandler;
import org.nanocontainer.webcontainer.PicoJettyServer;
import org.nanocontainer.webcontainer.PicoWebAppContext;
import org.nanocontainer.script.groovy.NodeCreator;
import org.picocontainer.MutablePicoContainer;

import java.util.Map;

public class ServerBuilder extends NodeBuilder {
    private final PicoJettyServer server;
    private final MutablePicoContainer parentContainer;

    public ServerBuilder(PicoJettyServer server, MutablePicoContainer parentContainer) {
        this.server = server;
        this.parentContainer = parentContainer;
    }

    protected Object createNode(Object name, Map map) {
        if (name.equals("context")) {
            return createContext(map);
        } else if (name.equals("blockingChannelConnector")) {
            return createBlockingChannelConnector(map);
        } else if (name.equals("xmlWebApplication")) {
            return createXmlWebApplication(map);
        }
        return null;
    }

    protected Object createBlockingChannelConnector(Map map) {
        int port = ((Integer) map.remove("port")).intValue();
        return server.createBlockingChannelConnector((String) map.remove("host"), port);
    }

    protected Object createContext(Map map) {
        boolean sessions = false;
        if (map.containsKey("sessions")) {
            sessions = Boolean.valueOf((String) map.remove("sessions")).booleanValue();
        }
        PicoContextHandler context = server.createContext((String) map.remove("path"), sessions);
        return new ContextBuilder(parentContainer, context);
    }

    protected Object createXmlWebApplication(Map map) {
        PicoWebAppContext context = server.addWebApplication((String) map.remove("path"), (String) map.remove("warfile"));
        return new WarFileBuilder(parentContainer, context);
    }

}
