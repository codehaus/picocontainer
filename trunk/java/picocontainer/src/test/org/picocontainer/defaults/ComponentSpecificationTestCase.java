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
import org.picocontainer.defaults.DefaultComponentAdapter;
import org.picocontainer.internals.ComponentAdapter;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

import junit.framework.TestCase;

public class ComponentSpecificationTestCase extends TestCase {
	public void testEquals() throws PicoIntrospectionException {
		ComponentAdapter componentAdapter =
			createComponentAdapter();
		
		assertEquals(componentAdapter, componentAdapter);
	}
	
	private ComponentAdapter createComponentAdapter() throws PicoIntrospectionException {
		return new DefaultComponentAdapter(Touchable.class, SimpleTouchable.class);
	}
}
