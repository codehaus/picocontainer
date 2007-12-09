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

import java.util.ArrayList;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
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
			// though it would be formally better ask found component adapter 
			// instead of container and get rid of container param at all
			return container.getComponent(adapters.iterator().next().getComponentKey());
			// this is conceptually better, as this will allow us
			// to made extractor unaware of container at all, and thus
			// simplify interface
			// ( by storing container reference in the lookup ) 
			// but we have to first resolve issue with instantiation 
			// order tracking 
			//return adapters.iterator().next().getComponentInstance(container);
		} 
		
		throw bombAmbiguity(adapters);
	}

	public void verify(PicoContainer container) {
		
		java.util.Collection<ComponentAdapter> adapters = lookup.lookup(container);
		System.err.println("found: " + adapters);
		if(adapters.isEmpty()) {
			throw new AbstractInjector.MissingDependencyException(this.toString());
		} else if(adapters.size() == 1) {
			// walk down 
			adapters.iterator().next().verify(container);
		} 
		throw bombAmbiguity(adapters);
	}

	public String toString() {
		return "Scalar [" + lookup + "]";
	}

	/**
	 * whther we can resolve scalar from this adapter. ambiguity 
	 * will be bombed
	 */
	public boolean isResolvable(PicoContainer container) {
		java.util.Collection<ComponentAdapter> adapters = lookup.lookup(container);
		switch(adapters.size()) {
		case 0: 
			return false;
		case 1:
			return true;
		default:
			throw bombAmbiguity(adapters);
		}
	}

	private  AbstractInjector.AmbiguousComponentResolutionException bombAmbiguity(java.util.Collection<ComponentAdapter> adapters) {
		//ok, more than one - produce ambiguity exception
		ArrayList<Object> keys = new ArrayList<Object>();
        for(ComponentAdapter adapter: adapters ) {
        	keys.add(adapter.getComponentKey());
        }
        //
       return  new AbstractInjector.AmbiguousComponentResolutionException(null,keys.toArray());

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
