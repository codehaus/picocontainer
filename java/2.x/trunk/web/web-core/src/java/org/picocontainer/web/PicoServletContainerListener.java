/*******************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.
 * --------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.web;

import java.io.Serializable;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.Filter;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.BehaviorFactory;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.lifecycle.StartableLifecycleStrategy;
import org.picocontainer.lifecycle.LifecycleState;
import org.picocontainer.lifecycle.DefaultLifecycleState;
import org.picocontainer.containers.EmptyPicoContainer;
import org.picocontainer.behaviors.Storing;
import org.picocontainer.behaviors.Guarding;
import org.picocontainer.behaviors.Caching;
import com.thoughtworks.xstream.XStream;

/**
 * Servlet listener class that hooks into the underlying servlet container and
 * instantiates, assembles, starts, stores and disposes the appropriate pico
 * containers when applications/sessions start/stop.
 * <p>
 * To use, simply add as a listener to the web.xml the listener-class
 * 
 * <pre>
 * &lt;listener&gt;
 *  &lt;listener-class&gt;org.picocontainer.web.PicoServletContainerListener&lt;/listener-class&gt;
 * &lt;/listener&gt; 
 * </pre>
 * 
 * </p>
 * <p>
 * The listener also requires a the class name of the
 * {@link org.picocontainer.web.WebappComposer} as a context-param in web.xml:
 * 
 * <pre>
 *  &lt;context-param&gt;
 *   &lt;param-name&gt;webapp-composer-class&lt;/param-name&gt;
 *   &lt;param-value&gt;com.company.MyWebappComposer&lt;/param-value&gt;
 *  &lt;/context-param&gt;
 * </pre>
 * 
 * The composer will be used to compose the components for the different webapp
 * scopes after the context has been initialised.
 * </p>
 * 
 * @author Joe Walnes
 * @author Aslak Helles&oslash;y
 * @author Philipp Meier
 * @author Paul Hammant
 * @author Mauro Talevi
 * @author Konstantin Pribluda
 */
@SuppressWarnings("serial")
public class PicoServletContainerListener implements ServletContextListener, HttpSessionListener, Serializable {

    public static final String WEBAPP_COMPOSER_CLASS = "webapp-composer-class";
    
    private ScopedContainers scopedContainers;

    /**
     * Default constructor used in webapp containers
     */
    public PicoServletContainerListener() {
    }

    public void contextInitialized(final ServletContextEvent event) {

        ServletContext context = event.getServletContext();

        scopedContainers = makeScopedContainers();

        scopedContainers.getApplicationContainer().setName("application");

        scopedContainers.getSessionContainer().setName("session");
        scopedContainers.getSessionContainer().setLifecycleState(scopedContainers.getSessionState());

        scopedContainers.getRequestContainer().setName("request");
        scopedContainers.getRequestContainer().setLifecycleState(scopedContainers.getRequestState());

        compose(loadComposer(context), context);

        scopedContainers.getApplicationContainer().start();

        context.setAttribute(ScopedContainers.class.getName(), scopedContainers);
    }

    /**
     * Overide this method if you need a more specialized container tree.
     * Here is the default block of code for this -
     *
     *   DefaultPicoContainer appCtnr = new DefaultPicoContainer(new Guarding().wrap(new Caching()), makeLifecycleStrategy(), makeParentContainer(), makeAppComponentMonitor());
     *   Storing sessStoring = new Storing();
     *   DefaultPicoContainer sessCtnr = new DefaultPicoContainer(new Guarding().wrap(sessStoring), makeLifecycleStrategy(), appCtnr, makeSessionComponentMonitor());
     *   Storing reqStoring = new Storing();
     *   DefaultPicoContainer reqCtnr = new DefaultPicoContainer(new Guarding().wrap(addRequestBehaviors(reqStoring)), makeLifecycleStrategy(), sessCtnr, makeRequestComponentMonitor());
     *   ThreadLocalLifecycleState sessionState = new ThreadLocalLifecycleState();
     *   ThreadLocalLifecycleState requestState = new ThreadLocalLifecycleState();
     *
     *   return new ScopedContainers(appCtnr, sessCtnr, reqCtnr, sessStoring, reqStoring, sessionState, requestState);
     * @return an instance of ScopedContainers
     */
    protected ScopedContainers makeScopedContainers() {
        DefaultPicoContainer appCtnr = new DefaultPicoContainer(new Guarding().wrap(new Caching()), makeLifecycleStrategy(), makeParentContainer(), makeAppComponentMonitor());
        Storing sessStoring = new Storing();
        DefaultPicoContainer sessCtnr = new DefaultPicoContainer(new Guarding().wrap(sessStoring), makeLifecycleStrategy(), appCtnr, makeSessionComponentMonitor());
        Storing reqStoring = new Storing();
        DefaultPicoContainer reqCtnr = new DefaultPicoContainer(new Guarding().wrap(addRequestBehaviors(reqStoring)), makeLifecycleStrategy(), sessCtnr, makeRequestComponentMonitor());
        ThreadLocalLifecycleState sessionState = new ThreadLocalLifecycleState();
        ThreadLocalLifecycleState requestState = new ThreadLocalLifecycleState();

        return new ScopedContainers(appCtnr, sessCtnr, reqCtnr, sessStoring, reqStoring, sessionState, requestState);
    }

