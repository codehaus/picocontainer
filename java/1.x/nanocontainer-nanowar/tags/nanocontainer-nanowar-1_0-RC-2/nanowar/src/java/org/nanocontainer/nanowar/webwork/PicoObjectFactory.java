/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar.webwork;

import javax.servlet.http.HttpServletRequest;

import org.nanocontainer.nanowar.ServletContainerFinder;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ObjectReference;

import com.opensymphony.xwork.ObjectFactory;

/**
 * <p>
 * XWork ObjectFactory which uses a PicoContainer to create component instances.
 * </p>
 * 
 * @author Cyrille Le Clerc
 * @author Jonas Engman
 * @author Mauro Talevi
 */
public class PicoObjectFactory extends ObjectFactory {

	private ServletContainerFinder containerFinder;
	private ObjectReference objectReference;

	public PicoObjectFactory(ObjectReference objectReference) {
		this.objectReference = objectReference;
		this.containerFinder = new ServletContainerFinder();
	}

	/**
	 * @see com.opensymphony.xwork.ObjectFactory#buildBean(java.lang.Class)
	 */
	public Object buildBean(Class clazz) throws Exception {
		PicoContainer requestContainer = findRequestContainer();
        
		Object bean = requestContainer.getComponentInstance(clazz);
		if (bean == null) {
             // The action wasn't registered.  Attempt to instantiate it.
             bean = createComponentInstance(requestContainer, clazz);
		}        
		if (bean == null) {
			throw new PicoIntrospectionException("No component instance found for " + clazz);
		}
		return bean;
	}

    private PicoContainer findRequestContainer() {
        return containerFinder.findContainer((HttpServletRequest) objectReference.get());
    }
    
	private Object createComponentInstance(PicoContainer parentContainer, Class clazz) {
        MutablePicoContainer pico = new DefaultPicoContainer(parentContainer);
        pico.registerComponentImplementation(clazz);
        return pico.getComponentInstance(clazz);
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
