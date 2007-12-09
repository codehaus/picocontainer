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
 * lookup is responsible for retrieval of collection of
 * component adapters out of container. this collection will be 
 * later processed by convert. 
 * 
 * @author Konstantin Pribluda
 * @TODO: investigate adding  container reference to lookup 
 * (would made programatic configuration worse,and made them not 
 * reusable)
 */
public interface Lookup {
	
	/**
	 * retrieve collection of component adapters 
	 * from container. 
	 * @param container
	 * @return
	 */
	Collection<ComponentAdapter> lookup(PicoContainer container);
}
