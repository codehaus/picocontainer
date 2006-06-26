package org.nanocontainer.nanowar.server;

import groovy.util.NodeBuilder;
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
                    try {
                        ServerGroovyObject.this.server.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void stop() {
                }
            });
        }

        protected Object createNode(Object name, Map map) {
            if (name.equals("context")) {
                ContextHandlerPicoEdition context = server.createContext((String) map.remove("path"));
                return new ContextGroovyObject(context, parentContainer);
            } else if(name.equals("start")) {
                server.start();
            }
            return null;
        }


    }
