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

import junit.framework.TestCase;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

public class DefaultComponentRegistryTestCase extends TestCase {
	private DefaultPicoContainer picoContainer;

	protected void setUp() throws Exception {
		picoContainer = new DefaultPicoContainer();
	}

	public void testRegisterComponent() throws PicoRegistrationException {
		ComponentAdapter componentSpecification = createComponentAdapter();

		picoContainer.registerComponent(componentSpecification);

		assertTrue(picoContainer.getComponentAdapters().contains(componentSpecification));
	}

	public void testUnregisterComponent() throws PicoRegistrationException {
		ComponentAdapter componentSpecification = createComponentAdapter();

		picoContainer.registerComponent(componentSpecification);

		picoContainer.unregisterComponent(Touchable.class);

		assertFalse(picoContainer.getComponentAdapters().contains(componentSpecification));
	}

	private ComponentAdapter createComponentAdapter() throws AssignabilityRegistrationException, NotConcreteRegistrationException {
		return new ConstructorComponentAdapter(Touchable.class, SimpleTouchable.class);
	}
}
