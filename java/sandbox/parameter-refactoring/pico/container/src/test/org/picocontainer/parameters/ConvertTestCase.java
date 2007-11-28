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

import java.io.File;
import java.util.Collection;
import java.util.Collections;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.adapters.InstanceAdapter;

import junit.framework.TestCase;

/**
 * test that converter works properly
 * 
 * @author Konstantin Pribluda
 */
public class ConvertTestCase extends TestCase {

	/**
	 * convert parameter passes non-string values as-is
	 */

	public void testNotStringIsLeavedAlone() {
		assertEquals(239, wrap(Integer.class, new Integer(239))
				.resolveInstance(null));
	}

	/**
	 * all registered converters shall work
	 * 
	 * @TODO: string to character conversion?
	 */
	public void testRegisteredConverters() {
		assertEquals(239, wrap(Integer.class, "239").resolveInstance(null));
		assertEquals(239.0, wrap(Double.class, "239").resolveInstance(null));
		assertEquals(true, wrap(Boolean.class, "true").resolveInstance(null));
		assertEquals(239l, wrap(Long.class, "239").resolveInstance(null));
		assertEquals((float) 239.0, wrap(Float.class, "239").resolveInstance(
				null));
		// assertEquals('v',wrap(Character.class,"v").resolveInstance(null));
		assertEquals((byte) 121, wrap(Byte.class, "121").resolveInstance(null));
		assertEquals((short) 239, wrap(Short.class, "239")
				.resolveInstance(null));
		assertEquals(new File("foo"), wrap(File.class, "foo").resolveInstance(
				null));
	}

	
	/**
	 * converters shall be registered automatically
	 * if possible
	 */
	public void testAutomaticConverterRegistration() {
		assertEquals(ValueOfComponent.class,wrap(ValueOfComponent.class, "239").resolveInstance(null).getClass());
		assertEquals(StringConstructorComponent.class,wrap(StringConstructorComponent.class, "239").resolveInstance(null).getClass());
	}
	
	
	/**
	 * those who register converter explicitely 
	 * without available converter do not deserve anything
	 * less than thrown  exception 
	 *
	 */
	public void testNoConverterIsBombed() {
		try {
			wrap(PicoContainer.class, "239").resolveInstance(null);
			fail("wa not bombed on non available converter");
		} catch(PicoCompositionException ex) {
			
		}
	}
	
	public void testNotStringIsPassedThrough() {
		assertEquals(new File("glumbla"), wrap(Integer.class, new File("glumbla")).resolveInstance(null));
		
	}
	
	static class ValueOfComponent {
		public static ValueOfComponent valueOf(String string) {
			return new ValueOfComponent();
		}
	}
	
	public static class StringConstructorComponent{
		public StringConstructorComponent(String value) {
			
		}
	}
	/**
	 * conveniently wrap object into singleton collection
	 * 
	 * @param object
	 * @return
	 */
	@SuppressWarnings("unchecked")
	Convert wrap(final Class clazz, final Object object) {
		Convert convert = new Convert(new Lookup() {

			public Collection<ComponentAdapter> lookup(PicoContainer container) {

				return Collections
						.singleton(((ComponentAdapter) new InstanceAdapter(
								"foo", object)));
			}
		}, clazz);
		return convert;
	}
}
