/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picoextras.servlet;

import org.picoextras.integrationkit.ContainerAssembler;
import org.picoextras.integrationkit.ContainerBuilder;
import org.picoextras.integrationkit.ObjectReference;
import org.picoextras.servlet.ApplicationScopeObjectReference;
import org.picoextras.servlet.KeyConstants;
import org.picoextras.servlet.RequestScopeObjectReference;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author <a href="mailto:joe@thoughtworks.net">Joe Walnes</a>
 */
public class ServletRequestContainerLauncher {

    private ContainerBuilder containerBuilder;
    private ContainerAssembler assembler;
    private ObjectReference containerRef;
    private HttpServletRequest request;

    public ServletRequestContainerLauncher(ServletContext context, HttpServletRequest request) {

        ObjectReference builderRef = new ApplicationScopeObjectReference(context, KeyConstants.BUILDER);
        ObjectReference assemblerRef = new ApplicationScopeObjectReference(context, KeyConstants.ASSEMBLER);

        assembler = (ContainerAssembler) assemblerRef.get();
        containerRef = new RequestScopeObjectReference(request, KeyConstants.REQUEST_CONTAINER);
        containerBuilder = (ContainerBuilder) builderRef.get();
        this.request = request;

    }

    public void startContainer() throws ServletException {

        HttpSession session = request.getSession(true);

        ObjectReference parentContainerRef = new SessionScopeObjectReference(session, KeyConstants.SESSION_CONTAINER);

        if (assembler == null || containerBuilder == null) {
            throw new ServletException("org.picoextras.servlet.ServletContainerListener not deployed");
        }

        containerBuilder.buildContainer(containerRef, parentContainerRef, assembler, "request");

    }

    public void killContainer() {
        containerBuilder.killContainer(containerRef);
    }
}
