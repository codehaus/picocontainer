package org.picocontainer.parameters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;

/**
 * abstract base lookup class. implements common lookup functionality 
 * 
 * @author k.pribluda
 *
 */
public abstract class AbstractLookup  implements Lookup {


	Collection<ComponentAdapter> result;


	abstract void extract(PicoContainer container,Collection<ComponentAdapter> store);
	
	

	/**
	 * retrieve component adapters
	 */
	@SuppressWarnings("unchecked")
	public Collection<ComponentAdapter> lookup(PicoContainer container) {
		if (result == null) {
			result = processTree(container, new ArrayList<ComponentAdapter>());
		}
		return result;
	}



	Collection<ComponentAdapter> processTree(PicoContainer container, List<ComponentAdapter> adapters) {
		if (container.getParent() != null) {
			processTree(container.getParent(), adapters);
		}
		extract(container,adapters);
		return adapters;
	}

}
