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

import org.nanocontainer.nanowar.ActionsContainerFactory;
import org.picocontainer.MutablePicoContainer;
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

    private ActionsContainerFactory actionsContainerFactory = new ActionsContainerFactory();
    private final ObjectReference objectReference;

    /**
     * Creates a PicoObjectFactory with given object reference, 
     * used to pass the http request to the factory
     * 
     * @param objectReference the ObjectReference 
     */
	public PicoObjectFactory(ObjectReference objectReference) {
        this.objectReference = objectReference;
	}

    /**
     * Instantiates an action using the PicoContainer found in the request scope.
     * 
     * @see com.opensymphony.xwork.ObjectFactory#buildBean(java.lang.Class)
     */
	public Object buildBean(Class actionClass) throws Exception {
        MutablePicoContainer actionsContainer = actionsContainerFactory.getActionsContainer((HttpServletRequest)objectReference.get());
        Object action = actionsContainer.getComponentInstance(actionClass);
        
        if (action == null) {
            // The action wasn't registered. Attempt to instantiate it.
            actionsContainer.registerComponentImplementation(actionClass);
            action = actionsContainer.getComponentInstance(actionClass);
        }
        return action;
	}
    
    /**
	 * As {@link ObjectFactory#buildBean(java.lang.String)}does not delegate to
	 * {@link ObjectFactory#buildBean(java.lang.Class)} but directly calls
	 * <code>clazz.newInstance()</code>, overwrite this method to call
	 * <code>buildBean()</code>
	 * 
	 * @see com.opensymphony.xwork.ObjectFactory#buildBean(java.lang.String)
	 */
	public Object buildBean(String className) throws Exception {
        Class actionClass = actionsContainerFactory.getActionClass(className);
        return buildBean(actionClass);
	}
}
