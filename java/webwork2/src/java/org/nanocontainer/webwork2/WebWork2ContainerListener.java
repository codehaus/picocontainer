/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picoextras.webwork2;

import com.opensymphony.xwork.ActionProxyFactory;
import org.picoextras.servlet.ServletContainerListener;

import javax.servlet.ServletContextEvent;

/**
 * @author Chris Sturm
 */
public class WebWork2ContainerListener extends ServletContainerListener {
    public void contextInitialized(ServletContextEvent event) {
        ActionProxyFactory.setFactory(new PicoActionProxyFactory());
        super.contextInitialized(event);
    }
}
