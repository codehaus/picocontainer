/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picoextras.webwork;

import org.picoextras.servlet.ServletContainerListener;
import javax.servlet.ServletContextEvent;
import webwork.action.factory.ActionFactory;

/**
 * @author Aslak Helles&oslash;y
 */
public class WebWorkContainerListener extends ServletContainerListener {
    public void contextInitialized(ServletContextEvent event) {
        super.contextInitialized(event);
        ActionFactory.setActionFactory(new WebWorkActionFactory());
    }
}
