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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.injectors.AbstractInjector;

/**
 * assure that maps are created correctly
 * 
 * @author k.pribluda
 * 
 */
public class MapTestCase extends AbstractCollectionTestCase {

	/**
	 * map shall be created keyed by CA keys, with content of CA and of default
	 * map subtype
	 */
	public void testMapIsCreatedProperly() {
		Map map = new Map(new Lookup() {

			public Collection<ComponentAdapter> lookup(PicoContainer container) {
				return adapters;
			}});
		
		// map shall be resolvable, as this is not empty
		// container is not necessary for this
		assertTrue(map.isResolvable(null));
		
		// default type of map is HashMap
		assertEquals(HashMap.class,map.resolveInstance(null).getClass());
		
		// shall contain everything
		assertEquals("bar",((java.util.Map)map.resolveInstance(null)).get("foo"));
		assertEquals("bang",((java.util.Map)map.resolveInstance(null)).get("baz"));	
	}
	
	
	public void testEmptyMapIsNotResolvableByDefault() {
		Map map = new Map(new Lookup() {

			public Collection<ComponentAdapter> lookup(PicoContainer container) {
				return (Collection<ComponentAdapter>) Collections.EMPTY_LIST;
			}
			
		});
		
		assertFalse(map.isResolvable(null));
	}
	
	
	public void testEmptyMapIsResolvableWhenExplicitelyAsked() {
		Map map = new Map(new Lookup() {

			public Collection<ComponentAdapter> lookup(PicoContainer container) {
				return (Collection<ComponentAdapter>) Collections.EMPTY_LIST;
			}
			
		},true);	
		assertTrue(map.isResolvable(null));	
	}
	
	
	public void testContainerIsPassedToLookup() {
		final PicoContainer toCheck = new DefaultPicoContainer();
		Map map = new Map(new Lookup() {
			public Collection<ComponentAdapter> lookup(PicoContainer container) {
				assertSame(container,toCheck);
				return (Collection<ComponentAdapter>) Collections.EMPTY_LIST;
			}			
		});			
		map.resolveInstance(toCheck);	
	}
	
	public void testDesiredClassIsHonored() {
		Map map = new Map(new Lookup() {
			public Collection<ComponentAdapter> lookup(PicoContainer container) {
				return (Collection<ComponentAdapter>) Collections.EMPTY_LIST;
			}			
		},true,TreeMap.class);			
		assertEquals(TreeMap.class,map.resolveInstance(null).getClass());
	}
	
	
	public void testBombedIfNotResolvable() {
		Map map = new Map(new Lookup() {

			public Collection<ComponentAdapter> lookup(PicoContainer container) {
				return (Collection<ComponentAdapter>) Collections.EMPTY_LIST;
			}
			
		});
		try {
			map.verify(null);
			fail("was not bombed on verification failure");
		} catch(AbstractInjector.MissingDependencyException ex) {
			// anticipated
			
		}
	}
}
