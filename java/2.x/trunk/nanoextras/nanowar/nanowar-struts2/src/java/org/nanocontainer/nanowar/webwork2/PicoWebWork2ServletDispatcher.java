/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar.webwork2;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nanocontainer.nanowar.ServletRequestContainerLauncher;

import org.apache.struts2.views.JspSupportServlet;
import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.DefaultActionProxyFactory;

/**
 * Extension to the standard WebWork2 ServletDispatcher that instantiates 
 * a new container in the request scope for each request and disposes of it 
 * correctly at the end of the request.
 * <p/>
 * To use, replace the WebWork ServletDispatcher in web.xml with this.
 *
 * @author Joe Walnes
 */
public class PicoWebWork2ServletDispatcher extends JspSupportServlet { // was ServletDispatcher

    public PicoWebWork2ServletDispatcher() {
        super();
        ActionProxyFactory.setFactory(new PicoActionProxyFactory());
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        ServletRequestContainerLauncher containerLauncher = new ServletRequestContainerLauncher(getServletContext(), request);
        try {
            containerLauncher.startContainer();
            // process the servlet using webwork2
            super.service(request, response);
        } finally {
            containerLauncher.killContainer();
        }
    }
}
