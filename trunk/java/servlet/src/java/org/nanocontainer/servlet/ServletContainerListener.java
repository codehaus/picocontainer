/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.servlet;

import org.nanocontainer.integrationkit.ContainerBuilder;
import org.nanocontainer.integrationkit.ContainerComposer;
import org.nanocontainer.integrationkit.DefaultLifecycleContainerBuilder;
import org.nanocontainer.integrationkit.PicoAssemblyException;
import org.nanocontainer.script.ScriptedComposingLifecycleContainerBuilder;
import org.picocontainer.defaults.ObjectReference;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
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
 */
public class ServletContainerListener implements ServletContextListener, HttpSessionListener, KeyConstants {
    public static final String KILLER_HELPER = "KILLER_HELPER";
    private final ContainerBuilder containerKiller = new DefaultLifecycleContainerBuilder(null);

    public void contextInitialized(ServletContextEvent event) {

        ServletContext context = event.getServletContext();
        try {
            ContainerBuilder containerBuilder = createBuilder(context);

            ObjectReference builderRef = new ApplicationScopeObjectReference(context, BUILDER);
            builderRef.set(containerBuilder);

            ObjectReference containerRef = new ApplicationScopeObjectReference(context, APPLICATION_CONTAINER);
            containerBuilder.buildContainer(containerRef, null, context);
        } catch (ClassNotFoundException e) {
            throw new PicoAssemblyException(e);
        } catch (IllegalAccessException e) {
            throw new PicoAssemblyException(e);
        } catch (InstantiationException e) {
            throw new PicoAssemblyException(e);
        }
    }

    private ContainerBuilder createBuilder(ServletContext context) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Enumeration initParameters = context.getInitParameterNames();
        while (initParameters.hasMoreElements()) {
            String initParameter = (String) initParameters.nextElement();
            if(initParameter.startsWith("nanocontainer")) {
                String extension = initParameter.substring(initParameter.lastIndexOf('.') + 1);
                String script = context.getInitParameter(initParameter);
                return ScriptedComposingLifecycleContainerBuilder.createBuilder(extension, new StringReader(script), Thread.currentThread().getContextClassLoader());
            }
            if(initParameter.equals(ContainerComposer.class.getName())) {
                String containerComposerClassName = context.getInitParameter(initParameter);
                ContainerComposer containerComposer = (ContainerComposer) Thread.currentThread().getContextClassLoader().loadClass(containerComposerClassName).newInstance();
                return new DefaultLifecycleContainerBuilder(containerComposer);
            }
        }
        throw new PicoAssemblyException("Couldn't create a builder from context parameters in web.xml");
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
        ObjectReference containerRef = new SessionScopeObjectReference(session, SESSION_CONTAINER);
        ObjectReference parentContainerRef = new ApplicationScopeObjectReference(context, APPLICATION_CONTAINER);
        containerBuilder.buildContainer(containerRef, parentContainerRef, session);

        session.setAttribute(KILLER_HELPER, new HttpSessionBindingListener() {
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

    private ContainerBuilder getBuilder(ServletContext context) {
        ObjectReference assemblerRef = new ApplicationScopeObjectReference(context, BUILDER);
        return (ContainerBuilder) assemblerRef.get();
    }

//    private ContainerComposer loadAssembler(ServletContext context) {
//        ObjectReference assemblerRef = new ApplicationScopeObjectReference(context, BUILDER);
//        //TODO Is this right? It still feel like a mouthful.
//        String className = context.getInitParameter("picoextras-servlet-container-composer");
//        ContainerComposer result = null;
//        try {
//            result = (ContainerComposer) Class.forName(className, true, Thread.currentThread().getContextClassLoader()).newInstance();
//        } catch (Exception e) {
//            // Don't use exception chaining to be JDK 1.3 compatible
//            //TODO Use a PicoException derived exception (Paul)?
//            throw new RuntimeException("Cannot instanciate container assembler class. Root cause: " + e);
//        }
//        assemblerRef.set(result);
//        return result;
//    }
//
//    private ContainerComposer getAssembler(ServletContext context) {
//        ObjectReference assemblerRef = new ApplicationScopeObjectReference(context, ASSEMBLER);
//        return (ContainerComposer) assemblerRef.get();
//    }

    private void killContainer(ObjectReference containerRef) {
        if (containerRef.get() != null) {
            containerKiller.killContainer(containerRef);
        }
    }
}
