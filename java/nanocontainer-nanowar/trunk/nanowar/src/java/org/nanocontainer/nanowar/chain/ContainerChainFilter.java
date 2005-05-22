/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Konstantin Pribluda                                      *
 *****************************************************************************/
package org.nanocontainer.nanowar.chain;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nanocontainer.nanowar.KeyConstants;
import org.nanocontainer.nanowar.RequestScopeObjectReference;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * filter building chain of servlet containers. builds container chain based on
 * servlet path. chain is wired to request container and substituted with
 * request scoped reference so nobody notices additional containers. chain is
 * started after creation and stopped after request processing. on end of
 * request processing chain is removed and reference to original request
 * container is established again.
 * 
 * @author Konstantin Pribluda
 * @version $Revision:$
 */
public class ContainerChainFilter implements Filter {
	/**
	 * used to prevent chain creation when already processing.
	 */
	private final static String ALREADY_FILTERED_KEY = "nanocontainer_chain_filter_already_filtered";

	private final static String FAILURE_URL = "failureUrl";

	private ServletContext context;

	String redirectUrl;

	ServletChainBuilder chainBuilder;

	public void init(FilterConfig config) throws ServletException {

		if (config.getInitParameter(FAILURE_URL) != null) {
			redirectUrl = config.getInitParameter(FAILURE_URL);
		}
		chainBuilder = new ServletChainBuilder(config.getServletContext());
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest hrequest = (HttpServletRequest) request;
		HttpServletResponse hresponse = (HttpServletResponse) response;

		if (hrequest.getAttribute(ALREADY_FILTERED_KEY) == null) {
			// we were not here, filter
			hrequest.setAttribute(ALREADY_FILTERED_KEY, Boolean.TRUE);
			// obtain pico container for chaining
			PicoContainer container = (PicoContainer) ((new RequestScopeObjectReference(
					hrequest, KeyConstants.REQUEST_CONTAINER)).get());
			try {
				String originalUrl = hrequest.getServletPath();
				System.err.println("filterinng " + originalUrl);

				ArrayList elements = new ArrayList();

				elements.add("/");
				for (int pos = originalUrl.indexOf("/", 1); pos > 0; pos = originalUrl
						.indexOf("/", pos + 1)) {
					elements.add(originalUrl.substring(0, pos + 1));
					System.err.println("adding: " + originalUrl.substring(0, pos + 1));
				}

				ContainerChain chain = chainBuilder.buildChain(elements
						.toArray(), container);
				chain.start();
				// inject chain
				(new RequestScopeObjectReference(request,
						KeyConstants.REQUEST_CONTAINER)).set(chain.getLast());

				filterChain.doFilter(request, response);
				chain.stop();
			} catch (Exception ex) {
				ex.printStackTrace();
				if (redirectUrl != null) {

					// if we got an exception,we create fake container
					DefaultPicoContainer dpc = new DefaultPicoContainer(
							container);
					dpc.registerComponentInstance("cause", ex.getCause());
					// wire container to request
					(new RequestScopeObjectReference(request,
							KeyConstants.REQUEST_CONTAINER)).set(dpc);

					// and transfer us to this url
					request.getRequestDispatcher(redirectUrl).forward(request,
							response);
				} else {
					// if there is no configured redirect url, we just 
					// wrap in srvlet exception and rethrow this. 
					throw new ServletException(ex);
				}

			} finally {
				// restore old contaier head
				(new RequestScopeObjectReference(request,
						KeyConstants.REQUEST_CONTAINER)).set(container);
			}

		}
	}

	public void destroy() {

	}

}