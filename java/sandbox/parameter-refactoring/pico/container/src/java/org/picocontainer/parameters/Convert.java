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

public interface Convert<T> {

	/**
	 * perform instance resolution and conversion.
	 * @param container
	 * @return
	 */
	T resolveInstance(PicoContainer container); 
}
