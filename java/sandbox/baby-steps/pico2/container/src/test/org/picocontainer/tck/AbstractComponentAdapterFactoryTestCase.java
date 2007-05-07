/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.tck;

import junit.framework.TestCase;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.ComponentCharacteristics;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public abstract class AbstractComponentAdapterFactoryTestCase extends TestCase {
    protected DefaultPicoContainer picoContainer;

    protected abstract ComponentAdapterFactory createComponentAdapterFactory();

    protected void setUp() throws Exception {
        picoContainer = new DefaultPicoContainer();
    }

    public void testEquals() throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        ComponentAdapter componentAdapter = createComponentAdapterFactory().createComponentAdapter(ComponentCharacteristics.CDI, Touchable.class, SimpleTouchable.class, null);

        assertEquals(componentAdapter, componentAdapter);
        assertTrue(!componentAdapter.equals("blah"));
    }

    public void testRegisterComponent() throws PicoRegistrationException, AssignabilityRegistrationException {
        ComponentAdapter componentAdapter =
                createComponentAdapterFactory().createComponentAdapter(ComponentCharacteristics.CDI, Touchable.class, SimpleTouchable.class, null);

        picoContainer.addAdapter(componentAdapter);

        assertTrue(picoContainer.getComponentAdapters().contains(componentAdapter));
    }

    public void testUnregisterComponent() throws PicoRegistrationException, AssignabilityRegistrationException {
        ComponentAdapter componentAdapter =
                createComponentAdapterFactory().createComponentAdapter(ComponentCharacteristics.CDI, Touchable.class, SimpleTouchable.class, null);

        picoContainer.addAdapter(componentAdapter);
        picoContainer.removeComponent(Touchable.class);

        assertFalse(picoContainer.getComponentAdapters().contains(componentAdapter));
    }
}
