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
 * perform scalar conversion of data obtained by lookup. 
 * @author Konstantin Pribluda
 */
public class Scalar<T> implements Extract<T> {

	Lookup lookup;
	
	
	public Scalar(Lookup lookup) {
		this.lookup = lookup;
	}

	/**
	 * resolve instance ot
	 */
	public T resolveInstance(PicoContainer container) {
		return null;
	}

}
