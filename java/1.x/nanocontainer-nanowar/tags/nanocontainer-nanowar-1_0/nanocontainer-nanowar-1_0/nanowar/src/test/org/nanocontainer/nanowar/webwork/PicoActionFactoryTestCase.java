/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar.webwork;

import javax.servlet.http.HttpServletRequest;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.nanocontainer.nanowar.KeyConstants;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

import webwork.action.ServletActionContext;

/**
 * @author Konstantin Pribluda
 * @author Mauro Talevi
 */
public class PicoActionFactoryTestCase extends MockObjectTestCase {

    private PicoActionFactory factory;
    private DefaultPicoContainer container;
    
    public void setUp(){
        factory = new PicoActionFactory();
        container = new DefaultPicoContainer();
        (new ActionContextScopeObjectReference(KeyConstants.REQUEST_CONTAINER)).set(container);
    }
    
	public void testActionInstantiationWithValidClassName() throws Exception {
		container.registerComponentInstance("foo");
		TestAction action = (TestAction) factory
				.getActionImpl("org.nanocontainer.nanowar.webwork.TestAction");
		assertNotNull(action);
		assertEquals("foo", action.getFoo());
	}
    
    public void testActionInstantiationWhichFailsDueToFailedDependencies() throws Exception {
        TestAction action = (TestAction) factory
                .getActionImpl("org.nanocontainer.nanowar.webwork.TestAction");
        assertNull(action);
    }

    public void testActionInstantiationWithInvalidClassName() throws Exception {
        container.registerComponentInstance("foo");
        TestAction action = (TestAction) factory
                .getActionImpl("invalidAction");
        assertNull(action);
    }

    public void testActionInstantiationWhichHasAlreadyBeenRegistered() throws Exception {
        container.registerComponentInstance("foo");
        container.registerComponentImplementation(TestAction.class);
        TestAction action1 = (TestAction) container.getComponentInstance(TestAction.class);
        TestAction action2 = (TestAction) factory
                .getActionImpl("org.nanocontainer.nanowar.webwork.TestAction");
        assertSame(action1, action2);
    }

    public void testActionInstantiationWhichHasAlreadyBeenRequested() throws Exception {
        container.registerComponentInstance("foo");
        TestAction action1 = (TestAction) factory
                .getActionImpl("org.nanocontainer.nanowar.webwork.TestAction");
        TestAction action2 = (TestAction) factory
                .getActionImpl("org.nanocontainer.nanowar.webwork.TestAction");
        assertSame(action1, action2);
    }
    
    public void testActionContainerIsFoundInRequest() throws Exception {
        Mock requestMock = mock(HttpServletRequest.class);
        HttpServletRequest request = (HttpServletRequest) requestMock.proxy();
        requestMock.expects(once()).method("getAttribute").with(eq(KeyConstants.ACTIONS_CONTAINER)).will(
                returnValue(null));
        requestMock.expects(once()).method("getAttribute").with(eq(KeyConstants.REQUEST_CONTAINER)).will(
                returnValue(container));
        requestMock.expects(once()).method("setAttribute").with(eq(KeyConstants.ACTIONS_CONTAINER),
                isA(MutablePicoContainer.class));
        ServletActionContext.setRequest(request);
        container.registerComponentInstance("foo");
        TestAction action = (TestAction) factory
                .getActionImpl("org.nanocontainer.nanowar.webwork.TestAction");
        assertNotNull(action);        
    }

}