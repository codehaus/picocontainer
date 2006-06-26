/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Aslak Hellesoy and Paul Hammant                          *
 *****************************************************************************/

package org.nanocontainer.nanowar.server;

import groovy.util.BuilderSupport;
import groovy.util.NodeBuilder;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.Startable;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

public class WebContainerBuilder extends BuilderSupport {

    private final MutablePicoContainer parentContainer;

    public WebContainerBuilder(MutablePicoContainer toOperateOn) {
        this.parentContainer = toOperateOn;
    }

    protected void setParent(Object o, Object o1) {
        System.out.println("o ->" + o);
        System.out.println("o1 ->" + o1);
    }

    protected Object createNode(Object name) {
        return createNode(name, Collections.EMPTY_MAP);
    }

    protected Object createNode(Object name, Object value) {
        if (value instanceof Class) {
            Map attributes = new HashMap();
            attributes.put("class", value);
            return createNode(name, attributes);
        }
        return createNode(name);
    }

    protected Object createNode(Object name, Map map) {
        if (name.equals("webContainer")) {
            int port = ((Integer) map.remove("port")).intValue();
            JettyServerPicoEdition server = new JettyServerPicoEdition("localhost", port);
            return new ServerGroovyObject(server, parentContainer);
        } else if (name.equals("context")) {
            return null;
        }
        return null;
    }

    protected Object createNode(Object o, Map map, Object o1) {
        return new Object();
    }

    static class ServerGroovyObject extends NodeBuilder {
        JettyServerPicoEdition server;
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
                try {
                    server.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }


    }
    static class ContextGroovyObject extends NodeBuilder {
        private final ContextHandlerPicoEdition context;
        private final PicoContainer parentContainer;

        public ContextGroovyObject(ContextHandlerPicoEdition context, PicoContainer parentContainer) {
            this.context = context;
            this.parentContainer = parentContainer;
        }
        protected Object createNode(Object name, Map map) {
            if (name.equals("servlet")) {
                ServletHandlerPicoEdition servlet = context.addServletWithMapping(
                        (Class) map.remove("class"),
                        (String) map.remove("path"),
                        parentContainer);
                return servlet;
            }
            return null;
        }

    }

}


