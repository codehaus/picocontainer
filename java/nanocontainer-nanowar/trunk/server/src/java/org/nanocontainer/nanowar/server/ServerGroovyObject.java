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

public class ServerGroovyObject extends NodeBuilder {
    private final JettyServerPicoEdition server;

    public ServerGroovyObject(JettyServerPicoEdition server, MutablePicoContainer parentContainer) {
        this.server = server;
        parentContainer.registerComponentInstance(new Startable() {
            public void start() {
                ServerGroovyObject.this.server.start();
            }

            public void stop() {
                ServerGroovyObject.this.server.stop();
            }
        });
    }

    protected Object createNode(Object name, Map map) {
        if (name.equals("context")) {
            return createContext(map);
        } else if (name.equals("blockingChannelConnector")) {
            return createBlockingChannelConnector(map);
        }
        return null;
    }

    private Object createBlockingChannelConnector(Map map) {
        int port = ((Integer) map.remove("port")).intValue();
        Connector connector = server.createBlockingChannelConnector((String) map.remove("host"), port);
        return connector;
    }

    private Object createContext(Map map) {
        ContextHandlerPicoEdition context = server.createContext((String) map.remove("path"));
        return new ContextGroovyObject(context);
    }


}
