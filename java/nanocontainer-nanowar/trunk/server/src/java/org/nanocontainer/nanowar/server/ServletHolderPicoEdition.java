/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar.server;

import org.mortbay.jetty.servlet.ServletHolder;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

import javax.servlet.Servlet;

/**
 * @deprecated - to be replaced by forthcoming 'Jervlet' release
 */
public class ServletHolderPicoEdition extends ServletHolder {

    private final PicoContainer parentContainer;

    public ServletHolderPicoEdition(Class clazz, PicoContainer parentContainer) {
        super(clazz);
        this.parentContainer = parentContainer;
    }

    public synchronized Object newInstance() throws InstantiationException, IllegalAccessException {
        super.newInstance();
        DefaultPicoContainer child = new DefaultPicoContainer(parentContainer);
        child.registerComponentImplementation(Servlet.class, _class);
        return child.getComponentInstance(Servlet.class);
    }
}