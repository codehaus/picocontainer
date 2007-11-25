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

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;

/**
 * component parameter implementing by-class lookup strategy
 * 
 * @author Konstantin Pribluda
 * 
 */

public class ByClass implements Lookup {

	Class expectedType;

	Collection<ComponentAdapter> result;

	public ByClass(Class expectedType) {
		this.expectedType = expectedType;
	}

	/**
	 * retrieve component adapters by class.
	 */
	@SuppressWarnings("unchecked")
	public Collection<ComponentAdapter> lookup(PicoContainer container) {
		if (result == null) {
			result = recursive(container, new ArrayList<ComponentAdapter>());
		}
		return result;
	}

	Collection<ComponentAdapter> recursive(PicoContainer container,
			Collection<ComponentAdapter> keep) {
		for (Object candidate : container.getComponentAdapters(expectedType)) {
			if (evaluate((ComponentAdapter) candidate)) {
				keep.add((ComponentAdapter) candidate);
			}
		}
		if (container.getParent() != null) {
			recursive(container.getParent(), keep);
		}
		return keep;
	}

	/**
	 * whether to add component adapter to collection
	 * 
	 * @param adapter
	 * @return
	 */
	public boolean evaluate(ComponentAdapter adapter) {
		return true;
	}
}
