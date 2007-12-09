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
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.adapters.InstanceAdapter;
import org.picocontainer.injectors.AbstractInjector;

import junit.framework.TestCase;

/**
 * test scalar extraction out of lookup
 * 
 * @author Konstantin Pribluda
 * 
 */
public class ScalarTestCase extends TestCase {

	/**
	 * shall resolve single instance
	 */
	public void testSuccesfullExtraction() {
		MutablePicoContainer container = new DefaultPicoContainer();
		container.addComponent("42", "239");
		Scalar scalar = new Scalar(new ByKey("42"));
		assertTrue(scalar.isResolvable(container));
		assertEquals("239", scalar.resolveInstance(container));
	}

	/**
	 * no instance - not resolvable
	 */
	public void testEmptyNotResolvable() {
		Scalar scalar = new Scalar(new Lookup() {

			public Collection<ComponentAdapter> lookup(PicoContainer container) {
				return Collections.EMPTY_SET;
			}
		});
		assertFalse(scalar.isResolvable(null));
		assertEquals(null, scalar.resolveInstance(null));

	}

	/**
	 * attemtp to resolve orverify ambiguous resolution shallbe bombed, but not
	 * polite asking whether this is resolvable
	 * 
	 */
	public void testAmbiguousResolutionShallNotSucceed() {
		Scalar scalar = new Scalar(new Lookup() {

			public Collection<ComponentAdapter> lookup(PicoContainer container) {
				return Arrays.asList(new ComponentAdapter[] {
						new InstanceAdapter("42", "239"),
						new InstanceAdapter("24", "239") });
			}
		});
		try {
			scalar.isResolvable(null);
			fail("should have produced exception");
		} catch (AbstractInjector.AmbiguousComponentResolutionException ex) {
			// anticipated
			assertEquals("42",ex.getAmbiguousComponentKeys()[0]);
			assertEquals("24",ex.getAmbiguousComponentKeys()[1]);
		}
		
		try {
			scalar.resolveInstance(null);
			fail("should have produced exception");
		} catch (AbstractInjector.AmbiguousComponentResolutionException ex) {
			// anticipated
			assertEquals("42",ex.getAmbiguousComponentKeys()[0]);
			assertEquals("24",ex.getAmbiguousComponentKeys()[1]);
		}

		try {
			scalar.verify(null);
			fail("should have produced exception");
		} catch (AbstractInjector.AmbiguousComponentResolutionException ex) {
			// anticipated
			assertEquals("42",ex.getAmbiguousComponentKeys()[0]);
			assertEquals("24",ex.getAmbiguousComponentKeys()[1]);
		}
	}

}
