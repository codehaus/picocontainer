/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.nanocontainer.nanowar.server;

import groovy.util.NodeBuilder;
import org.mortbay.jetty.Connector;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Startable;

import java.util.Map;

public class ServerBuilder extends NodeBuilder {
    private final PicoJettyServer server;

    public ServerBuilder(PicoJettyServer server, MutablePicoContainer parentContainer) {
        this.server = server;
        parentContainer.registerComponentInstance(new Startable() {
            public void start() {
                ServerBuilder.this.server.start();
            }
            public void stop() {
                ServerBuilder.this.server.stop();
            }
        });
    }

    protected Object createNode(Object name, Map map) {
        if (name.equals("context")) {
            return createContext(map);
        } else if (name.equals("blockingChannelConnector")) {
            return createBlockingChannelConnector(map);
        } else if(name.equals("xmlWebApplication")) {
            return createXmlWebApplication(map);
        }
        return null;
    }

    protected Object createBlockingChannelConnector(Map map) {
        int port = ((Integer) map.remove("port")).intValue();
        Connector connector = server.createBlockingChannelConnector((String) map.remove("host"), port);
        return connector;
    }

    protected Object createContext(Map map) {
        PicoContextHandler context = server.createContext((String) map.remove("path"));
        return new ContextBuilder(context);
    }

    protected Object createXmlWebApplication(Map map) {
        PicoWebAppContext context = server.addWebApplication((String) map.remove("path"), (String) map.remove("warfile") );
        return new WarFileBuilder(context);
    }

}
