/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar.webwork2;

import javax.servlet.http.HttpServletRequest;

import org.nanocontainer.nanowar.ServletContainerFinder;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ObjectReference;

import com.opensymphony.xwork.ObjectFactory;

/**
 * <p>
 * PicoContainer based object factory
 * </p>
 * 
 * @author <a href="mailto:cleclerc@pobox.com">Cyrille Le Clerc </a>
 * @author Jonas Engman
 */
public class PicoObjectFactory extends ObjectFactory {

	private ServletContainerFinder containerFinder;
	private ObjectReference objectReference;

	public PicoObjectFactory(ObjectReference objectReference) {
		super();
		this.objectReference = objectReference;
		containerFinder = new ServletContainerFinder();
	}

	/**
	 * @see com.opensymphony.xwork.ObjectFactory#buildBean(java.lang.Class)
	 */
	public Object buildBean(Class clazz) throws Exception {
		PicoContainer requestContainer = containerFinder.findContainer((HttpServletRequest) objectReference.get());

		Object bean = requestContainer.getComponentInstance(clazz);
		if (bean == null) {
			// The action wasn't registered. Do it ad-hoc here.
			MutablePicoContainer tempContainer = new DefaultPicoContainer(requestContainer);
			tempContainer.registerComponentImplementation(clazz);
			bean = tempContainer.getComponentInstance(clazz);
		}
		if (bean == null) {
			//TODO check the exception type (should it subclass PicoException)
			throw new NullPointerException("No component instance found for " + clazz);
		}
		return bean;
	}

	/**
	 * <p>
	 * As {@link ObjectFactory#buildBean(java.lang.String)}does not delegate to
	 * {@link ObjectFactory#buildBean(java.lang.Class)}but directly calls
	 * <code>clazz.newInstance()</code>, overwrite this method to call
	 * <code>buildBean()</code>
	 * </p>
	 * 
	 * @see com.opensymphony.xwork.ObjectFactory#buildBean(java.lang.String)
	 */
	public Object buildBean(String className) throws Exception {
		Class clazz = getClassInstance(className);
		return buildBean(clazz);
	}
}
