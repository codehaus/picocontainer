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

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.picocontainer.defaults.DefaultPicoContainer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * test case for XStreamContainerComposer
 *
 * @author Konstantin Pribluda ( konstantin.pribluda[at]infodesire.com )
 * @version $Revision$
 */
public class XStreamContainerComposerTestCase extends MockObjectTestCase implements KeyConstants {

    public void testThatProperConfigurationIsRead() throws Exception {
        XStreamContainerComposer composer = new XStreamContainerComposer();

        DefaultPicoContainer application = new DefaultPicoContainer();
        Mock servletContextMock = mock(ServletContext.class);

        composer.composeContainer(application, servletContextMock.proxy());

        assertNotNull(application.getComponentInstance("applicationScopedInstance"));

        DefaultPicoContainer session = new DefaultPicoContainer();
        Mock httpSessionMock = mock(HttpSession.class);

        composer.composeContainer(session, httpSessionMock.proxy());

        assertNotNull(session.getComponentInstance("sessionScopedInstance"));

        DefaultPicoContainer request = new DefaultPicoContainer();
        Mock httpRequestMock = mock(HttpServletRequest.class);

        composer.composeContainer(request, httpRequestMock.proxy());

        assertNotNull(request.getComponentInstance("requestScopedInstance"));
    }
}

