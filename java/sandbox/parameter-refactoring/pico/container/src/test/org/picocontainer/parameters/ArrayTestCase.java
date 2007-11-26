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

import org.picocontainer.ComponentAdapter;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.PicoContainer;

/**
 * test that array extractor works as intendet
 * 
 * @author k.pribluda
 */
public class ArrayTestCase extends AbstractCollectionTestCase {

	/**
	 * array shall be created and popukated
	 * 
	 */
	public void testThatArrayIsCreatedProperly() {
		Array array = new Array(new Lookup() {

			public Collection<ComponentAdapter> lookup(PicoContainer container) {
				return adapters;
			}
		}, String.class);

		assertTrue(array.isResolvable(null));
		assertEquals(String[].class, array.resolveInstance(null).getClass());
		assertEquals(2, ((String[]) array.resolveInstance(null)).length);
		assertEquals("bar", ((String[]) array.resolveInstance(null))[0]);
		assertEquals("bang", ((String[]) array.resolveInstance(null))[1]);
	};

	
	public void testContainerIsPassedToLookup() {
		final PicoContainer toCheck = new DefaultPicoContainer();
		Array array = new Array(new Lookup() {

			public Collection<ComponentAdapter> lookup(PicoContainer container) {
				assertSame(container, toCheck);
				return adapters;

			}
		}, String.class);
		array.resolveInstance(toCheck);
	}

	

}
