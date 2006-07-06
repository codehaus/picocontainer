/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *                                                                           *
 *****************************************************************************/

package org.nanocontainer.nanowar.server;

import groovy.util.NodeBuilder;
import org.picocontainer.MutablePicoContainer;

import java.util.Map;

public class WebContainerBuilder extends NodeBuilder {

    private final MutablePicoContainer parentContainer;

    public WebContainerBuilder(MutablePicoContainer parentContainer) {
        this.parentContainer = parentContainer;
    }

    protected Object createNode(Object name, Map map) {
        if (name.equals("webContainer")) {
            int port =0;
            if (map.containsKey("port")) {
                port = ((Integer) map.remove("port")).intValue();
            }
            String host;
            if (map.containsKey("host")) {
                host = (String) map.remove("host");
            } else {
                host = "localhost";
            }
            if (port != 0) {
                return new ServerGroovyObject(new JettyServerPicoEdition(host, port, parentContainer), parentContainer);
            } else {
                return new ServerGroovyObject(new JettyServerPicoEdition(parentContainer), parentContainer);
            }
        }
        return null;
    }



}


