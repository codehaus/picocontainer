/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Stacy Curl                        *
 *****************************************************************************/

package org.picocontainer.defaults;

import org.picocontainer.PicoInitializationException;
import org.picocontainer.testmodel.AlternativeTouchable;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

import junit.framework.TestCase;

public class DefaultPicoContainerUnregisterComponentTestCase extends TestCase {
	private DefaultPicoContainer picoContainer;
	
	protected void setUp() throws Exception {
		picoContainer = new DefaultPicoContainer.Default();
	}
	
	public void testCannotInstantiateAnUnregisteredComponent() throws DuplicateComponentKeyRegistrationException, AssignabilityRegistrationException, NotConcreteRegistrationException, PicoInvocationTargetInitializationException, PicoInitializationException {
		picoContainer.registerComponent(Touchable.class, SimpleTouchable.class);
		picoContainer.unregisterComponent(Touchable.class);
		
		picoContainer.instantiateComponents();
		
		assertTrue(picoContainer.getComponents().isEmpty());
	}
	
	public void testCanInstantiateReplacedComponent() throws DuplicateComponentKeyRegistrationException, AssignabilityRegistrationException, NotConcreteRegistrationException, PicoInvocationTargetInitializationException, PicoInitializationException {
		picoContainer.registerComponent(Touchable.class, SimpleTouchable.class);
		picoContainer.unregisterComponent(Touchable.class);
				
		picoContainer.registerComponent(Touchable.class, AlternativeTouchable.class);
		
		picoContainer.instantiateComponents();
		
		assertEquals("Container should container 1 component",
			1, picoContainer.getComponents().size());
	}
	
	public void testReplacedInstantiatedComponentHasCorrectClass() throws DuplicateComponentKeyRegistrationException, AssignabilityRegistrationException, NotConcreteRegistrationException, PicoInvocationTargetInitializationException, PicoInitializationException {
		picoContainer.registerComponent(Touchable.class, SimpleTouchable.class);
		picoContainer.unregisterComponent(Touchable.class);
				
		picoContainer.registerComponent(Touchable.class, AlternativeTouchable.class);
		
		picoContainer.instantiateComponents();
		
		Object component = picoContainer.getComponents().iterator().next();
		
		assertEquals(AlternativeTouchable.class, component.getClass());
	}
	
//	public void testCanInstantiateOriginalComponentThenReplaceAndInstantiateReplacement() throws DuplicateComponentKeyRegistrationException, AssignabilityRegistrationException, NotConcreteRegistrationException, PicoInvocationTargetInitializationException, PicoInitializationException {
//		picoContainer.registerComponent(Touchable.class, SimpleTouchable.class);
//		picoContainer.instantiateComponents();
//
//		picoContainer.unregisterComponent(Touchable.class);
//		picoContainer.registerComponent(Touchable.class, AlternativeTouchable.class);
//
//		picoContainer.instantiateComponents();
//		
//		assertEquals("Container should contain 2 components: the original and the replacement", 2,
//			picoContainer.getComponents().size());
//	}
}
