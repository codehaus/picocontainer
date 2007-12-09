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
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
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

	MutablePicoContainer container;
	
	public void setUp() {
		container= new DefaultPicoContainer();
		container.addComponent("foo.bar","239");
	}
	
	/**
	 * convert parameter passes non-string values as-is
	 */
	public void testNotStringIsLeavedAlone() {
		Integer value=new Integer(239);
		container.addComponent(value);
		assertSame(value, new Convert(new ByKey(Integer.class),Integer.class).resolveInstance(container));
	}

	/**
	 * all registered converters shall work
	 * 
	 * @TODO: string to character conversion?
	 */
	public void testRegisteredConverters() {
		container.addComponent("bool","true");
		container.addComponent("121","121");
		ByKey byKey= new ByKey("foo.bar");
		
		assertEquals(239, new Convert(byKey,Integer.class).resolveInstance(container));
		assertEquals(239.0, new Convert(byKey,Double.class).resolveInstance(container));
		assertEquals(true, new Convert(new ByKey("bool"),Boolean.class).resolveInstance(container));
		assertEquals(239l,new Convert(byKey,Long.class).resolveInstance(container));
		assertEquals((float) 239.0, new Convert(byKey,Float.class).resolveInstance(container));
		// something stupid with character conveter...
		//assertEquals('v',new Convert(byKey,Character.class).resolveInstance(container));
		assertEquals((byte) 121, new Convert(new ByKey("121"),Byte.class).resolveInstance(container));
		assertEquals((short) 239, new Convert(byKey,Short.class).resolveInstance(container));
		assertEquals(new File("239"), new Convert(byKey,File.class).resolveInstance(container));
	}

	
	/**
	 * converters shall be registered automatically
	 * if possible
	 */
	public void testAutomaticConverterRegistration() {
		ByKey byKey= new ByKey("foo.bar");
		assertEquals(ValueOfComponent.class,new Convert(byKey,ValueOfComponent.class).resolveInstance(container).getClass());
		assertEquals(StringConstructorComponent.class,new Convert(byKey,StringConstructorComponent.class).resolveInstance(container).getClass());
	}
	
	
	/**
	 * those who register converter explicitely 
	 * without available converter do not deserve anything
	 * less than thrown  exception 
	 *
	 */
	public void testNoConverterIsBombed() {
		try {
			new Convert(new ByKey("foo.bar"),PicoContainer.class).resolveInstance(container);
			fail("was not bombed on non available converter");
		} catch(PicoCompositionException ex) {
			
		}
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

}
