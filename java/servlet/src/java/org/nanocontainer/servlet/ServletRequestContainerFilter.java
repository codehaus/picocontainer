/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Thomas Heller & Jacob Kjome                              *
 *****************************************************************************/
package org.picoextras.servlet;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author <a href="mailto:hoju@visi.com">Jacob Kjome</a>
 * @author Thomas Heller
 */
public class ServletRequestContainerFilter implements Filter {
	private ServletContext context;

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest  hrequest = (HttpServletRequest) request;
		HttpServletResponse hresponse = (HttpServletResponse) response;

		ServletRequestContainerLauncher launcher = new ServletRequestContainerLauncher(this.context, hrequest);
		try {
			launcher.startContainer();
			chain.doFilter(hrequest, hresponse);
		}
		finally {
			launcher.killContainer();
		}
	}

	public void init(FilterConfig config) throws ServletException {
		this.context = config.getServletContext();
	}

	public void destroy() {}

}
