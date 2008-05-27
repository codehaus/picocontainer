/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar.struts2;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.picocontainer.ObjectReference;
import org.nanocontainer.nanowar.NewFilter;

import com.opensymphony.xwork2.ObjectFactory;

/**
 * Filter which initialises a PicoObjectFactory as the XWork ObjectFactory
 * and passes to it the HttpServletRequest.
 * 
 * @author Jonas Engman
 */
public class PicoObjectFactoryFilter extends NewFilter {

	public void init(FilterConfig config) throws ServletException {
		ObjectFactory.setObjectFactory(new PicoObjectFactory());
	}

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
	    chain.doFilter(req, resp);
	}

	public void destroy() {
	}
}
