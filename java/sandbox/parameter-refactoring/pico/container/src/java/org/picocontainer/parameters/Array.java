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

	
	/**
	 * create non-empty array of base class with by-class lookup,
	 * @param baseClass class and array type
	 */
	public Array(Class baseClass) {
		this(new ByClass(baseClass),false,baseClass);
	}
	
	public Array(Class baseClass, boolean empty) {
		this(new ByClass(baseClass),empty,baseClass);
	}

	public Array(Lookup lookup,Class baseClass) {
		this(lookup, false,baseClass);
	}
	

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
			result[i] = componentAdapter.getComponentInstance(container);
			i++;
		}	
		return result;
	}
	
	public String toString() {		
		return "Array of " + baseClass + "[" + lookup + "]";
	}

	/**
	 * array satisfyes only if expected type is array, and expected
	 * array type can be assigned from  base class
	 * ( say, int[] satisfies Object[] ) 
	 */
	public boolean canSatisfy(PicoContainer container, Class expectedType) {
		return super.isResolvable(container) && expectedType.isArray() && expectedType.getComponentType().isAssignableFrom(baseClass);
	}

}
