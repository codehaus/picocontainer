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
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.injectors.AbstractInjector;


/**
 * perform scalar extraction of data obtained via lookup. 
 * @author Konstantin Pribluda
 */
public class Scalar extends AbstractParameter {
    public Scalar(Lookup lookup) {
		super(lookup);
	}
    
    /**
     * resolve instance in question
     * 
     */
	public Object resolveInstance(PicoContainer container) {
		
		java.util.Collection<ComponentAdapter> adapters = lookup.lookup(container);
		if(adapters.size() == 0) {
			return null;
		} if(adapters.size() == 1) {
			// resolution against the container is correct,
			// as we performed lookup against it, we can be sure that
			// we got nothing from child and masking happened already
			return container.getComponent(adapters.iterator().next().getComponentKey());
		} if(adapters.size() > 1) {
			throw new PicoCompositionException("Ambiguous component found by scalar resolution. [" + lookup +"]");
		}
		return null;
	}

	public void verify(PicoContainer container) {
		
		java.util.Collection<ComponentAdapter> adapters = lookup.lookup(container);
		if(adapters.isEmpty()) {
			throw new AbstractInjector.MissingDependencyException(this.toString());
		} else if(adapters.size() == 1) {
			// walk down 
			adapters.iterator().next().verify(container);
		} else if(adapters.size() > 1) {
			throw new PicoCompositionException("Ambiguous component found by scalar resolution. [" + lookup +"]");
		}
	}

	public String toString() {
		return "Scalar [" + lookup + "]";
	}

	public boolean isResolvable(PicoContainer container) {	
		return lookup.lookup(container).size() == 1;
	}

	/**
	 * convenience method to create scalar parameter
	 * based on by-key lookup strategy. 
	 * @param key
	 * @return
	 */
	public static Parameter byKey(Object key) {
		return new Scalar(new ByKey(key));
	}
	
	/**
	 * convenience method to create scalar parameter with by-class lookup
	 * strategy. 
	 * @param clazz
	 * @return
	 */
	public static Parameter byClass(Class clazz) {
		return new Scalar(new ByClass(clazz));
	}

	public boolean canSatisfy(PicoContainer container, Class expectedType) {
		return isResolvable(container) && expectedType.isAssignableFrom(lookup.lookup(container).iterator().next().getComponentImplementation());
	}
}
