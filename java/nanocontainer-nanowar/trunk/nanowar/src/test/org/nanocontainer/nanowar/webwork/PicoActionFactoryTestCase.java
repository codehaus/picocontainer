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
 * @author Konstantin Pribluda
 * @author Mauro Talevi
 */
public class PicoActionFactoryTestCase extends TestCase {

    private PicoActionFactory factory;
    private DefaultPicoContainer container;
    
    public void setUp(){
        factory = new PicoActionFactory();
        container = new DefaultPicoContainer();
    }
    
	public void testActionInstantiationWithValidClassName() throws Exception {
		container.registerComponentInstance("foo");
		(new ActionContextScopeObjectReference(KeyConstants.REQUEST_CONTAINER))
				.set(container);
		TestAction action = (TestAction) factory
				.getActionImpl("org.nanocontainer.nanowar.webwork.TestAction");
		assertNotNull(action);
		assertEquals("foo", action.getFoo());
	}
    
    public void testActionInstantiationWhichFailsDueToFailedDependencies() throws Exception {
        (new ActionContextScopeObjectReference(KeyConstants.REQUEST_CONTAINER))
                .set(container);
        TestAction action = (TestAction) factory
                .getActionImpl("org.nanocontainer.nanowar.webwork.TestAction");
        assertNull(action);
    }

    public void testActionInstantiationWithInvalidClassName() throws Exception {
        container.registerComponentInstance("foo");
        (new ActionContextScopeObjectReference(KeyConstants.REQUEST_CONTAINER))
                .set(container);
        TestAction action = (TestAction) factory
                .getActionImpl("invalidAction");
        assertNull(action);
    }

    public void testActionInstantiationWhichHasAlreadyBeenRegistered() throws Exception {
        container.registerComponentInstance("foo");
        container.registerComponentImplementation(TestAction.class);
        (new ActionContextScopeObjectReference(KeyConstants.REQUEST_CONTAINER))
                .set(container);
        TestAction action1 = (TestAction) container.getComponentInstance(TestAction.class);
        TestAction action2 = (TestAction) factory
                .getActionImpl("org.nanocontainer.nanowar.webwork.TestAction");
        assertSame(action1, action2);
    }

    public void testActionInstantiationWhichHasAlreadyBeenRequested() throws Exception {
        container.registerComponentInstance("foo");
        (new ActionContextScopeObjectReference(KeyConstants.REQUEST_CONTAINER))
                .set(container);
        TestAction action1 = (TestAction) factory
                .getActionImpl("org.nanocontainer.nanowar.webwork.TestAction");
        TestAction action2 = (TestAction) factory
                .getActionImpl("org.nanocontainer.nanowar.webwork.TestAction");
        assertNotSame(action1, action2);
    }
}