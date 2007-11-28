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

import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoVisitor;

/**
 * constant instance resover. 
 * @author k.pribluda
 *
 * @param <T>
 */
public class Constant<T> implements Parameter<T> {

	T  value;

	public Constant(T value) {
		this.value = value;
	}

	/**
	 * resolve constant instance
	 */
	public T resolveInstance(PicoContainer container) {
		return value;
	}
	
	
	public String toString() {
		return "Constant[" +  value + "]";
	}

	public boolean isResolvable(PicoContainer container) {
		return true;
	}

	public void verify(PicoContainer container) {
	}

	public void accept(PicoVisitor visitor) {
		visitor.visitParameter(this);
	}
}
