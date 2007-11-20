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
import java.util.HashSet;
import java.util.Set;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;


/**
 * component parameter implementing by-class lookup strategy
 * @author Konstantin Pribluda
 *
 */

public class ByClass implements Lookup {

	Class expectedType;
	
	public ByClass(Class expectedType) {
		this.expectedType = expectedType;
	}

	public Collection<ComponentAdapter<?>> lookup(PicoContainer container) {

		return null;
	}


}
