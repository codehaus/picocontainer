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
import org.picoextras.integrationkit.ContainerBuilder;
import org.picoextras.integrationkit.DefaultLifecycleContainerBuilder;
import org.picoextras.integrationkit.ContainerComposer;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

/**
 * Servlet listener class that hooks into the underlying servlet
 * container and instantiates, assembles, starts, stores and
 * disposes the appropriate pico containers when
 * applications/sessions start/stop.
 * <p/>
 * To use, add this as a listener-class to web.xml.
 *
 * @author <a href="mailto:joe@thoughtworks.net">Joe Walnes</a>
 */
public class ServletContainerListener implements ServletContextListener, HttpSessionListener, KeyConstants {
    private final ContainerBuilder containerKiller = new DefaultLifecycleContainerBuilder(null);

    public void contextInitialized(ServletContextEvent event) {

        ServletContext context = event.getServletContext();
        ContainerComposer assembler = loadAssembler(context);

        ObjectReference builderRef = new ApplicationScopeObjectReference(context, BUILDER);
        ContainerBuilder containerBuilder = new DefaultLifecycleContainerBuilder(assembler);
        builderRef.set(containerBuilder);

        ObjectReference containerRef = new ApplicationScopeObjectReference(context, APPLICATION_CONTAINER);
        containerBuilder.buildContainer(containerRef, null, context);
    }

    public void contextDestroyed(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        ObjectReference containerRef = new ApplicationScopeObjectReference(context, APPLICATION_CONTAINER);
        killContainer(containerRef);
    }

    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        ServletContext context = session.getServletContext();
        ContainerComposer assembler = getAssembler(context);
        ObjectReference containerRef = new SessionScopeObjectReference(session, SESSION_CONTAINER);
        ObjectReference parentContainerRef = new ApplicationScopeObjectReference(context, APPLICATION_CONTAINER);

        ContainerBuilder containerBuilder = new DefaultLifecycleContainerBuilder(assembler);
        containerBuilder.buildContainer(containerRef, parentContainerRef, session);

        session.setAttribute("picoextras session container killer helper", new HttpSessionBindingListener() {
            public void valueBound(HttpSessionBindingEvent bindingEvent) {
            }

            public void valueUnbound(HttpSessionBindingEvent event) {
                HttpSession session = event.getSession();
                ObjectReference containerRef = new SessionScopeObjectReference(session, SESSION_CONTAINER);
                killContainer(containerRef);
            }
        });

    }

    public void sessionDestroyed(HttpSessionEvent event) {
    }

    private ContainerComposer loadAssembler(ServletContext context) {
        ObjectReference assemblerRef = new ApplicationScopeObjectReference(context, ASSEMBLER);
        //TODO Is this right? It still feel like a mouthful.
        String className = context.getInitParameter("picoextras-servlet-container-composer");
        ContainerComposer result = null;
        try {
            result = (ContainerComposer) Class.forName(className, true, Thread.currentThread().getContextClassLoader()).newInstance();
        } catch (Exception e) {
            // Don't use exception chaining to be JDK 1.3 compatible
            //TODO Use a PicoException derived exception (Paul)?
            throw new RuntimeException("Cannot instanciate container assembler class. Root cause: " + e);
        }
        assemblerRef.set(result);
        return result;
    }

    private ContainerComposer getAssembler(ServletContext context) {
        ObjectReference assemblerRef = new ApplicationScopeObjectReference(context, ASSEMBLER);
        return (ContainerComposer) assemblerRef.get();
    }

    private void killContainer(ObjectReference containerRef) {
        if (containerRef.get() != null) {
            containerKiller.killContainer(containerRef);
        }
    }
}
