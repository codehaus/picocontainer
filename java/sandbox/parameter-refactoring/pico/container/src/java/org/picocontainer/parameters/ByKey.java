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

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.ParameterName;
import org.picocontainer.PicoContainer;
import org.picocontainer.injectors.AbstractInjector;

/**
 * component parameter implementing by-key resolution strategy. type conversion
 * will be applied if necessary to allow injection of config entries.
 * 
 * @author Konstantin Pribluda
 */
@SuppressWarnings("serial")
public class ByKey<T> extends BasicComponentParameter<T> {


	private Object componentKey;

	public ByKey(Object componentKey) {
		this.componentKey = componentKey;
	}

	/**
	 * Check wether the given Parameter can be statisfied by the container.
	 * 
	 * @return <code>true</code> if the Parameter can be verified.
	 * 
	 * @throws org.picocontainer.PicoCompositionException
	 *             {@inheritDoc}
	 * @see Parameter#isResolvable(PicoContainer,ComponentAdapter,Class,ParameterName,boolean)
	 */
	public boolean isResolvable(PicoContainer container) {
		return resolveAdapter(container) != null;
	}

	/**
	 * actual adapter retrieval
	 */
	@Override
	ComponentAdapter<T> obtainAdapter(PicoContainer container) {
		return (ComponentAdapter<T>) container.getComponentAdapter(componentKey);
	}

	public T resolveInstance(PicoContainer container) {

		return resolveAdapter(container).getComponentInstance(container);
	}

	public void verify(PicoContainer container) {
		final ComponentAdapter resolvedAdapter = resolveAdapter(container);
		if(resolvedAdapter == null) {
			throw new AbstractInjector.MissingDependencyException(componentKey);
		}
		resolvedAdapter.verify(container);
	}


}
