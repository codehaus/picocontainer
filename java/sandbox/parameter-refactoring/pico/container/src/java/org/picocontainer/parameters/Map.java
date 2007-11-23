package org.picocontainer.parameters;

import java.util.HashMap;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;

/**
 * extract results to map
 * 
 * @author Konstantin Pribluda
 * 
 */
public class Map extends AbstractCollectionExtractor {

	Class<? extends java.util.Map> mapType;

	public Map(Lookup lookup, Class<? extends java.util.Map> mapType) {
		this(lookup, false, mapType);
	}

	public Map(Lookup lookup) {
		this(lookup, false, HashMap.class);
	}

	public Map(Lookup lookup, boolean empty) {
		this(lookup, empty, HashMap.class);
	}

	public Map(Lookup lookup, boolean empty,
			Class<? extends java.util.Map> mapType) {
		super(lookup, empty);
		this.mapType = mapType;
	}

	public Object resolveInstance(PicoContainer container) {
		java.util.Map result;
		try {
			result = mapType.newInstance();
		} catch (Exception e) {
			throw new PicoCompositionException(
					"can not create collection type", e);
		}
		java.util.Collection<ComponentAdapter> adapters = lookup
				.lookup(container);
		for (ComponentAdapter adapter : adapters) {
			result.put(adapter.getComponentKey(), adapter
					.getComponentInstance(container));
		}
		return result;
	}

}
