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
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.testmodel.AlternativeTouchable;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

public class DefaultPicoContainerUnregisterComponentTestCase extends TestCase {
    private DefaultPicoContainer picoContainer;

    protected void setUp() throws Exception {
        picoContainer = new DefaultPicoContainer();
    }

    public void testCannotInstantiateAnUnregisteredComponent() throws PicoRegistrationException, AssignabilityRegistrationException, NotConcreteRegistrationException, PicoInvocationTargetInitializationException, PicoInitializationException {
        picoContainer.registerComponentImplementation(Touchable.class, SimpleTouchable.class);
        picoContainer.unregisterComponent(Touchable.class);

        assertTrue(picoContainer.getComponentAdapters().isEmpty());
        assertTrue(picoContainer.getComponentKeys().isEmpty());
        assertTrue(picoContainer.getComponentInstances().isEmpty());
    }

    public void testCanInstantiateReplacedComponent() throws PicoRegistrationException, AssignabilityRegistrationException, NotConcreteRegistrationException, PicoInvocationTargetInitializationException, PicoInitializationException {
        picoContainer.registerComponentImplementation(Touchable.class, SimpleTouchable.class);
        picoContainer.unregisterComponent(Touchable.class);

        picoContainer.registerComponentImplementation(Touchable.class, AlternativeTouchable.class);

        assertEquals("Container should container 1 component",
                1, picoContainer.getComponentInstances().size());
    }

    public void testUnregisterAfterInstantiateComponents() throws PicoRegistrationException, AssignabilityRegistrationException, PicoInitializationException, DuplicateComponentKeyRegistrationException, PicoInvocationTargetInitializationException, AmbiguousComponentResolutionException {
        picoContainer.registerComponentImplementation(Touchable.class, SimpleTouchable.class);
        picoContainer.unregisterComponent(Touchable.class);
        assertNull("instance should be removed after unregistering component", picoContainer.getComponentInstance(Touchable.class));
    }

    public void testReplacedInstantiatedComponentHasCorrectClass() throws PicoRegistrationException, AssignabilityRegistrationException, NotConcreteRegistrationException, PicoInvocationTargetInitializationException, PicoInitializationException {
        picoContainer.registerComponentImplementation(Touchable.class, SimpleTouchable.class);
        picoContainer.unregisterComponent(Touchable.class);

        picoContainer.registerComponentImplementation(Touchable.class, AlternativeTouchable.class);
        Object component = picoContainer.getComponentInstances().get(0);

        assertEquals(AlternativeTouchable.class, component.getClass());
    }
}
