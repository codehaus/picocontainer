/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.servlet;

import org.nanocontainer.NanoContainer;
import org.nanocontainer.integrationkit.ContainerBuilder;
import org.nanocontainer.integrationkit.ContainerComposer;
import org.nanocontainer.integrationkit.DefaultLifecycleContainerBuilder;
import org.nanocontainer.integrationkit.PicoCompositionException;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.Enumeration;

/**
 * Servlet listener class that hooks into the underlying servlet
 * container and instantiates, assembles, starts, stores and
 * disposes the appropriate pico containers when
 * applications/sessions start/stop.
 * <p/>
 * To use, add this as a listener-class to web.xml.
 *
 * @author <a href="mailto:joe@thoughtworks.net">Joe Walnes</a>
 * @author Aslak Helles&oslash;y
 * @author Philipp Meier
 */
public class ServletContainerListener implements ServletContextListener, HttpSessionListener, KeyConstants, Serializable {
    private transient ContainerBuilder containerKiller = new DefaultLifecycleContainerBuilder(null);

    public static final String KILLER_HELPER = "KILLER_HELPER";

    public void contextInitialized(ServletContextEvent event) {

        ServletContext context = event.getServletContext();
        try {
            ContainerBuilder containerBuilder = createBuilder(context);

            ObjectReference builderRef = new ApplicationScopeObjectReference(context, BUILDER);
            builderRef.set(containerBuilder);

            ObjectReference containerRef = new ApplicationScopeObjectReference(context, APPLICATION_CONTAINER);
            containerBuilder.buildContainer(containerRef, new SimpleReference(), context);
        } catch (Exception e) {
            // Not all servlet containers print the nested exception. Do it here.
            event.getServletContext().log(e.getMessage(), e);
            throw new PicoCompositionException(e);
        }
    }

    private ContainerBuilder createBuilder(ServletContext context) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Enumeration initParameters = context.getInitParameterNames();
        while (initParameters.hasMoreElements()) {
            String initParameter = (String) initParameters.nextElement();
            if(initParameter.startsWith("nanocontainer")) {
                String extension = initParameter.substring(initParameter.lastIndexOf('.'));
                String builderClassName = NanoContainer.getBuilderClassName(extension);
                String script = context.getInitParameter(initParameter);
                Reader scriptReader;
                if(script.trim().startsWith("/") && !(script.trim().startsWith("//") || script.trim().startsWith("/*"))) {
                    // the script isn't inlined, but in a separate file.
                    scriptReader = new InputStreamReader(context.getResourceAsStream(script));
                } else {
                    scriptReader = new StringReader(script);
                }
                NanoContainer nanoContainer = new NanoContainer(scriptReader, builderClassName, Thread.currentThread().getContextClassLoader());
                return nanoContainer.getContainerBuilder();
            }
            if(initParameter.equals(ContainerComposer.class.getName())) {
                String containerComposerClassName = context.getInitParameter(initParameter);
                ContainerComposer containerComposer = (ContainerComposer) Thread.currentThread().getContextClassLoader().loadClass(containerComposerClassName).newInstance();
                return new DefaultLifecycleContainerBuilder(containerComposer);
            }
        }
        throw new PicoCompositionException("Couldn't create a builder from context parameters in web.xml");
    }

    public void contextDestroyed(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        ObjectReference containerRef = new ApplicationScopeObjectReference(context, APPLICATION_CONTAINER);
        killContainer(containerRef);
    }

    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        ServletContext context = session.getServletContext();
        ContainerBuilder containerBuilder = getBuilder(context);
        ObjectReference sessionContainerRef = new SessionScopeObjectReference(session, SESSION_CONTAINER);
        ObjectReference webappContainerRef = new ApplicationScopeObjectReference(context, APPLICATION_CONTAINER);
        containerBuilder.buildContainer(sessionContainerRef, webappContainerRef, session);

        session.setAttribute(KILLER_HELPER, new ContainerKillerHelper() {
            public void valueBound(HttpSessionBindingEvent bindingEvent) {
            }

            public void valueUnbound(HttpSessionBindingEvent event) {
                HttpSession session = event.getSession();
                ObjectReference containerRef = new SessionScopeObjectReference(session, SESSION_CONTAINER);
                try {
                    killContainer(containerRef);
                } catch (IllegalStateException e) {
                    //
                    //Some servlet containers (Jetty) call contextDestroyed(ServletContextEvent event)
                    //and then afterwards call valueUnbound(HttpSessionBindingEvent event).

                    //contextDestroyed will kill the top level (app level) pico container which will
                    //cascade stop() down to the session children.

                    //This means that when valueUnbound is called later, the session level container will
                    //already be stopped.
                    //
                }
            }
        });

    }

    public void sessionDestroyed(HttpSessionEvent event) {
    }

    private ContainerBuilder getBuilder(ServletContext context) {
        ObjectReference assemblerRef = new ApplicationScopeObjectReference(context, BUILDER);
        return (ContainerBuilder) assemblerRef.get();
    }

    private void killContainer(ObjectReference containerRef) {
        if (containerRef.get() != null) {
            containerKiller.killContainer(containerRef);
        }
    }

    private abstract class ContainerKillerHelper implements HttpSessionBindingListener, Serializable {}
}
