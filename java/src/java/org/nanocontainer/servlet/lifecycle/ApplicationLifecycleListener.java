/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joe Walnes                                               *
 *****************************************************************************/


package org.nanocontainer.servlet.lifecycle;

import org.nanocontainer.servlet.holder.ApplicationScopeObjectHolder;
import org.nanocontainer.servlet.ObjectHolder;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContext;

import org.picocontainer.PicoContainer;
import org.picocontainer.internals.ComponentRegistry;
import org.picocontainer.defaults.DefaultComponentRegistry;

public class ApplicationLifecycleListener extends BaseLifecycleListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent event) {

        ServletContext context = event.getServletContext();

        // Store comp reg for application level internals.
        ComponentRegistry reg = new DefaultComponentRegistry();
        ObjectHolder regHolder = new ApplicationScopeObjectHolder(context, COMPONENT_REGISTRY_KEY);
        regHolder.put(reg);
        
        // build a internals
        PicoContainer container = getFactory(context).buildContainer("application", reg);

        // and hold on to it
        ObjectHolder holder = new ApplicationScopeObjectHolder(context, CONTAINER_KEY);
        holder.put(container);

    }


    public void contextDestroyed(ServletContextEvent event) {
        ServletContext context = event.getServletContext();

        // shutdown internals
        destroyContainer(context, new ApplicationScopeObjectHolder(context, CONTAINER_KEY));
    }

}