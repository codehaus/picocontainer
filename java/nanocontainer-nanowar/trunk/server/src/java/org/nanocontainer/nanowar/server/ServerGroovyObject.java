package org.nanocontainer.nanowar.server;

import groovy.util.NodeBuilder;
import org.mortbay.jetty.Connector;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Startable;

import java.util.Map;

public class ServerGroovyObject extends NodeBuilder {
    private final JettyServerPicoEdition server;
    private final MutablePicoContainer parentContainer;

    public ServerGroovyObject(JettyServerPicoEdition server, MutablePicoContainer parentContainer) {
        this.server = server;
        this.parentContainer = parentContainer;
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
        return new ContextGroovyObject(context, parentContainer);
    }


}
