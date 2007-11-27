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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.adapters.InstanceAdapter;

import junit.framework.TestCase;

/**
 * test scalar extraction out of lookup
 * @author Konstantin Pribluda
 *
 */
public class ScalarTestCase extends TestCase {

	/**
	 * shall resolve single instance
	 */
	public void testSuccesfullExtraction() {	
		Scalar scalar = new Scalar(new Lookup() {

			public Collection<ComponentAdapter> lookup(PicoContainer container) {
				// TODO Auto-generated method stub
				return Collections.singleton(((ComponentAdapter)new InstanceAdapter("42","239")));
			}});
		assertTrue(scalar.isResolvable(null));
		assertEquals("239",scalar.resolveInstance(null));
	}
	
	/**
	 * no instance - not resolvable
	 */
	public void testEmptyNotResolvable() {
		Scalar scalar = new Scalar(new Lookup() {

			public Collection<ComponentAdapter> lookup(PicoContainer container) {
				return Collections.EMPTY_SET;
			}});
		assertFalse(scalar.isResolvable(null));
		assertEquals(null,scalar.resolveInstance(null));
		
	}
	
	
	public void testAmbiguousIsNotResolvableAndBombs() {
		Scalar scalar = new Scalar(new Lookup() {

			public Collection<ComponentAdapter> lookup(PicoContainer container) {
				return Arrays.asList(new ComponentAdapter[] {
						new InstanceAdapter("42","239"),
						new InstanceAdapter("42","239")
				});
			}});
		assertFalse(scalar.isResolvable(null));
		try {
			scalar.resolveInstance(null);
			fail("should have produced exception");
		} catch(PicoCompositionException ex) {
			// anticipated
		}
	}
	
	
	
	
}
