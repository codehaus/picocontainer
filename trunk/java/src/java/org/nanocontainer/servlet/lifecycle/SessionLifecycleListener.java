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
import org.nanocontainer.servlet.holder.SessionScopeObjectHolder;
import org.nanocontainer.servlet.ObjectHolder;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletContext;

import org.picocontainer.PicoContainer;

public class SessionLifecycleListener extends BaseLifecycleListener implements HttpSessionListener {

    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        ServletContext context = session.getServletContext();

        // grab the parent container
        ObjectHolder parentHolder = new ApplicationScopeObjectHolder(context, CONTAINER_KEY);
        PicoContainer parentContainer = (PicoContainer) parentHolder.get();

        // build a container
        PicoContainer container = getFactory(context).buildContainerWithParent(parentContainer, "session");

        // and hold on to it
        ObjectHolder holder = new SessionScopeObjectHolder(session, CONTAINER_KEY);
        holder.put(container);
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        ServletContext context = session.getServletContext();

        // shutdown container
        destroyContainer(context, new SessionScopeObjectHolder(session, CONTAINER_KEY));
    }

}

