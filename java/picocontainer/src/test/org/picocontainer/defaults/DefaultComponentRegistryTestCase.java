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

import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.internals.ComponentFactory;
import org.picocontainer.internals.ComponentRegistry;
import org.picocontainer.internals.ComponentSpecification;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

import junit.framework.TestCase;

public class DefaultComponentRegistryTestCase extends TestCase {
	private ComponentRegistry componentRegistry;
	private ComponentFactory componentFactory;

	protected void setUp() throws Exception {
		componentRegistry = new DefaultComponentRegistry();
		componentFactory = new DefaultComponentFactory();
	}
	
	public void testRegisterComponent() throws PicoIntrospectionException {
		ComponentSpecification componentSpecification =
			createComponentSpecification();
		
		componentRegistry.registerComponent(componentSpecification);
		
		assertTrue(componentRegistry.getComponentSpecifications().contains(
			componentSpecification));
	}
	
	public void testUnregisterComponent() throws PicoIntrospectionException {
		ComponentSpecification componentSpecification =
			createComponentSpecification();
			
		componentRegistry.registerComponent(componentSpecification);
		
		componentRegistry.unregisterComponent(Touchable.class);
		
		assertFalse(componentRegistry.getComponentSpecifications().contains(
			componentSpecification));
	}

	private ComponentSpecification createComponentSpecification() throws PicoIntrospectionException {
		return new ComponentSpecification(componentFactory, Touchable.class,
			SimpleTouchable.class);
	}
}
