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

import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.nanocontainer.SoftCompositionPicoContainer;
import org.nanocontainer.reflection.DefaultSoftCompositionPicoContainer;

/**
 * @author Mauro Talevi
 * @author Konstantin Pribluda ( konstantin.pribluda[at]infodesire.com )
 */
public class XMLContainerComposerTestCase extends MockObjectTestCase {

    public void testDefaultConfiguration() throws Exception {
        XMLContainerComposer composer = new XMLContainerComposer();

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

    public void testCustomConfiguration() throws Exception {
        Map config = new HashMap();
        config.put(XMLContainerComposer.APPLICATION_CONFIG_KEY,
                new String[]{"nano-application.xml","nano-application-2.xml"});
        config.put(XMLContainerComposer.SESSION_CONFIG_KEY,
                new String[]{"nano-session.xml","nano-session-2.xml"});
        config.put(XMLContainerComposer.REQUEST_CONFIG_KEY,
                new String[]{"nano-request.xml","nano-request-2.xml"});
        XMLContainerComposer composer = new XMLContainerComposer(config);

        SoftCompositionPicoContainer application = new DefaultSoftCompositionPicoContainer();
        Mock servletContextMock = mock(ServletContext.class);

        composer.composeContainer(application, servletContextMock.proxy());
        assertNotNull(application.getComponentInstance("applicationScopedInstance"));
        assertNotNull(application.getComponentInstance("applicationScopedInstance2"));

        SoftCompositionPicoContainer session = new DefaultSoftCompositionPicoContainer();
        Mock httpSessionMock = mock(HttpSession.class);
        composer.composeContainer(session, httpSessionMock.proxy());
        assertNotNull(session.getComponentInstance("sessionScopedInstance"));
        assertNotNull(session.getComponentInstance("sessionScopedInstance2"));

        SoftCompositionPicoContainer request = new DefaultSoftCompositionPicoContainer();
        Mock httpRequestMock = mock(HttpServletRequest.class);
        composer.composeContainer(request, httpRequestMock.proxy());
        assertNotNull(request.getComponentInstance("requestScopedInstance"));
        assertNotNull(request.getComponentInstance("requestScopedInstance2"));
    }

}
