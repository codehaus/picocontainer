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

import java.util.Collection;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;

/**
 * extract components to array of certain type
 * @author k.pribluda
 */
public  class Array extends AbstractCollectionExtractor {

	Class baseClass;

	

	public Array(Lookup lookup, boolean empty, Class baseClass) {
		super(lookup, empty);
		this.baseClass = baseClass;
	}

	/**
	 * 
	 */
	public Object resolveInstance(PicoContainer container) {
		Collection<ComponentAdapter> adapters = lookup.lookup(container);
		final Object[] result = (Object[]) java.lang.reflect.Array.newInstance(baseClass, 
				adapters.size());
		int i = 0;
		for (ComponentAdapter componentAdapter : adapters) {
			result[i] = container.getComponent(componentAdapter
					.getComponentKey());
			i++;
		}	
		return result;
	}
	
	public String toString() {		
		return "Array of " + baseClass + "[" + lookup + "]";
	}

}
