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
import org.picocontainer.defaults.DefaultPicoContainer;
import org.nanocontainer.SoftCompositionPicoContainer;
import org.nanocontainer.nanowar.KeyConstants;
import org.nanocontainer.nanowar.XStreamContainerComposer;
import org.nanocontainer.reflection.DefaultSoftCompositionPicoContainer;

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

        SoftCompositionPicoContainer application = new DefaultSoftCompositionPicoContainer();
        Mock servletContextMock = mock(ServletContext.class);

        composer.composeContainer(application, servletContextMock.proxy());

        assertNotNull(application.getComponentInstance("applicationScopedInstance"));

        SoftCompositionPicoContainer session = new DefaultSoftCompositionPicoContainer();
        Mock httpSessionMock = mock(HttpSession.class);

        composer.composeContainer(session, httpSessionMock.proxy());

        assertNotNull(session.getComponentInstance("sessionScopedInstance"));

        SoftCompositionPicoContainer request = new DefaultSoftCompositionPicoContainer();
        Mock httpRequestMock = mock(HttpServletRequest.class);

        composer.composeContainer(request, httpRequestMock.proxy());

        assertNotNull(request.getComponentInstance("requestScopedInstance"));
    }
}

