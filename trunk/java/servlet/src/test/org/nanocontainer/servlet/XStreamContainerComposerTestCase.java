/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.servlet;

import junit.framework.TestCase;
import org.jmock.C;
import org.jmock.Mock;
import org.nanocontainer.script.groovy.GroovyContainerBuilder;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;


import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * test case for XStreamContainerComposer
 * @author Konstantin Pribluda ( konstantin[at]infodesire.com ) 
 * @version $Revision$
 */

public class XStreamContainerComposerTestCase extends TestCase implements KeyConstants {
	
	public void testThatProperConfigurationIsRead() throws Exception {
		XStreamContainerComposer composer = new XStreamContainerComposer();
		
		DefaultPicoContainer application = new DefaultPicoContainer();
		Mock servletContextMock = new Mock(ServletContext.class);
		
		composer.composeContainer(application,servletContextMock.proxy());
		
		assertNotNull(application.getComponentInstance("applicationScopedInstance"));
		
		DefaultPicoContainer session = new DefaultPicoContainer();
		Mock httpSessionMock = new Mock(HttpSession.class);
		
		composer.composeContainer(session,httpSessionMock.proxy());
		
		assertNotNull(session.getComponentInstance("sessionScopedInstance"));
		
		DefaultPicoContainer request = new DefaultPicoContainer();
		Mock httpRequestMock = new Mock(HttpServletRequest.class);
		
		composer.composeContainer(request,httpRequestMock.proxy());
		
		assertNotNull(request.getComponentInstance("requestScopedInstance"));
	}
}

