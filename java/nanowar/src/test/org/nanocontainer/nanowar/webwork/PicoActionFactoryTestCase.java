/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar.webwork;

import junit.framework.TestCase;
import org.nanocontainer.nanowar.KeyConstants;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * test capabilities of pico action factory
 * 
 * @author Konstantin Pribluda
 */
public class PicoActionFactoryTestCase extends TestCase {

	/**
	 * test that action is instantiated and receives construtor parameters from
	 * container
	 */
	public void testActionInstantiation() throws Exception {
		PicoActionFactory factory = new PicoActionFactory();
		DefaultPicoContainer container = new DefaultPicoContainer();
		container.registerComponentInstance("foo");
		(new ActionContextScopeObjectReference(KeyConstants.REQUEST_CONTAINER))
				.set(container);
		TestAction action = (TestAction) factory
				.getActionImpl("org.nanocontainer.nanowar.webwork.TestAction");
		assertNotNull(action);
		assertEquals("foo", action.getFoo());
	}
}