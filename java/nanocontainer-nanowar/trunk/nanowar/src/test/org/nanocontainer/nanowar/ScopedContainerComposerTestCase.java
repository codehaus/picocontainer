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

import java.io.StringReader;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.nanocontainer.script.ScriptedContainerBuilder;
import org.nanocontainer.script.ScriptedContainerBuilderFactory;
import org.nanocontainer.script.groovy.GroovyContainerBuilder;
import org.nanocontainer.script.xml.XMLContainerBuilder;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;

/**
 * @author Mauro Talevi
 * @author Konstantin Pribluda ( konstantin.pribluda[at]infodesire.com )
 */
public class ScopedContainerComposerTestCase extends MockObjectTestCase {

    public void testCompositionWithInvalidScope() throws ClassNotFoundException {
        ScopedContainerComposer composer = new ScopedContainerComposer();
        MutablePicoContainer applicationContainer = new DefaultPicoContainer();
        composer.composeContainer(applicationContainer, "invalid-scope");
        assertNull(applicationContainer.getComponentInstance("applicationScopedInstance"));
    }
    
    public void testComposedHierarchyWithDefaultConfiguration() throws ClassNotFoundException {
        assertComposedHierarchy(new ScopedContainerComposer());
    }
    
    public void testComposedHierarchyWithCustomConfiguration() throws ClassNotFoundException {
        String groovyConfig =
            "pico = builder.container(parent:parent, scope:assemblyScope) {\n" +
            "   component(class:'org.nanocontainer.nanowar.ScopedContainerConfigurator', \n"+
            "             parameters:['org.nanocontainer.script.groovy.GroovyContainerBuilder', " +
            "                         'nanowar-application.groovy', " +
            "                         'nanowar-session.groovy', " +
            "                         'nanowar-request.groovy' ])\n" +
            "}";
        assertComposedHierarchy(new ScopedContainerComposer(createConfigurationContainer(groovyConfig, GroovyContainerBuilder.class)));
        String xmlConfig = 
            "<container>" +
            "<component-implementation class='org.nanocontainer.nanowar.ScopedContainerConfigurator'>"+
            "      <parameter><string>org.nanocontainer.script.xml.XMLContainerBuilder</string></parameter>"+
            "      <parameter><string>nanowar-application.xml</string></parameter> "+
            "      <parameter><string>nanowar-session.xml</string></parameter>        "+
            "      <parameter><string>nanowar-request.xml</string></parameter> "+
            "</component-implementation>" +                         
            "</container>";
        assertComposedHierarchy(new ScopedContainerComposer(createConfigurationContainer(xmlConfig, XMLContainerBuilder.class)));
    }
    
    private void assertComposedHierarchy(ScopedContainerComposer composer) throws ClassNotFoundException {
        MutablePicoContainer applicationContainer = new DefaultPicoContainer();
        Mock servletContextMock = mock(ServletContext.class);

        composer.composeContainer(applicationContainer, servletContextMock.proxy());
        assertNotNull(applicationContainer.getComponentInstance("applicationScopedInstance"));
        assertNotNull(applicationContainer.getComponentInstance("testFoo"));

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
        assertNotNull(requestContainer.getComponentInstance("testFooHierarchy"));
    }
    

    private PicoContainer createConfigurationContainer(String script, Class containerBuilder) throws ClassNotFoundException{
        ScriptedContainerBuilderFactory scriptedContainerBuilderFactory = new ScriptedContainerBuilderFactory(
                new StringReader(script), containerBuilder.getName(), Thread.currentThread().getContextClassLoader());
        return buildContainer(scriptedContainerBuilderFactory.getContainerBuilder());        
    }
    
    private PicoContainer buildContainer(ScriptedContainerBuilder builder) {
        ObjectReference containerRef = new SimpleReference();
        builder.buildContainer(containerRef, new SimpleReference(), new SimpleReference(), false);
        return (PicoContainer) containerRef.get();
    }
        
}