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
import java.util.Collections;
import java.util.TreeSet;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.PicoContainer;

/**
 * assure that collection types are created correctly
 * @author k.pribluda
 */
public class CollectionTestCase extends AbstractCollectionTestCase {

	/**
	 * default collection type is array list. 
	 */
	public void testCollectionIsCreatedProperly() {
		Collection collection = new Collection(new Lookup() {

			public java.util.Collection<ComponentAdapter> lookup(PicoContainer container) {
				return adapters;
			}});
		
		// map shall be resolvable, as this is not empty
		// container is not necessary for this
		assertTrue(collection.isResolvable(null));
		
		// default type of map is HashMap
		assertEquals(ArrayList.class,collection.resolveInstance(null).getClass());
		
		// shall contain everything
		assertEquals("bar",((java.util.List)collection.resolveInstance(null)).get(0));
		assertEquals("bang",((java.util.List)collection.resolveInstance(null)).get(1));	
	}
	
		
	
	public void testContainerIsPassedToLookup() {
		final PicoContainer toCheck = new DefaultPicoContainer();
		Collection collection = new Collection(new Lookup() {
			public java.util.Collection<ComponentAdapter> lookup(PicoContainer container) {
				assertSame(container,toCheck);
				return (java.util.Collection<ComponentAdapter>) Collections.EMPTY_LIST;
			}			
		});			
		collection.resolveInstance(toCheck);	
	}
	
	
	public void testDesiredClassIsHonored() {
		Collection collection = new Collection(new Lookup() {
			public java.util.Collection<ComponentAdapter> lookup(PicoContainer container) {
				return (java.util.Collection<ComponentAdapter>) Collections.EMPTY_LIST;
			}			
		},true,TreeSet.class);			
		assertEquals(TreeSet.class,collection.resolveInstance(null).getClass());
	}
}
