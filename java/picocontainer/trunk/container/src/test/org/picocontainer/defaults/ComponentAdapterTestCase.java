/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.defaults;

import junit.framework.TestCase;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoVerificationException;

/**
 * Test AbstractComponentAdapter behaviour
 * @author J&ouml;rg Schaible
 */
public class ComponentAdapterTestCase
        extends TestCase {

    private static class TestComponentAdapter extends AbstractComponentAdapter {
        protected TestComponentAdapter(Object componentKey, Class componentImplementation, ComponentMonitor componentMonitor) throws AssignabilityRegistrationException {
            super(componentKey, componentImplementation, componentMonitor);
        }
        protected TestComponentAdapter(Object componentKey, Class componentImplementation) throws AssignabilityRegistrationException {
            super(componentKey, componentImplementation);
        }
        public Object getComponentInstance(PicoContainer container) throws PicoInitializationException, PicoIntrospectionException {
            return null;
        }
        public void verify(PicoContainer container) throws PicoVerificationException {
        }
        
    }

    private static class TestDecoratingComponentAdapter extends DecoratingComponentAdapter {
        protected TestDecoratingComponentAdapter(ComponentAdapter adapter, ComponentMonitor componentMonitor) throws AssignabilityRegistrationException {
            super(adapter, componentMonitor);
        }
        protected TestDecoratingComponentAdapter(ComponentAdapter adapter) throws AssignabilityRegistrationException {
            super(adapter);
        }
        public Object getComponentInstance(PicoContainer container) throws PicoInitializationException, PicoIntrospectionException {
            return null;
        }
        public void verify(PicoContainer container) throws PicoVerificationException {
        }
        
    }
    
    public void testComponentImplementationMayNotBeNull() {
        try {
            new TestComponentAdapter("Key", null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            assertEquals("componentImplementation", e.getMessage());
        }
    }

    public void testComponentKeyCanBeNullButNotRequested() {
        ComponentAdapter componentAdapter = new TestComponentAdapter(null, String.class);
        try {
            componentAdapter.getComponentKey();
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            assertEquals("componentKey", e.getMessage());
        }
    }

    public void testComponentMonitorMayNotBeNull() {
        try {
            new TestComponentAdapter("Key", String.class, null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            assertEquals("componentMonitor", e.getMessage());
        }
        try {
            new TestDecoratingComponentAdapter(new TestComponentAdapter("Key", String.class), null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            assertEquals("componentMonitor", e.getMessage());
        }
    }
    
    
    public void testStringRepresentation() {
        ComponentAdapter componentAdapter = new TestComponentAdapter("Key", Integer.class);
        assertEquals(TestComponentAdapter.class.getName() + "[Key]", componentAdapter.toString());
    }
}
