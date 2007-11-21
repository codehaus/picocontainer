package org.picocontainer.parameters;

import org.picocontainer.PicoContainer;
import org.picocontainer.injectors.AbstractInjector;

public abstract class AbstractCollectionExtractor extends AbstractParameter implements Extract{

	boolean empty;
	
	/**
	 * 
	 * @param lookup lookup to use against container 
	 * @param empty whether empty collection is to be honored
	 */
	public AbstractCollectionExtractor(Lookup lookup, boolean empty) {
		super(lookup);
		this.empty = empty;
	}

	/**
	 * is resolvable if not empty collection is allowed
	 */
	public boolean isResolvable(PicoContainer container) {	
		return empty  || !lookup.lookup(container).isEmpty() ;
	}

	public void verify(PicoContainer container) {
		if(!isResolvable(container)) {
			throw new AbstractInjector.MissingDependencyException(this.toString());
		}
	}
	

}