    protected PicoContainer makeParentContainer() {
        return new EmptyPicoContainer();
    }

    protected LifecycleStrategy makeLifecycleStrategy() {
        return new StartableLifecycleStrategy(makeRequestComponentMonitor());
    }

    protected ComponentMonitor makeAppComponentMonitor() {
        return new NullComponentMonitor();
    }

    protected ComponentMonitor makeSessionComponentMonitor() {
        return new NullComponentMonitor();
    }

    protected ComponentMonitor makeRequestComponentMonitor() {
        return new NullComponentMonitor();
    }

    protected BehaviorFactory addRequestBehaviors(BehaviorFactory reqStoring) {
        return reqStoring;
    }

    /**
     * Get the class to do compostition with - from a "webapp-composer-class" config param
     * from web.xml :
     *
     *   <context-param>
     *       <param-name>webapp-composer-class</param-name>
     *       <param-value>com.yourcompany.YourWebappComposer</param-value>
     *   </context-param>
     *
     * @param context
     * @return
     */
    protected WebappComposer loadComposer(ServletContext context) {
        String composerClassName = context.getInitParameter(WEBAPP_COMPOSER_CLASS);
        try {
            return (WebappComposer) Thread.currentThread().getContextClassLoader().loadClass(composerClassName)
                    .newInstance();
        } catch (Exception e) {
            throw new PicoCompositionException("Failed to load webapp composer class " + composerClassName
                    + ": ensure the context-param '" + WEBAPP_COMPOSER_CLASS + "' is configured in the web.xml.", e);
        }
    }

    protected void compose(WebappComposer composer, ServletContext context) {
        composer.composeApplication(scopedContainers.getApplicationContainer(), context);
        composer.composeSession(scopedContainers.getSessionContainer());
        composer.composeRequest(scopedContainers.getRequestContainer());
    }

    public void contextDestroyed(ServletContextEvent event) {
        scopedContainers.getApplicationContainer().stop();
        scopedContainers.getApplicationContainer().dispose();
    }

    public void sessionCreated(HttpSessionEvent event) {

        HttpSession session = event.getSession();
        ServletContext context = session.getServletContext();

        SessionStoreHolder ssh = new SessionStoreHolder(scopedContainers.getSessionStoring().resetCacheForThread(), scopedContainers.getSessionState().resetStateModelForThread());

        scopedContainers.getSessionContainer().start();
        session.setAttribute(SessionStoreHolder.class.getName(), ssh);
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        ServletContext context = session.getServletContext();

        SessionStoreHolder ssh = (SessionStoreHolder) session.getAttribute(SessionStoreHolder.class.getName());

        scopedContainers.getSessionStoring().putCacheForThread(ssh.getStoreWrapper());
        scopedContainers.getSessionState().putLifecycleStateModelForThread(ssh.getLifecycleState());

        scopedContainers.getSessionContainer().stop();
        scopedContainers.getSessionContainer().dispose();

        scopedContainers.getSessionStoring().invalidateCacheForThread();
        scopedContainers.getSessionState().invalidateStateModelForThread();
        
        session.setAttribute(SessionStoreHolder.class.getName(), null);
    }

    public final static class ScopedContainers {

        private final MutablePicoContainer applicationContainer;
        private final MutablePicoContainer sessionContainer;
        private final MutablePicoContainer requestContainer;
        private final Storing sessionStoring;
        private final Storing requestStoring;
        private final ThreadLocalLifecycleState sessionState;
        private final ThreadLocalLifecycleState requestState;

        public ScopedContainers(MutablePicoContainer applicationContainer, MutablePicoContainer sessionContainer, MutablePicoContainer requestContainer, Storing sessionStoring, Storing requestStoring, ThreadLocalLifecycleState sessionState, ThreadLocalLifecycleState requestState) {
            this.applicationContainer = applicationContainer;
            this.sessionContainer = sessionContainer;
            this.requestContainer = requestContainer;
            this.sessionStoring = sessionStoring;
            this.requestStoring = requestStoring;
            this.sessionState = sessionState;
            this.requestState = requestState;
        }

        MutablePicoContainer getApplicationContainer() {
            return applicationContainer;
        }

        MutablePicoContainer getSessionContainer() {
            return sessionContainer;
        }

        MutablePicoContainer getRequestContainer() {
            return requestContainer;
        }

        Storing getSessionStoring() {
            return sessionStoring;
        }

        Storing getRequestStoring() {
            return requestStoring;

        }

        ThreadLocalLifecycleState getSessionState() {
            return sessionState;
        }

        ThreadLocalLifecycleState getRequestState() {
            return requestState;
        }
    }

}
