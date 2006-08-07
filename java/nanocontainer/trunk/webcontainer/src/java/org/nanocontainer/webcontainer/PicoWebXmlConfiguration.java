/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.nanocontainer.webcontainer;

import org.mortbay.jetty.webapp.WebXmlConfiguration;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.servlet.FilterHolder;
import org.picocontainer.PicoContainer;
import org.nanocontainer.webcontainer.PicoFilterHolder;
import org.nanocontainer.webcontainer.PicoServletHolder;

public class PicoWebXmlConfiguration extends WebXmlConfiguration {

    private PicoContainer parentContainer;

    public PicoWebXmlConfiguration(PicoContainer parentContainer) {
        this.parentContainer = parentContainer;
    }

    protected ServletHolder newServletHolder() {
        return new PicoServletHolder(parentContainer);
    }

    protected FilterHolder newFilterHolder() {
        return new PicoFilterHolder(parentContainer);
    }

}
