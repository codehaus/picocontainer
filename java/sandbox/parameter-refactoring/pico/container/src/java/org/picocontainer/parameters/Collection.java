package org.picocontainer.parameters;

import java.util.ArrayList;
import java.util.LinkedList;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;

import com.sun.org.apache.bcel.internal.generic.CPInstruction;

/**
 * extract collection of values into array list
 * 
 * @author Konstantin Pribluda
 */
public class Collection extends AbstractCollectionExtractor {

	Class<? extends java.util.Collection> collectionClass;

	
	
	public Collection(Lookup lookup) {
		this(lookup, false,ArrayList.class);
	}

	public Collection(Lookup lookup, boolean empty) {
		this(lookup, empty,ArrayList.class);
	}
	
	
	public Collection(Lookup lookup, boolean empty,
			Class<? extends java.util.Collection> collectionClass) {
		super(lookup, empty);
		this.collectionClass = collectionClass;
	}

	public Collection(Lookup lookup, Class<? extends java.util.Collection> collectionClass) {
		this(lookup,false,collectionClass);
	}

	public Object resolveInstance(PicoContainer container) {
		java.util.Collection<ComponentAdapter> adapters = lookup
				.lookup(container);
		java.util.Collection result;
		try {
			result = collectionClass.newInstance();
		} catch (Exception e) {
			throw new PicoCompositionException(
					"can not create collection type", e);
		}
		for (ComponentAdapter adapter : adapters) {
			result.add(adapter.getComponentInstance(container));
		}
		return result;
	}
}
