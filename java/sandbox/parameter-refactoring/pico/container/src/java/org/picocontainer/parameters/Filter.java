package org.picocontainer.parameters;

import java.util.ArrayList;
import java.util.Collection;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;

/**
 * filter lookup results. this class intendet to be overiden. 
 * @author k.pribluda
 */
public abstract class Filter implements Lookup {

	Lookup delegate;
	
	Collection<ComponentAdapter> result;
	
	public Filter(Lookup delegate) {
		super();
		this.delegate = delegate;
	}

	public Collection<ComponentAdapter> lookup(PicoContainer container) {
		if(result == null) {
		 result = new ArrayList<ComponentAdapter>(delegate.lookup(container).size());
			for(ComponentAdapter adapter: delegate.lookup(container)) {
				if(evaluate(adapter)) {
					result.add(adapter);
				}
			}
		}
		return result;
	}

	/**
	 * ovverride this method to provide filtering caopacity
	 * @param adapter
	 * @return
	 */
	public abstract  boolean  evaluate(ComponentAdapter adapter);
}
