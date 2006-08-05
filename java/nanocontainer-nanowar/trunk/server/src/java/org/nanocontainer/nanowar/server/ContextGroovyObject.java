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

import java.util.Map;

public class ContextGroovyObject extends NodeBuilder {
        private final PicoContextHandler context;

        public ContextGroovyObject(PicoContextHandler context) {
            this.context = context;
        }
        protected Object createNode(Object name, Map map) {
            if (name.equals("servlet")) {
                PicoServletHandler servlet = context.addServletWithMapping(
                        (Class) map.remove("class"),
                        (String) map.remove("path"));
                return servlet;
            }
            return null;
        }

    }