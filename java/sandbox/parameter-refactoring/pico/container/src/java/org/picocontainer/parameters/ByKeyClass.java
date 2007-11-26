/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.parameters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;

/**
 * lookup component adapters registered with certain key class
 * @author Konstantin Pribluda
 *
 */
public class ByKeyClass implements Lookup {

	Class keyClass;
	
	
	public ByKeyClass(Class keyClass) {
		this.keyClass = keyClass;
	}


	/**
	 * retrieve list of matching component adapters
	 * (registered with subclass of keyClass)
	 */
	public Collection<ComponentAdapter> lookup(PicoContainer container) {
		List<ComponentAdapter> adapters = new ArrayList<ComponentAdapter>();
		return extractAdapters(container,adapters,keyClass);
	}
	/**
	 * recursive adapter extraction. do parent-first so 
	 * deeper keys can be overload later
	 */
	private Collection<ComponentAdapter> extractAdapters(PicoContainer container, List<ComponentAdapter> adapters, Class keyClass) { 
		if(container.getParent() != null) {
			extractAdapters(container.getParent(),adapters,keyClass);
		}
		for(ComponentAdapter candidate: container.getComponentAdapters()) {
			System.err.println("checking " + candidate.getComponentKey() );
			if(keyClass.isAssignableFrom(candidate.getComponentKey().getClass())) {
				adapters.add(candidate);
			}
		}
		return adapters;
	}

}
