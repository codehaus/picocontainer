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
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.internals.ComponentAdapter;
import org.picocontainer.internals.ComponentRegistry;
import org.picocontainer.internals.Parameter;
import org.picocontainer.defaults.DefaultComponentAdapter;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

public class DefaultComponentAdapterTestCase extends TestCase {
	private ComponentRegistry componentRegistry;

	protected void setUp() throws Exception {
		componentRegistry = new DefaultComponentRegistry();
	}

	public void testRegisterComponent() throws PicoIntrospectionException {
		ComponentAdapter componentAdapter = createComponentAdapter();

		componentRegistry.registerComponent(componentAdapter);

		assertTrue(componentRegistry.getComponentAdapters().contains(componentAdapter));
	}

	public void testUnregisterComponent() throws PicoIntrospectionException {
		ComponentAdapter componentSpecification = createComponentAdapter();

		componentRegistry.registerComponent(componentSpecification);

		componentRegistry.unregisterComponent(Touchable.class);

		assertFalse(componentRegistry.getComponentAdapters().contains(componentSpecification));
	}

	private ComponentAdapter createComponentAdapter() {
		ComponentAdapter adapter = new DefaultComponentAdapter(Touchable.class, SimpleTouchable.class);
        return adapter;
	}

    public void testInstantiateComponentWithNoDependencies() throws PicoInitializationException {
        ComponentAdapter componentAdapter = new DefaultComponentAdapter(SimpleTouchable.class, SimpleTouchable.class, new Parameter[0]);
        DefaultComponentRegistry componentRegistry = new DefaultComponentRegistry();
        Object comp = componentAdapter.instantiateComponent(componentRegistry);
        assertNotNull(comp);
        assertTrue(comp instanceof SimpleTouchable);
    }

}
