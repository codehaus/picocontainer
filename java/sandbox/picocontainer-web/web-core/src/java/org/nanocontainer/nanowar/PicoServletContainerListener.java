/*******************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.
 * --------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.nanocontainer.nanowar;

import java.io.Serializable;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.behaviors.Storing;

/**
 * Servlet listener class that hooks into the underlying servlet container and
 * instantiates, assembles, starts, stores and disposes the appropriate pico
 * containers when applications/sessions start/stop.
 * <p>
 * To use, simply add as a listener to the web.xml the listener-class
 * 
 * <pre>
 * &lt;listener&gt;
 *  &lt;listener-class&gt;org.nanocontainer.nanowar.PicoServletContainerListener&lt;/listener-class&gt;
 * &lt;/listener&gt; 
 * </pre>
 * 
 * </p>
 * <p>
 * The listener also requires a the class name of the
 * {@link org.nanocontainer.nanowar.WebappComposer} as a context-param in
 * web.xml:
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
public class PicoServletContainerListener implements ServletContextListener, HttpSessionListener, KeyConstants,
        Serializable {

    public static final String WEBAPP_COMPOSER_CLASS = "webapp-composer-class";
    private DefaultPicoContainer appContainer;
    private DefaultPicoContainer sessionContainer;
    private DefaultPicoContainer requestContainer;
    private Storing sessionStoring;
    private Storing requestStoring;

    public void contextInitialized(final ServletContextEvent event) {

        ServletContext context = event.getServletContext();
        appContainer = new DefaultPicoContainer(new Caching());
        appContainer.setName("application");

        context.setAttribute(ApplicationContainerHolder.class.getName(), new ApplicationContainerHolder(appContainer));

        sessionStoring = new Storing();
        sessionContainer = new DefaultPicoContainer(sessionStoring, appContainer);
        sessionContainer.setName("session");
        ThreadLocalLifecycleState sessionStateModel = new ThreadLocalLifecycleState();
        sessionContainer.setLifecycleState(sessionStateModel);

        context.setAttribute(SessionContainerHolder.class.getName(), new SessionContainerHolder(sessionContainer,
                sessionStoring, sessionStateModel));

        requestStoring = new Storing();
        requestContainer = new DefaultPicoContainer(requestStoring, sessionContainer);
        requestContainer.setName("request");
        ThreadLocalLifecycleState requestStateModel = new ThreadLocalLifecycleState();
        requestContainer.setLifecycleState(requestStateModel);

        context.setAttribute(RequestContainerHolder.class.getName(), new RequestContainerHolder(requestContainer,
                requestStoring, requestStateModel));

        compose(loadComposer(context));
        appContainer.start();
    }

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

    protected void compose(WebappComposer composer) {
        composer.application(appContainer);
        composer.session(sessionContainer);
        composer.request(requestContainer);
    }

    public void contextDestroyed(ServletContextEvent event) {
        appContainer.stop();
        appContainer.dispose();
    }

    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        ServletContext context = session.getServletContext();

        SessionContainerHolder sch = (SessionContainerHolder) context.getAttribute(SessionContainerHolder.class
                .getName());
        ThreadLocalLifecycleState tlLifecycleState = sch.getLifecycleStateModel();
        session.setAttribute(SessionStoreHolder.class.getName(), new SessionStoreHolder(sessionStoring
                .resetCacheForThread(), tlLifecycleState.resetStateModelForThread()));

        sessionContainer.start();
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        ServletContext context = session.getServletContext();

        SessionStoreHolder ssh = (SessionStoreHolder) session.getAttribute(SessionStoreHolder.class.getName());

        SessionContainerHolder sch = (SessionContainerHolder) context.getAttribute(SessionContainerHolder.class
                .getName());
        ThreadLocalLifecycleState tlLifecycleState = sch.getLifecycleStateModel();

        sessionStoring.putCacheForThread(ssh.getStoreWrapper());
        tlLifecycleState.putLifecycleStateModelForThread(ssh.getDefaultLifecycleState());

        sessionContainer.stop();
        sessionContainer.dispose();
        sessionStoring.invalidateCacheForThread();
    }

}
