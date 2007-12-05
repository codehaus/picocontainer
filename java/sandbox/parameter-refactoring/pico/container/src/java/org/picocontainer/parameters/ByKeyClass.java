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
 * lookup component adapters registered with certain key class
 * @author Konstantin Pribluda
 *
 */
public class ByKeyClass extends AbstractLookup {

	Class keyClass;
	


	public ByKeyClass(Class keyClass,boolean recursive) {
		super(recursive);
		this.keyClass = keyClass;
	}

	public ByKeyClass(Class keyClass) { 
		this(keyClass,true);
	}


	@SuppressWarnings("unchecked")
	@Override
	boolean isAcceptable(ComponentAdapter adapter) {
		return keyClass.isAssignableFrom(adapter.getComponentKey().getClass());
	}

}
