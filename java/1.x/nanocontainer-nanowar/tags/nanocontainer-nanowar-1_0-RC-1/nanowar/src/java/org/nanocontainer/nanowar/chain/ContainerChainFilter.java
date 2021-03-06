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
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.nanocontainer.nanowar.KeyConstants;
import org.nanocontainer.nanowar.RequestScopeObjectReference;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ObjectReference;

/**
 * <p>
 * Filter for building chain of servlet containers, based on servlet path.
 * </p>
 * 
 * <p>Chain is wired to request container and substituted with request scoped 
 * reference so nobody notices additional containers. 
 * </p>
 * 
 * <p>Chain is started after creation and stopped after request processing.
 * </p>
 * 
 * <p>At end of request processing, the chain is removed and reference to original request
 * container is established again.
 * </p>
 * 
 * <p>The filter requires the following mandatory init params:
 * <ul>
 *  <li>builderClassName:  specifies the name of the ContainerBuilder to use, eg XMLContainerBuilder</li>
 *  <li>containerScriptName:  specifies the container script name found in the paths, eg nano.xml</li>
 *  <li>emptyContainerScript:  specifies the empty container script (as a String) to use if the path container script is not found</li>
 * </ul>
 * </p>
 *
 * <p>The filter accepts the following optional init params:
 * <ul>
 *  <li>chainMonitor:  specifies the name of the ChainMonitor to use, eg ConsoleChainMonitorr</li>
 *  <li>failureUrl:  specifies the URL to redirect to in case of failure</li>
 * </ul>
 * </p>
 * 
 * @author Konstantin Pribluda
 * @author Mauro Talevi
 */
public class ContainerChainFilter implements Filter {

    /** The init param name for the chain monitor */
    public static final String CHAIN_MONITOR_PARAM = "chainMonitor";
    /** The init param name for the failure url */
    public final static String FAILURE_URL_PARAM = "failureUrl";
    /** The init param name for the builder class name */
    public static final String BUILDER_CLASSNAME_PARAM = "builderClassName";
    /** The init param name for the container script name */
    public static final String CONTAINER_SCRIPT_NAME_PARAM = "containerScriptName";
    /** The init param name for the empty container script */
    public static final String EMPTY_CONTAINER_SCRIPT_PARAM = "emptyContainerScript";

    /** Key used to prevent chain creation when already processing */
	private final static String ALREADY_FILTERED_KEY = "nanocontainer_chain_filter_already_filtered";
    /** The path separator */
    private static final String PATH_SEPARATOR = "/";
    
	private String failureUrl;
	private ServletChainBuilder chainBuilder;
    private ChainMonitor monitor;

    /**
     * @see Filter#init(javax.servlet.FilterConfig)
     */
	public void init(FilterConfig config) throws ServletException {
        monitor = createMonitor(config.getInitParameter(CHAIN_MONITOR_PARAM));
        failureUrl = config.getInitParameter(FAILURE_URL_PARAM);
        String builderClassName = config
                .getInitParameter(BUILDER_CLASSNAME_PARAM);
        String containerScriptName = config
                .getInitParameter(CONTAINER_SCRIPT_NAME_PARAM);
        String emptyContainerScript = config
                .getInitParameter(EMPTY_CONTAINER_SCRIPT_PARAM);
        checkParametersAreSet(builderClassName, containerScriptName, emptyContainerScript);
        chainBuilder = new ServletChainBuilder(config.getServletContext(),
                builderClassName, containerScriptName, emptyContainerScript);
    }

    /**
     * Instantiates ChainMonitor from class name or a ConsoleChainMonitor if 
     * class name not provided or invalid
     * @param monitorClassName
     * @return A ChainMonitor
     */
    private ChainMonitor createMonitor(String monitorClassName) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            return (ChainMonitor) classLoader.loadClass(monitorClassName)
                    .newInstance();
        } catch (Exception e) {
            return new ConsoleChainMonitor();
        }
    }

    /**
     * Checks parameters are set
     * 
     * @param builderClassName
     * @param containerScriptName
     * @param emptyContainerScript
     * @throws ServletException if parameters are not all set
     */
    private void checkParametersAreSet(String builderClassName, String containerScriptName, String emptyContainerScript) throws ServletException {
        if ( builderClassName == null ) { 
            throw new ServletException("Parameter '"+BUILDER_CLASSNAME_PARAM+"' must be set in filter init params");
        }
        if ( containerScriptName == null ) { 
            throw new ServletException("Parameter '"+CONTAINER_SCRIPT_NAME_PARAM+"' must be set in filter init params");
        }
        if ( emptyContainerScript == null ) { 
            throw new ServletException("Parameter '"+EMPTY_CONTAINER_SCRIPT_PARAM+"' must be set in filter init params");
        }        
    }

    /**
     * @see Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;

		if (httpRequest.getAttribute(ALREADY_FILTERED_KEY) == null) {
			// we were not here, filter
			httpRequest.setAttribute(ALREADY_FILTERED_KEY, Boolean.TRUE);
			// obtain pico container for chaining
			PicoContainer container = obtainContainer(httpRequest);
            
            try {
				String originalUrl = httpRequest.getServletPath();
                 monitor.filteringURL(originalUrl);
                 List elements = extractPathElements(originalUrl);
                 // build chain
				ContainerChain chain = chainBuilder.buildChain(elements
						.toArray(), container);
                 // start chain
				chain.start();
				// inject last container in chain
                 injectLastContainerInChain(request, chain);
                 // filter
				filterChain.doFilter(request, response);
                 // stop chain
				chain.stop();
			} catch (Exception ex) {
                handleException(ex, container, request, response);
			} finally {
                restoreContainer(request, container);
			}

		}
	}

    private ObjectReference obtainRequestObjectReference(ServletRequest request) {
        return new RequestScopeObjectReference(request,
                KeyConstants.REQUEST_CONTAINER);
    }        

    private PicoContainer obtainContainer(ServletRequest request) {
        return (PicoContainer) obtainRequestObjectReference(request).get();
    }        

    private void injectLastContainerInChain(ServletRequest request, ContainerChain chain) {
        obtainRequestObjectReference(request).set(chain.getLast());       
    }

    private void restoreContainer(ServletRequest request, PicoContainer container) {
        obtainRequestObjectReference(request).set(container);       
    }        
    
    private void handleException(Exception e, PicoContainer container, ServletRequest request, ServletResponse response) throws ServletException, IOException {
        monitor.exceptionOccurred(e);
        if (failureUrl != null) {
            // if we got an exception, we create fake container
            DefaultPicoContainer dpc = new DefaultPicoContainer(
                    container);
            dpc.registerComponentInstance("cause", e.getCause());
            // wire container to request
            obtainRequestObjectReference(request).set(dpc);
            // and transfer us to this url
            request.getRequestDispatcher(failureUrl).forward(request,
                    response);
        } else {
            // if there is no configured redirect url for failure, we just 
            // wrap in servlet exception and rethrow this. 
            throw new ServletException(e);
        }
    }

    /**
     * Extracts the list of path element from url
     * @param url the String with the original url
     * @return A List of String representing the path elements
     */
    private List extractPathElements(String url) {
        List elements = new ArrayList();
        elements.add(PATH_SEPARATOR);
        for (int pos = url.indexOf(PATH_SEPARATOR, 1); pos > 0; pos = url
                .indexOf(PATH_SEPARATOR, pos + 1)) {
            String path = url.substring(0, pos + 1);
            elements.add(path);
            monitor.pathAdded(path, url);
        }
        return elements;
    }

    /**
     * @see Filter#destroy()
     */
    public void destroy() {
        // no-op
    }

}