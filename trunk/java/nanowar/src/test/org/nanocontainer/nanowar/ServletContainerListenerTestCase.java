/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.nanowar;

import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.util.Vector;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionEvent;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.nanocontainer.SoftCompositionPicoContainer;
import org.nanocontainer.reflection.DefaultSoftCompositionPicoContainer;
import org.nanocontainer.script.groovy.GroovyContainerBuilder;
import org.picocontainer.PicoContainer;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ServletContainerListenerTestCase extends MockObjectTestCase implements KeyConstants {

    private String groovyScript = 
            "builder = new org.nanocontainer.script.groovy.NanoGroovyBuilder()\n" +
            "pico = builder.container(parent:parent) { \n" +
            "  component(StringBuffer)\n" +
            "}";

    public void testApplicationScopeContainerIsCreatedWhenServletContextIsInitialisedWithInlinedScript() {
        ServletContainerListener listener = new ServletContainerListener();

        Mock servletContextMock = mock(ServletContext.class);
        final Vector initParams = new Vector();
        initParams.add("nanocontainer.groovy");
        servletContextMock.expects(once())
                .method("getInitParameterNames")
                .withNoArguments()
                .will(returnValue(initParams.elements()));
        servletContextMock.expects(once())
                .method("getInitParameter")
                .with(eq("nanocontainer.groovy"))
                .will(returnValue(groovyScript));
        servletContextMock.expects(once())
                .method("setAttribute")
                .with(eq(BUILDER), isA(GroovyContainerBuilder.class));
        servletContextMock.expects(once())
                .method("setAttribute")
                .with(eq(APPLICATION_CONTAINER), isA(PicoContainer.class));

        listener.contextInitialized(new ServletContextEvent((ServletContext) servletContextMock.proxy()));
    }

    public void testApplicationScopeContainerIsCreatedWhenServletContextIsInitialisedWithSeparateScript() {
        ServletContainerListener listener = new ServletContainerListener();

        Mock servletContextMock = mock(ServletContext.class);
        final Vector initParams = new Vector();
        initParams.add("nanocontainer.groovy");
        servletContextMock.expects(once())
                .method("getInitParameterNames")
                .withNoArguments()
                .will(returnValue(initParams.elements()));
        servletContextMock.expects(once())
                .method("getInitParameter")
                .with(eq("nanocontainer.groovy"))
                .will(returnValue("/config/nanocontainer.groovy"));
        servletContextMock.expects(once())
                .method("getResourceAsStream")
                .with(eq("/config/nanocontainer.groovy"))
                .will(returnValue(new StringBufferInputStream(groovyScript)));
        servletContextMock.expects(once())
                .method("setAttribute")
                .with(eq(BUILDER), isA(GroovyContainerBuilder.class));
        servletContextMock.expects(once())
                .method("setAttribute")
                .with(eq(APPLICATION_CONTAINER), isA(PicoContainer.class));

        listener.contextInitialized(new ServletContextEvent((ServletContext) servletContextMock.proxy()));
    }

    public void testSessionScopeContainerWithAppScopeContainerAsParentIsCreatedWhenServletContextIsInitialised() {
        ServletContainerListener listener = new ServletContainerListener();

        Mock httpSessionMock = mock(HttpSession.class);
        Mock servletContextMock = mock(ServletContext.class);
        httpSessionMock.expects(once())
                .method("getServletContext")
                .withNoArguments()
                .will(returnValue(servletContextMock.proxy()));
        httpSessionMock.expects(once())
                .method("setAttribute")
                .with(eq(ServletContainerListener.KILLER_HELPER), isA(HttpSessionBindingListener.class));
        SoftCompositionPicoContainer appScopeContainer = new DefaultSoftCompositionPicoContainer();
        servletContextMock.expects(once())
                .method("getAttribute")
                .with(eq(APPLICATION_CONTAINER))
                .will(returnValue(appScopeContainer));
        servletContextMock.expects(once())
                .method("getAttribute")
                .with(eq(BUILDER))
                .will(returnValue(new GroovyContainerBuilder(new StringReader(groovyScript), getClass().getClassLoader())));

        httpSessionMock.expects(once())
                .method("setAttribute")
                .with(eq(SESSION_CONTAINER), isA(PicoContainer.class));

        listener.sessionCreated(new HttpSessionEvent((HttpSession) httpSessionMock.proxy()));
    }

}
