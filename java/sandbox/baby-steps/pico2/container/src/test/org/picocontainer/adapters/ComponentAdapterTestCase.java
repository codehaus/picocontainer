/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.adapters;

import junit.framework.TestCase;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoVerificationException;
import org.picocontainer.PicoVisitor;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.AmbiguousComponentResolutionException;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.parameters.ConstantParameter;
import org.picocontainer.adapters.AbstractComponentAdapter;
import org.picocontainer.adapters.InjectingAdapter;
import org.picocontainer.adapters.MonitoringAdapter;

import java.lang.reflect.Constructor;

/**
 * Test AbstractComponentAdapter behaviour
 * @author J&ouml;rg Schaible
 */
public class ComponentAdapterTestCase
        extends TestCase {

    private static class TestComponentAdapter extends AbstractComponentAdapter {
        TestComponentAdapter(Object componentKey, Class componentImplementation, ComponentMonitor componentMonitor) throws AssignabilityRegistrationException {
            super(componentKey, componentImplementation, componentMonitor);
        }
        TestComponentAdapter(Object componentKey, Class componentImplementation) throws AssignabilityRegistrationException {
            super(componentKey, componentImplementation);
        }
        public Object getComponentInstance(PicoContainer container) throws PicoInitializationException, PicoIntrospectionException {
            return null;
        }
        public void verify(PicoContainer container) throws PicoVerificationException {
        }
        
    }

    private static class TestMonitoringComponentAdapter extends MonitoringAdapter {
        TestMonitoringComponentAdapter(ComponentMonitor componentMonitor) throws AssignabilityRegistrationException {
            super(componentMonitor);
        }
        public Object getComponentInstance(PicoContainer container) throws PicoInitializationException, PicoIntrospectionException {
            return null;
        }
        public void verify(PicoContainer container) throws PicoVerificationException {
        }
        public Object getComponentKey() {
            return null;
        }
        public Class getComponentImplementation() {
            return null;
        }
        public void accept(PicoVisitor visitor) {
        }        
    }
    
    private static class TestInstantiatingComponentAdapter extends InjectingAdapter {
        TestInstantiatingComponentAdapter(Object componentKey, Class componentImplementation, Parameter[] parameters) {
            super(componentKey, componentImplementation, parameters);
        }
        protected Constructor getGreediestSatisfiableConstructor(PicoContainer container) throws PicoIntrospectionException, AmbiguousComponentResolutionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
            return null;
        }

        public void verify(PicoContainer container) throws PicoIntrospectionException {
        }

        public Object getComponentInstance(PicoContainer container) throws PicoInitializationException, PicoIntrospectionException {
            return null;
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
            assertEquals("monitor", e.getMessage());
        }
        try {
            new TestMonitoringComponentAdapter(null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            assertEquals("monitor", e.getMessage());
        }
    }

    public void testParameterMayNotBeNull() throws Exception {
        try {
            new TestInstantiatingComponentAdapter("Key", String.class, new Parameter[]{new ConstantParameter("Value"), null});
            fail("Thrown " + NullPointerException.class.getName() + " expected");
        } catch (final NullPointerException e) {
            assertTrue(e.getMessage().endsWith("1 is null"));
        }
    }
    
    public void testStringRepresentation() {
        ComponentAdapter componentAdapter = new TestComponentAdapter("Key", Integer.class);
        assertEquals(TestComponentAdapter.class.getName() + "[Key]", componentAdapter.toString());
    }
}
