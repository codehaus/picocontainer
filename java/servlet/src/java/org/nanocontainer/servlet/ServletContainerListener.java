/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picoextras.servlet;

import org.picoextras.integrationkit.ContainerAssembler;
import org.picoextras.integrationkit.ContainerBuilder;
import org.picoextras.integrationkit.DefaultLifecycleContainerBuilder;
import org.picoextras.integrationkit.ObjectReference;
import org.picoextras.servlet.ApplicationScopeObjectReference;
import org.picoextras.servlet.KeyConstants;

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

        containerBuilder.buildContainer(containerRef, null, assembler, "application");
    }

    public void contextDestroyed(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        ObjectReference containerRef = new ApplicationScopeObjectReference(context, APPLICATION_CONTAINER);

        containerBuilder.killContainer(containerRef);
    }

    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        ServletContext context = session.getServletContext();
        ContainerAssembler assembler = getAssembler(context);
        ObjectReference containerRef = new SessionScopeObjectReference(session, SESSION_CONTAINER);
        ObjectReference parentContainerRef = new ApplicationScopeObjectReference(context, APPLICATION_CONTAINER);

        containerBuilder.buildContainer(containerRef, parentContainerRef, assembler, "session");
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        ObjectReference containerRef = new SessionScopeObjectReference(session, SESSION_CONTAINER);

        containerBuilder.killContainer(containerRef);
    }

    private ContainerAssembler loadAssembler(ServletContext context) {
        try {
            ObjectReference assemblerRef = new ApplicationScopeObjectReference(context, ASSEMBLER);
            String className = context.getInitParameter("assembler");
            ContainerAssembler result = (ContainerAssembler) Class.forName(className).newInstance();
            assemblerRef.set(result);
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e); // todo: what should be thrown?
        }
    }

    private ContainerAssembler getAssembler(ServletContext context) {
        ObjectReference assemblerRef = new ApplicationScopeObjectReference(context, ASSEMBLER);
        return (ContainerAssembler) assemblerRef.get();
    }
}
