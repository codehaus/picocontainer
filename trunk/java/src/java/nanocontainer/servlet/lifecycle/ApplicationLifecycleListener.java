/*****************************************************************************
 * Copyright (Cc) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joe Walnes                                               *
 *****************************************************************************/


package nanocontainer.servlet.lifecycle;

import nanocontainer.servlet.holder.ApplicationScopeObjectHolder;
import nanocontainer.servlet.ObjectHolder;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContext;

import picocontainer.PicoContainer;

public class ApplicationLifecycleListener extends BaseLifecycleListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent event) {

        ServletContext context = event.getServletContext();

        // build a container
        PicoContainer container = getFactory(context).buildContainer("application");

        // and hold on to it
        ObjectHolder holder = new ApplicationScopeObjectHolder(context, CONTAINER_KEY);
        holder.put(container);

    }


    public void contextDestroyed(ServletContextEvent event) {
        ServletContext context = event.getServletContext();

        // shutdown container
        destroyContainer(context, new ApplicationScopeObjectHolder(context, CONTAINER_KEY));
    }

}

