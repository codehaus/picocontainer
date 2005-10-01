/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.Enumeration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.nanocontainer.DefaultNanoContainer;
import org.nanocontainer.NanoContainer;
import org.nanocontainer.integrationkit.ContainerBuilder;
import org.nanocontainer.integrationkit.ContainerComposer;
import org.nanocontainer.integrationkit.DefaultLifecycleContainerBuilder;
import org.nanocontainer.integrationkit.PicoCompositionException;
import org.nanocontainer.script.ScriptedContainerBuilder;
import org.nanocontainer.script.ScriptedContainerBuilderFactory;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ConstantParameter;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;

/**
 * Servlet listener class that hooks into the underlying servlet
 * container and instantiates, assembles, starts, stores and
 * disposes the appropriate pico containers when
 * applications/sessions start/stop.
 * <p/>
 * To use, add this as a listener-class to web.xml.
 * The containers are configured via context-params in web.xml, in two ways: 
 * <ol>
 * 	 <li>A NanoContainer script via a parameter whose name is nanocontainer.<language>, 
 *       where <language> is one of the supported scripting languages,
 *       see {@link ScriptedContainerBuilderFactory}.  The parameter value can be
 * 	     either an inlined script (enclosed in <![CDATA[]>), or a resource path for 
 * 	  	 the script.
 *   </li>
 *   <li>A ContainerComposer class via the parameter name 
 *   {@link ServletContainerListener#CONTAINER_COMPOSER CONTAINER_COMPOSER},
 * 	 which can be configured via an optional parameter 
 *   {@link ServletContainerListener#CONTAINER_COMPOSER_CONFIGURATION CONTAINER_COMPOSER_CONFIGURATION}.
 *   </li>
 * </ol>
 *
 * @author Joe Walnes
 * @author Aslak Helles&oslash;y
 * @author Philipp Meier
 * @author Paul Hammant
 * @author Mauro Talevi
 * @author Konstantin Pribluda
 */
public class ServletContainerListener implements ServletContextListener, HttpSessionListener, KeyConstants, Serializable {

    public static final String KILLER_HELPER = "KILLER_HELPER";
    public static final String NANOCONTAINER_PREFIX = "nanocontainer";
    public static final String CONTAINER_COMPOSER = ContainerComposer.class.getName();
    public static final String CONTAINER_COMPOSER_CONFIGURATION = CONTAINER_COMPOSER + ".configuration";
    
    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        try {
            ContainerBuilder containerBuilder = createBuilder(context);

            ObjectReference builderRef = new ApplicationScopeObjectReference(context, BUILDER);
            builderRef.set(containerBuilder);

            ObjectReference containerRef = new ApplicationScopeObjectReference(context, APPLICATION_CONTAINER);
            containerBuilder.buildContainer(containerRef, new SimpleReference(), context, false);
        } catch (ClassNotFoundException e) {
            // Not all servlet containers print the nested exception. Do it here.
            event.getServletContext().log(e.getMessage(), e);
            throw new PicoCompositionException(e);
        }
    }

    private ContainerBuilder createBuilder(ServletContext context) throws ClassNotFoundException {
        Enumeration initParameters = context.getInitParameterNames();
        while (initParameters.hasMoreElements()) {
            String initParameter = (String) initParameters.nextElement();
            if (initParameter.startsWith(NANOCONTAINER_PREFIX)) {
                String builderClassName = getBuilderClassName(initParameter);
                String script = context.getInitParameter(initParameter);
                Reader scriptReader;
                if (script.trim().startsWith("/") && !(script.trim().startsWith("//") || script.trim().startsWith("/*"))) {
                    // the script isn't inlined, but in a separate file.
                    scriptReader = new InputStreamReader(context.getResourceAsStream(script));
                } else {
                    scriptReader = new StringReader(script);
                }
                ScriptedContainerBuilderFactory scriptedContainerBuilderFactory = new ScriptedContainerBuilderFactory(scriptReader, builderClassName, Thread.currentThread().getContextClassLoader());
                return scriptedContainerBuilderFactory.getContainerBuilder();
            }
            if (initParameter.equals(CONTAINER_COMPOSER)) {
                ContainerComposer containerComposer = createContainerComposer(context);
                return new DefaultLifecycleContainerBuilder(containerComposer);
            }
        }
        throw new PicoCompositionException("Couldn't create a builder from context parameters in web.xml");
    }

    private ContainerComposer createContainerComposer(ServletContext context) throws ClassNotFoundException{
        String containerComposerClassName = context.getInitParameter(CONTAINER_COMPOSER);
        // disposable container used to instantiate the ContainerComposer
        NanoContainer nanoContainer = new DefaultNanoContainer(Thread.currentThread().getContextClassLoader());
        String script = context.getInitParameter(CONTAINER_COMPOSER_CONFIGURATION);
        PicoContainer picoConfiguration = null;
        if ( script != null ){
            Reader scriptReader = new InputStreamReader(context.getResourceAsStream(script));
            String builderClassName = getBuilderClassName(script);
            ScriptedContainerBuilderFactory scriptedContainerBuilderFactory = new ScriptedContainerBuilderFactory(scriptReader, builderClassName, Thread.currentThread().getContextClassLoader());
            picoConfiguration = buildContainer(scriptedContainerBuilderFactory.getContainerBuilder());
        }
        ComponentAdapter componentAdapter = null;
        if ( picoConfiguration != null ){
            Parameter[] parameters = new Parameter[]{ new ConstantParameter(picoConfiguration) };
            componentAdapter = nanoContainer.registerComponentImplementation(containerComposerClassName, containerComposerClassName, parameters);
        } else {
            componentAdapter = nanoContainer.registerComponentImplementation(containerComposerClassName);            
        }
        return (ContainerComposer) componentAdapter.getComponentInstance(nanoContainer.getPico());        
    }

    private String getBuilderClassName(String scriptName){
        String extension = scriptName.substring(scriptName.lastIndexOf('.'));
        return ScriptedContainerBuilderFactory.getBuilderClassName(extension);
    }
    
    protected PicoContainer buildContainer(ScriptedContainerBuilder builder) {
        ObjectReference containerRef = new SimpleReference();
        builder.buildContainer(containerRef, new SimpleReference(), new SimpleReference(), false);
        return (PicoContainer) containerRef.get();
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
        containerBuilder.buildContainer(sessionContainerRef, webappContainerRef, session, false);

        session.setAttribute(KILLER_HELPER, new ContainerKillerHelper() {
            public void valueBound(HttpSessionBindingEvent bindingEvent) {
                HttpSession session = bindingEvent.getSession();
                containerRef = new SimpleReference();
                containerRef.set(new SessionScopeObjectReference(session, SESSION_CONTAINER).get());
            }

            public void valueUnbound(HttpSessionBindingEvent event) {
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
        ContainerBuilder containerKiller = new DefaultLifecycleContainerBuilder(null);
        if (containerRef.get() != null) {
            containerKiller.killContainer(containerRef);
        }
    }

    private abstract class ContainerKillerHelper implements HttpSessionBindingListener, Serializable {
        SimpleReference containerRef;
    }
}
