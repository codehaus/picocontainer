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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletContext;

import org.nanocontainer.integrationkit.ContainerRecorder;
import org.nanocontainer.reflection.DefaultContainerRecorder;
import org.nanocontainer.script.xml.XStreamContainerBuilder;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * build container chains from servlet path. cache container recorders for later
 * use
 * 
 * @author Kontantin Pribluda
 * @version $Revision:$
 */
public class ServletChainBuilder {

	/**
	 * Description of the Field
	 */
	public final static String CONTAINER = "nano.xml";

	/**
	 * Description of the Field
	 */
	public final static String EMPTY_CONTAINER = "<container></container>";

	private HashMap recorderCache = new HashMap();

	private ServletContext context;

	/**
	 * Constructor for the ServletChainBuilder object
	 * 
	 * @param context
	 *            Description of Parameter
	 */
	public ServletChainBuilder(ServletContext context) {
		setContext(context);
	}

	/**
	 * Gets the Context attribute of the ServletChainBuilder object
	 * 
	 * @return The Context value
	 */
	public ServletContext getContext() {
		return context;
	}

	/**
	 * give access to recorder cache
	 * 
	 * @return The RecorderCache value
	 */
	HashMap getRecorderCache() {
		return recorderCache;
	}

	/**
	 * obtain reader from servlet context path ( if possible ). appends
	 * CONTAINER to string, and checks whether this can be obtained. if not,
	 * return empty container instead.
	 * 
	 * @param path
	 *            path in servlet context
	 * @return corresponding reader or empty container.
	 */
	Reader obtainReader(String path) {
		InputStream is = getContext().getResourceAsStream(path + CONTAINER);
		if (is != null) {
			return new InputStreamReader(is);
		} else {
			return new StringReader(EMPTY_CONTAINER);
		}
	}

	/**
	 * populate container for given path. cache result in container recorders
	 * 
	 * @param container
	 *            Description of Parameter
	 * @param path
	 *            Description of Parameter
	 */
	public void populateContainerForPath(MutablePicoContainer container,
			String path) {
		ContainerRecorder recorder;
		synchronized (getRecorderCache()) {
			recorder = (ContainerRecorder) getRecorderCache().get(path);
			if (recorder == null) {
				recorder = new DefaultContainerRecorder(
						new DefaultPicoContainer());

				getRecorderCache().put(path, recorder);

				XStreamContainerBuilder builder = new XStreamContainerBuilder(
						obtainReader(path), Thread.currentThread()
								.getContextClassLoader());
				builder.populateContainer(recorder.getContainerProxy());
			}
		}
		recorder.replay(container);
	}

	/**
	 * build container chain for path elements
	 * 
	 * @param parent
	 *            parent container or null
	 * @param pathElements
	 *            array of path elements. Those elements serve as keys for
	 *            selecting Application objects
	 * @return configured container chain
	 */
	public ContainerChain buildChain(Object[] pathElements, PicoContainer parent) {
		ContainerChain chain = new ContainerChain();
		populateRecursive(chain, parent, Arrays.asList(pathElements).iterator());
		return chain;
	}

	/**
	 * create containers in recursive way
	 * 
	 * @param pathElements
	 *            Description of Parameter
	 * @param parent
	 *            Description of Parameter
	 * @param chain
	 *            Description of Parameter
	 */
	public void populateRecursive(ContainerChain chain, PicoContainer parent,
			Iterator pathElements) {
		if (pathElements.hasNext()) {
			Object key = pathElements.next();
			DefaultPicoContainer container = new DefaultPicoContainer(parent);

			populateContainerForPath(container, key.toString());
			chain.addContainer(container);
			populateRecursive(chain, container, pathElements);

		}

	}

	/**
	 * Sets the Context attribute of the ServletChainBuilder object
	 * 
	 * @param context
	 *            The new Context value
	 */
	public void setContext(ServletContext context) {
		this.context = context;
	}

}