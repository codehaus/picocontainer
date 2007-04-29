/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Stacy Curl                        *
 *****************************************************************************/

package org.picocontainer.defaults;

import junit.framework.TestCase;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.componentadapters.ConstructorInjectionComponentAdapter;
import org.picocontainer.testmodel.AlternativeTouchable;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

public class DefaultComponentRegistryTestCase extends TestCase {
    private DefaultPicoContainer picoContainer;

    protected void setUp() throws Exception {
        picoContainer = new DefaultPicoContainer();
    }

    public void testRegisterComponent() throws PicoRegistrationException {
        ComponentAdapter componentAdapter = createComponentAdapter();
        picoContainer.registerComponent(componentAdapter);
        assertTrue(picoContainer.getComponentAdapters().contains(componentAdapter));
    }

    public void testUnregisterComponent() throws PicoRegistrationException {
        ComponentAdapter componentAdapter = createComponentAdapter();
        picoContainer.registerComponent(componentAdapter);
        picoContainer.unregisterComponent(Touchable.class);
        assertFalse(picoContainer.getComponentAdapters().contains(componentAdapter));
    }

    public void testCannotInstantiateAnUnregisteredComponent() throws PicoRegistrationException, AssignabilityRegistrationException, NotConcreteRegistrationException, PicoInvocationTargetInitializationException, PicoInitializationException {
        ComponentAdapter componentAdapter = createComponentAdapter();
        picoContainer.registerComponent(componentAdapter);
        picoContainer.getComponents();
        picoContainer.unregisterComponent(Touchable.class);

        assertTrue(picoContainer.getComponents().isEmpty());
    }

    public void testCanInstantiateReplacedComponent() throws PicoRegistrationException, AssignabilityRegistrationException, NotConcreteRegistrationException, PicoInvocationTargetInitializationException, PicoInitializationException {
        ComponentAdapter componentAdapter = createComponentAdapter();
        picoContainer.registerComponent(componentAdapter);
        picoContainer.getComponents();
        picoContainer.unregisterComponent(Touchable.class);

        picoContainer.registerComponent(Touchable.class, AlternativeTouchable.class);

        assertEquals("Container should container 1 component",
                1, picoContainer.getComponents().size());
    }

    public void testUnregisterAfterInstantiateComponents() throws PicoRegistrationException, AssignabilityRegistrationException, PicoInitializationException, DuplicateComponentKeyRegistrationException, PicoInvocationTargetInitializationException, AmbiguousComponentResolutionException {
        ComponentAdapter componentAdapter = createComponentAdapter();
        picoContainer.registerComponent(componentAdapter);
        picoContainer.getComponents();
        picoContainer.unregisterComponent(Touchable.class);
        assertNull(picoContainer.getComponent(Touchable.class));
    }

    public void testReplacedInstantiatedComponentHasCorrectClass() throws PicoRegistrationException, AssignabilityRegistrationException, NotConcreteRegistrationException, PicoInvocationTargetInitializationException, PicoInitializationException {
        ComponentAdapter componentAdapter = createComponentAdapter();
        picoContainer.registerComponent(componentAdapter);
        picoContainer.getComponents();
        picoContainer.unregisterComponent(Touchable.class);

        picoContainer.registerComponent(Touchable.class, AlternativeTouchable.class);
        Object component = picoContainer.getComponents().iterator().next();

        assertEquals(AlternativeTouchable.class, component.getClass());
    }

    private ComponentAdapter createComponentAdapter() throws AssignabilityRegistrationException, NotConcreteRegistrationException {
        return new ConstructorInjectionComponentAdapter(Touchable.class, SimpleTouchable.class);
    }
}
