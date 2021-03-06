package org.picocontainer.parameters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;

/**
 * abstract base lookup class. implements core lookup functionality
 * 
 * @author k.pribluda
 * 
 */
public abstract class AbstractLookup implements Lookup {

	boolean recursive;

	Collection<ComponentAdapter> result;

	public AbstractLookup(boolean recursive) {
		super();
		this.recursive = recursive;
	}

	abstract boolean isAcceptable(ComponentAdapter adapter);

	/**
	 * retrieve component adapters
	 */
	@SuppressWarnings("unchecked")
	public Collection<ComponentAdapter> lookup(PicoContainer container) {
		if (result == null ) {
			result = processTree(container, new ArrayList<ComponentAdapter>(),
					Collections.EMPTY_SET);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	Collection<ComponentAdapter> processTree(PicoContainer container,
			Collection<ComponentAdapter> adapters, Collection exclude) {
		if (container.getParent() != null && recursive) {
			Collection toExclude = new HashSet(container.getComponentKeys());
			toExclude.addAll(exclude);
			processTree(container.getParent(), adapters, toExclude);
		}

		for (ComponentAdapter candidate : container.getComponentAdapters()) {
			if (!exclude.contains(candidate.getComponentKey())
					&& isAcceptable(candidate)) {
				adapters.add(candidate);
			}
		}
		return adapters;
	}

}
