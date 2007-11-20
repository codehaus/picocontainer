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

import org.picocontainer.PicoContainer;

/**
 * constant instance
 * @author k.pribluda
 *
 * @param <T>
 */
public class Constant<T> implements Extract<T> {

	T constant;
	
	public Constant(T constant) {
		super();
		this.constant = constant;
	}

	public T resolveInstance(PicoContainer container) {
		return constant;
	}

}
