/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar.webwork2;

import javax.servlet.http.HttpServletRequest;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.nanocontainer.nanowar.KeyConstants;
import org.nanocontainer.nanowar.webwork2.PicoObjectFactory;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.UnsatisfiableDependenciesException;
import org.picocontainer.gems.adapters.ThreadLocalReference;

/**
 * @author Mauro Talevi
 * @author Konstantin Pribluda
 */
public class PicoObjectFactoryTestCase extends MockObjectTestCase {

    private PicoObjectFactory factory;
    private DefaultPicoContainer container;
    private Mock requestMock = mock(HttpServletRequest.class);
    private HttpServletRequest request = (HttpServletRequest) requestMock.proxy();
    
    public void setUp(){
        container = new DefaultPicoContainer();
        ObjectReference reference = new ThreadLocalReference();
        reference.set(request);
        factory = new PicoObjectFactory(reference);
    }
    
	public void testActionInstantiationWithValidClassName() throws Exception {
        requestMock.expects(atLeastOnce()).method("getAttribute").with(eq(KeyConstants.ACTIONS_CONTAINER)).will(
                returnValue(null));
        requestMock.expects(atLeastOnce()).method("getAttribute").with(eq(KeyConstants.REQUEST_CONTAINER)).will(
                returnValue(container));
        requestMock.expects(atLeastOnce()).method("setAttribute").with(eq(KeyConstants.ACTIONS_CONTAINER),
                isA(MutablePicoContainer.class));
		container.registerComponentInstance("foo");
		TestAction action = (TestAction) factory
				.buildBean(TestAction.class.getName());
		assertNotNull(action);
		assertEquals("foo", action.getFoo());
	}
    
    public void testActionInstantiationWhichFailsDueToFailedDependencies() throws Exception {
        requestMock.expects(atLeastOnce()).method("getAttribute").with(eq(KeyConstants.ACTIONS_CONTAINER)).will(
                returnValue(null));
        requestMock.expects(atLeastOnce()).method("getAttribute").with(eq(KeyConstants.REQUEST_CONTAINER)).will(
                returnValue(container));
        requestMock.expects(atLeastOnce()).method("setAttribute").with(eq(KeyConstants.ACTIONS_CONTAINER),
                isA(MutablePicoContainer.class));
        TestAction action = null;
        try {
            action = (TestAction) factory
                            .buildBean(TestAction.class.getName());
            fail("should have barfed");
        } catch (UnsatisfiableDependenciesException e) {
            // expected
        }
        assertNull(action);
    }

    public void testActionInstantiationWithInvalidClassName() throws Exception {
        try {
            factory.buildBean("invalidAction");
            fail("PicoIntrospectionException expected");
        } catch ( PicoIntrospectionException e) {
            // expected
        }
    }

    public void testActionInstantiationWhichHasAlreadyBeenRegistered() throws Exception {
        requestMock.expects(atLeastOnce()).method("getAttribute").with(eq(KeyConstants.ACTIONS_CONTAINER)).will(
                returnValue(null));
        requestMock.expects(atLeastOnce()).method("getAttribute").with(eq(KeyConstants.REQUEST_CONTAINER)).will(
                returnValue(container));
        requestMock.expects(atLeastOnce()).method("setAttribute").with(eq(KeyConstants.ACTIONS_CONTAINER),
                isA(MutablePicoContainer.class));
        container.registerComponentInstance("foo");
        container.registerComponentImplementation(TestAction.class);
        TestAction action1 = (TestAction) container.getComponentInstance(TestAction.class);
        TestAction action2 = (TestAction) factory
                .buildBean(TestAction.class.getName());
        assertSame(action1, action2);
    }

    /**
     * if component was not registered explicitely,  there shall be different instance for
     * next invocation.  not only actions are instantiated via factory,  but also important stuff like filters,
     * validators, interceptors etc - they shall not be shared. 
     * @throws Exception
     */
    public void testActionInstantiationWhichHasAlreadyBeenRequested() throws Exception {
        requestMock.expects(atLeastOnce()).method("getAttribute").with(eq(KeyConstants.ACTIONS_CONTAINER)).will(
                returnValue(container));
        container.registerComponentInstance("foo");
        TestAction action1 = (TestAction) factory
                .buildBean(TestAction.class.getName());
        TestAction action2 = (TestAction) factory
                .buildBean(TestAction.class.getName());
        assertNotSame(action1, action2);
    }
    

}