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

import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

import junit.framework.TestCase;

public class ComponentAdapterTestCase extends TestCase {
	public void testEquals() throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
		ComponentAdapter componentAdapter =
			createComponentAdapter();

		assertEquals(componentAdapter, componentAdapter);
	}

	private ComponentAdapter createComponentAdapter() throws AssignabilityRegistrationException, NotConcreteRegistrationException {
		return new DefaultComponentAdapter(Touchable.class, SimpleTouchable.class);
	}
}
