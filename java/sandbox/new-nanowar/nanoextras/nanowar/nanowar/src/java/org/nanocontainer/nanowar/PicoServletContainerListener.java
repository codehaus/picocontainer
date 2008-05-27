/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar;

import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.ObjectReference;
import org.picocontainer.references.SimpleReference;
import org.picocontainer.parameters.ConstantParameter;
import org.picocontainer.behaviors.Storing;
import org.picocontainer.behaviors.Caching;
import org.nanocontainer.integrationkit.ContainerPopulator;
import org.nanocontainer.integrationkit.ContainerBuilder;
import org.nanocontainer.NanoContainer;
import org.nanocontainer.DefaultNanoContainer;
import org.nanocontainer.ClassName;

import java.io.Serializable;
import java.io.Reader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSession;

/**
 * Servlet listener class that hooks into the underlying servlet
 * container and instantiates, assembles, starts, stores and
 * disposes the appropriate pico containers when
 * applications/sessions start/stop.
 * <p>
 * To use, simply add as a listener to web.xml the listener-class
 * <code>org.nanocontainer.nanowar.ServletContainerListener</code>.
 * </p>
 * <p>
 * The containers are configured via context-params in web.xml, in two ways:
 * <ol>
 * 	 <li>A NanoContainer script via a parameter whose name is nanocontainer.<language>,
 *       where <language> is one of the supported scripting languages,
 *       see {@link org.nanocontainer.script.ScriptedContainerBuilderFactory ScriptedContainerBuilderFactory}.
 *       The parameter value can be either an inlined script (enclosed in <![CDATA[]>), or a resource path for
 * 	  	 the script (relative to the webapp context).
 *   </li>
 *   <li>A ContainerComposer class via the parameter name
 *   {@link KeyConstants#CONTAINER_COMPOSER CONTAINER_COMPOSER},
 * 	 which can be configured via an optional parameter
 *   {@link KeyConstants#CONTAINER_COMPOSER_CONFIGURATION CONTAINER_COMPOSER_CONFIGURATION}.
 *   </li>
 * </ol>
 * </p>
 * <p>
 *  To allow external configurability of application (which is quite usefull in 
 *  and often required in big environments) you can add 2  containers to hierarchy
 *  <dl>
 *  	<dt>SystemPropertiesContainer</dt>
 *  	<dd>
 *  		System properties container exposes system properties
 *          obtainable through <code>System.getProperties()</code>
 *          to components in lower level containers so they have possibility
 *          to depend on them this allowing external configuration. To
 *          activate system properties you have to pass any value to 
 *          {@link KeyConstants#SYSTEM_PROPERTIES_CONTAINER SYSTEM_PROPERTIES_CONTAINER}
 *      </dd>
 *  </dl>
 * </p>
 * <p>
 * The application-level listeners can also be
 * configured separately in web.xml if one is interested in application-scoped components only or has 
 * issues with session-scoped components (serialization, clustering, etc).
 * </p>
 * @author Joe Walnes
 * @author Aslak Helles&oslash;y
 * @author Philipp Meier
 * @author Paul Hammant
 * @author Mauro Talevi
 * @author Konstantin Pribluda
 */
@SuppressWarnings("serial")
public class PicoServletContainerListener implements ServletContextListener, HttpSessionListener, KeyConstants, Serializable {

    protected DefaultPicoContainer appContainer;
    protected DefaultPicoContainer sessionContainer;
    protected DefaultPicoContainer requestContainer;
    private Storing sessionStoring;
    private Storing requestStoring;

    public void contextInitialized(final ServletContextEvent event) {
        ServletContext context = event.getServletContext();



        appContainer = new DefaultPicoContainer(new Caching());
        appContainer.setName("application");
        String builderClassName = context.getInitParameter("container-builder-class") ;

        //TODO - real resources
        //populateContainer("appResources", appContainer, null, builderClassName);

        context.setAttribute(ApplicationContainerHolder.class.getName(), new ApplicationContainerHolder(appContainer));

        sessionStoring = new Storing();
        sessionContainer = new DefaultPicoContainer(sessionStoring, appContainer);
        sessionContainer.setName("session");
        ThreadLocalLifecycleState sessionStateModel = new ThreadLocalLifecycleState();
        sessionContainer.setLifecycleState(sessionStateModel);

        //TODO - real resources
        // populateContainer("sessionResources", sessionContainer, appContainer, builderClassName);

        context.setAttribute(SessionContainerHolder.class.getName(), new SessionContainerHolder(sessionContainer, sessionStoring, sessionStateModel));

        requestStoring = new Storing();
        requestContainer = new DefaultPicoContainer(requestStoring, sessionContainer);
        requestContainer.setName("request");
        ThreadLocalLifecycleState requestStateModel = new ThreadLocalLifecycleState();
        requestContainer.setLifecycleState(requestStateModel);

        //TODO - real resources
        // populateContainer("requestResources", requestContainer, sessionContainer, builderClassName);

        context.setAttribute(RequestContainerHolder.class.getName(), new RequestContainerHolder(requestContainer, requestStoring, requestStateModel));

        appContainer.start();
    }

    public void contextDestroyed(ServletContextEvent event) {
        appContainer.stop();
        appContainer.dispose();
    }

    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        ServletContext context = session.getServletContext();

        SessionContainerHolder sch = (SessionContainerHolder) context.getAttribute(SessionContainerHolder.class.getName());
        ThreadLocalLifecycleState tlLifecycleState = sch.getLifecycleStateModel();
        session.setAttribute(SessionStoreHolder.class.getName(), new SessionStoreHolder(sessionStoring.resetCacheForThread(), tlLifecycleState.resetStateModelForThread()));

        sessionContainer.start();
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        ServletContext context = session.getServletContext();

        SessionStoreHolder ssh = (SessionStoreHolder) session.getAttribute(SessionStoreHolder.class.getName());

        SessionContainerHolder sch = (SessionContainerHolder) context.getAttribute(SessionContainerHolder.class.getName());
        ThreadLocalLifecycleState tlLifecycleState = sch.getLifecycleStateModel();

        sessionStoring.putCacheForThread(ssh.getStoreWrapper());
        tlLifecycleState.putLifecycleStateModelForThread(ssh.getDefaultLifecycleState());

        sessionContainer.stop();
        sessionContainer.dispose();
        sessionStoring.invalidateCacheForThread();
    }

}
