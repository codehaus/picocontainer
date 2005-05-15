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

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.nanocontainer.script.ScriptedContainerBuilder;
import org.nanocontainer.script.ScriptedContainerBuilderFactory;
import org.nanocontainer.script.xml.XMLContainerBuilder;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Reader;
import java.io.StringReader;

/**
 * @author Mauro Talevi
 * @author Konstantin Pribluda ( konstantin.pribluda[at]infodesire.com )
 */
public class ScopedContainerComposerTestCase extends MockObjectTestCase {

    public void testDefaultConfiguration() throws ClassNotFoundException {
        ScopedContainerComposer composer = new ScopedContainerComposer();

        MutablePicoContainer applicationContainer = new DefaultPicoContainer();
        Mock servletContextMock = mock(ServletContext.class);

        composer.composeContainer(applicationContainer, servletContextMock.proxy());
        assertNotNull(applicationContainer.getComponentInstance("applicationScopedInstance"));

        MutablePicoContainer sessionContainer = new DefaultPicoContainer(applicationContainer);
        Mock httpSessionMock = mock(HttpSession.class);
        composer.composeContainer(sessionContainer, httpSessionMock.proxy());
        assertNotNull(sessionContainer.getComponentInstance("applicationScopedInstance"));
        assertNotNull(sessionContainer.getComponentInstance("sessionScopedInstance"));

        MutablePicoContainer requestContainer = new DefaultPicoContainer(sessionContainer);
        Mock httpRequestMock = mock(HttpServletRequest.class);
        composer.composeContainer(requestContainer, httpRequestMock.proxy());
        assertNotNull(requestContainer.getComponentInstance("applicationScopedInstance"));
        assertNotNull(requestContainer.getComponentInstance("sessionScopedInstance"));
        assertNotNull(requestContainer.getComponentInstance("requestScopedInstance"));
    }

    public void testCustomConfiguration() throws ClassNotFoundException {
        PicoContainer pico = createPicoContainerWithConfiguredComponents();
        ScopedContainerConfigurator configurator = (ScopedContainerConfigurator)pico.getComponentInstance(ScopedContainerConfigurator.class);
        assertNotNull("configurator", configurator);
        ScopedContainerComposer composer = new ScopedContainerComposer(pico);

        MutablePicoContainer applicationContainer = new DefaultPicoContainer();
        Mock servletContextMock = mock(ServletContext.class);

        composer.composeContainer(applicationContainer, servletContextMock.proxy());
        assertNotNull(applicationContainer.getComponentInstance("applicationScopedInstance"));
        assertNotNull(applicationContainer.getComponentInstance("applicationScopedInstance2"));

        MutablePicoContainer sessionContainer = new DefaultPicoContainer(applicationContainer);
        Mock httpSessionMock = mock(HttpSession.class);
        composer.composeContainer(sessionContainer, httpSessionMock.proxy());
        assertNotNull(sessionContainer.getComponentInstance("applicationScopedInstance"));
        assertNotNull(sessionContainer.getComponentInstance("applicationScopedInstance2"));
        assertNotNull(sessionContainer.getComponentInstance("sessionScopedInstance"));
        assertNotNull(sessionContainer.getComponentInstance("sessionScopedInstance2"));

        MutablePicoContainer requestContainer = new DefaultPicoContainer(sessionContainer);
        Mock httpRequestMock = mock(HttpServletRequest.class);
        composer.composeContainer(requestContainer, httpRequestMock.proxy());
        assertNotNull(requestContainer.getComponentInstance("applicationScopedInstance"));
        assertNotNull(requestContainer.getComponentInstance("applicationScopedInstance2"));
        assertNotNull(requestContainer.getComponentInstance("sessionScopedInstance"));
        assertNotNull(requestContainer.getComponentInstance("sessionScopedInstance2"));
        assertNotNull(requestContainer.getComponentInstance("requestScopedInstance"));
        assertNotNull(requestContainer.getComponentInstance("requestScopedInstance2"));
    }

    private PicoContainer createPicoContainerWithConfiguredComponents() throws ClassNotFoundException{
        Reader scriptReader = new StringReader("" +
                "<container>" +
                "<component-implementation class='org.nanocontainer.nanowar.ScopedContainerConfigurator'>"+
                "	   <parameter><string>org.nanocontainer.script.xml.XMLContainerBuilder</string></parameter>"+
                "      <parameter><string>nanowar-application.xml,nanowar/application.xml</string></parameter> "+
                "      <parameter><string>nanowar-session.xml,nanowar/session.xml</string></parameter>        "+
                "      <parameter><string>nanowar-request.xml,nanowar/request.xml</string></parameter> "+
                "</component-implementation>" +    						
                "</container>");
        String builderClassName = XMLContainerBuilder.class.getName();
        ScriptedContainerBuilderFactory scriptedContainerBuilderFactory = new ScriptedContainerBuilderFactory(scriptReader, builderClassName, Thread.currentThread().getContextClassLoader());
        return buildContainer(scriptedContainerBuilderFactory.getContainerBuilder());        
    }

    private PicoContainer buildContainer(ScriptedContainerBuilder builder) {
        ObjectReference containerRef = new SimpleReference();
        builder.buildContainer(containerRef, new SimpleReference(), new SimpleReference(), false);
        return (PicoContainer) containerRef.get();
    }
        
}