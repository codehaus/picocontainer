/*****************************************************************************
 * Copyright (C) OldPicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Stacy Curl                        *
 *****************************************************************************/

package org.picocontainer.defaults;

import junit.framework.TestCase;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

public class DefaultComponentRegistryTestCase extends TestCase {
	private AbstractPicoContainer componentRegistry;

	protected void setUp() throws Exception {
		componentRegistry = new DefaultPicoContainer();
	}
	
	public void testRegisterComponent() throws PicoRegistrationException {
		ComponentAdapter componentSpecification = createComponentAdapter();
		
		componentRegistry.registerComponent(componentSpecification);
		
		assertTrue(componentRegistry.getComponentAdapters().contains(componentSpecification));
	}
	
	public void testUnregisterComponent() throws PicoRegistrationException {
		ComponentAdapter componentSpecification = createComponentAdapter();
			
		componentRegistry.registerComponent(componentSpecification);
		
		componentRegistry.unregisterComponent(Touchable.class);
		
		assertFalse(componentRegistry.getComponentAdapters().contains(componentSpecification));
	}

	private ComponentAdapter createComponentAdapter() throws AssignabilityRegistrationException, NotConcreteRegistrationException {
		return new DefaultComponentAdapter(Touchable.class, SimpleTouchable.class);
	}
}
