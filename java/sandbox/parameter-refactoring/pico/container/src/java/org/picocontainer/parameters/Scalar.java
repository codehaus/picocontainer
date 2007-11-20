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
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;


/**
 * perform scalar extraction of data obtained via lookup. 
 * 
 * @author Konstantin Pribluda
 */
public class Scalar implements Extract {
    Lookup lookup;
    public Scalar(Lookup lookup) {
		this.lookup = lookup;
	}
    
    /**
     * resolve instance in question
     * 
     */
	public Object resolveInstance(PicoContainer container) {
		Collection<ComponentAdapter<?>> adapters = lookup.lookup(container);
		if(adapters.size() == 0) {
			return null;
		} if(adapters.size() == 1) {
			return adapters.iterator().next().getComponentInstance(container);
		} if(adapters.size() > 1) {
			throw new PicoCompositionException("Ambiguous component found by scalar resolution. (" + lookup +")");
		}
		return null;
	}


}
