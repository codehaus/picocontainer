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
import org.picocontainer.internals.ComponentAdapterFactory;
import org.picocontainer.internals.ComponentAdapter;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.testmodel.Touchable;
import org.picocontainer.testmodel.SimpleTouchable;

public class DefaultComponentAdapterFactoryTestCase extends AbstractComponentAdapterFactoryTestCase {
    protected ComponentAdapterFactory createComponentAdapterFactory() {
        return new DefaultComponentAdapterFactory();
    }

    public void testInstantiateComponentWithNoDependencies() throws PicoInitializationException {
        ComponentAdapter componentAdapter =
                createComponentAdapterFactory().createComponentAdapter(Touchable.class, SimpleTouchable.class, null);

        Object comp = componentAdapter.instantiateComponent(componentRegistry);
        assertNotNull(comp);
        assertTrue(comp instanceof SimpleTouchable);
    }

}
