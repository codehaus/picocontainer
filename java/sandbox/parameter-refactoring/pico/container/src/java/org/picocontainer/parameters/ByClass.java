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

import java.util.HashSet;
import java.util.Set;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;


/**
 * component parameter implementing by-class lookup strategy
 * @author Konstantin Pribluda
 *
 */
@SuppressWarnings("serial")
public class ByClass<T> extends BasicComponentParameter<T> {

	protected Class<T> expectedType;

	public ByClass(Class<T> expectedType) {
		super();
		this.expectedType = expectedType;
	}

	/**
	 * just retrieve component instance out of adapter. it shall be 
	 * of proper type.
	 */
	public T resolveInstance(PicoContainer container) {
		return resolveAdapter(container).getComponentInstance(container);
	}

	/**
	 * TODO: find a elegant way to produce good looking exception
	 */
	public void verify(PicoContainer container) {
	       final ComponentAdapter componentAdapter =
	            resolveAdapter(container);
	        if (componentAdapter == null) {
	            final Set<Class> set = new HashSet<Class>();
	            set.add(expectedType);
	           // throw new AbstractInjector.UnsatisfiableDependenciesException(adapter, null, set, container);
	        }
	        componentAdapter.verify(container);
	}

	/**
	 * obtain suitable adapter from container. 
	 */
	@Override
	ComponentAdapter<T> obtainAdapter(PicoContainer container) {
		return container.getComponentAdapter(expectedType);
	}

	protected Class<T> getExpectedType() {
		return expectedType;
	}

	protected void setExpectedType(Class<T> expectedType) {
		this.expectedType = expectedType;
	}


}
