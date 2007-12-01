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

/**
 * component parameter implementing by-class lookup strategy
 * 
 * @author Konstantin Pribluda
 */

public class ByClass extends AbstractLookup {

	Class expectedType;

	public ByClass(Class expectedType) {
		this.expectedType = expectedType;
	}

	public String toString() {
		return "ByClass(" + expectedType + ")";
	}

	@SuppressWarnings("unchecked")
	@Override
	boolean isAcceptable(ComponentAdapter adapter) {
		return expectedType.isAssignableFrom(adapter
				.getComponentImplementation());
	}
}
