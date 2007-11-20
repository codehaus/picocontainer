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
 * component parameter implementing by-key resolution strategy. type conversion
 * will be applied if necessary to allow injection of config entries.
 * 
 * @author Konstantin Pribluda
 */
@SuppressWarnings("serial")
public class ByKey implements Lookup {

	Object key;
	
	
	public ByKey(Object key) {
		super();
		this.key = key;
	}


	public Collection<ComponentAdapter<?>> lookup(PicoContainer container) {
		return null;
	}


}
