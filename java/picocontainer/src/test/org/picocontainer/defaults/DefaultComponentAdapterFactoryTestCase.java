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

import org.picocontainer.tck.AbstractComponentAdapterFactoryTestCase;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.testmodel.Touchable;
import org.picocontainer.testmodel.SimpleTouchable;

public class DefaultComponentAdapterFactoryTestCase extends AbstractComponentAdapterFactoryTestCase {
    protected ComponentAdapterFactory createComponentAdapterFactory() {
        return new DefaultComponentAdapterFactory();
    }

    public void testInstantiateComponentWithNoDependencies() throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        ComponentAdapter componentAdapter =
                createComponentAdapterFactory().createComponentAdapter(Touchable.class, SimpleTouchable.class, null);

        Object comp = componentAdapter.getComponentInstance(picoContainer);
        assertNotNull(comp);
        assertTrue(comp instanceof SimpleTouchable);
    }

    public void testSingleUseComponentCanBeInstantiatedByDefaultComponentAdapter() {
        ComponentAdapter componentAdapter = createComponentAdapterFactory().createComponentAdapter("o", Object.class, null);
        Object component = componentAdapter.getComponentInstance(new DefaultPicoContainer());
        assertNotNull(component);
    }
}
