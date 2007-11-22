package org.picocontainer.parameters;

import java.util.ArrayList;
import java.util.Collection;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;

/**
 * filter lookup results. this class intendet to be overiden. 
 * @author k.pribluda
 *
 */
public class Filter implements Lookup {

	Lookup delegate;
	
	public Filter(Lookup delegate) {
		super();
		this.delegate = delegate;
	}

	public Collection<ComponentAdapter> lookup(PicoContainer container) {
		ArrayList result = new ArrayList(delegate.lookup(container).size());
		
		for(ComponentAdapter adapter: delegate.lookup(container)) {
			if(evaluate(adapter)) {
				result.add(adapter);
			}
		}
		return result;
	}

	/**
	 * ovverride this method to provide filtering caopacity
	 * @param adapter
	 * @return
	 */
	protected boolean evaluate(ComponentAdapter adapter) {
		return true;
	}
}
