/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picoextras.servlet;

import org.picocontainer.defaults.ObjectReference;
import org.picoextras.integrationkit.ContainerAssembler;
import org.picoextras.integrationkit.ContainerBuilder;
import org.picoextras.integrationkit.DefaultLifecycleContainerBuilder;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Servlet listener class that hooks into the underlying servlet
 * container and instantiates, assembles, starts, stores and
 * disposes the appropriate pico containers when
 * applications/sessions start/stop.
 *
 * To use, add this as a listener-class to web.xml.
 *
 * @author <a href="mailto:joe@thoughtworks.net">Joe Walnes</a>
 */
public class ServletContainerListener implements ServletContextListener, HttpSessionListener, KeyConstants {

    private ContainerBuilder containerBuilder;

    public void contextInitialized(ServletContextEvent event) {

        ServletContext context = event.getServletContext();
        ContainerAssembler assembler = loadAssembler(context);

        ObjectReference containerRef = new ApplicationScopeObjectReference(context, APPLICATION_CONTAINER);
        ObjectReference builderRef = new ApplicationScopeObjectReference(context, BUILDER);

        containerBuilder = new DefaultLifecycleContainerBuilder();
        builderRef.set(containerBuilder);

        containerBuilder.buildContainer(containerRef, null, assembler, context);
    }

    public void contextDestroyed(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        ObjectReference containerRef = new ApplicationScopeObjectReference(context, APPLICATION_CONTAINER);

        killContainer(containerRef);
    }

    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        ServletContext context = session.getServletContext();
        ContainerAssembler assembler = getAssembler(context);
        ObjectReference containerRef = new SessionScopeObjectReference(session, SESSION_CONTAINER);
        ObjectReference parentContainerRef = new ApplicationScopeObjectReference(context, APPLICATION_CONTAINER);

        containerBuilder.buildContainer(containerRef, parentContainerRef, assembler, session);
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        ObjectReference containerRef = new SessionScopeObjectReference(session, SESSION_CONTAINER);

        killContainer(containerRef);
    }

    private ContainerAssembler loadAssembler(ServletContext context) {
        ObjectReference assemblerRef = new ApplicationScopeObjectReference(context, ASSEMBLER);
        String className = context.getInitParameter("assembler");
        ContainerAssembler result = null;
        try {
            result = (ContainerAssembler) Class.forName(className).newInstance();
        } catch (Exception e) {
            // Don't use exception chaining to be JDK 1.3 compatible
            throw new RuntimeException("Cannot instanciate container assembler class. Root cause: " + e);
        }
        assemblerRef.set(result);
        return result;
    }

    private ContainerAssembler getAssembler(ServletContext context) {
        ObjectReference assemblerRef = new ApplicationScopeObjectReference(context, ASSEMBLER);
        return (ContainerAssembler) assemblerRef.get();
    }

    private void killContainer(ObjectReference containerRef) {
        if (containerRef.get() != null) {
            containerBuilder.killContainer(containerRef);
        }
    }
}
