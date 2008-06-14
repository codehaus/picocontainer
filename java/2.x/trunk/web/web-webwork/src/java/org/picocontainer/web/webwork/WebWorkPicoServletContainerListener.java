/*******************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved. 
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.web.webwork;

import javax.servlet.ServletContextEvent;

import org.picocontainer.web.PicoServletContainerListener;

import webwork.action.factory.ActionFactory;

@SuppressWarnings("serial")
public class WebWorkPicoServletContainerListener extends PicoServletContainerListener {

    public void contextInitialized(ServletContextEvent event) {
        super.contextInitialized(event);
        ActionFactory.setActionFactory(new WebWorkActionFactory());
    }
}
