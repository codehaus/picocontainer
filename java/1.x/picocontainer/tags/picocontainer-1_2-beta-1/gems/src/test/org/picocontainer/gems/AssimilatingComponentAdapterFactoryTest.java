/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaibe                                            *
 *****************************************************************************/

package org.picocontainer.gems;

import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.DuplicateComponentKeyRegistrationException;
import org.picocontainer.tck.AbstractComponentAdapterFactoryTestCase;
import org.picocontainer.testmodel.AlternativeTouchable;
import org.picocontainer.testmodel.CompatibleTouchable;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

import java.util.List;

/**
 * @author J&ouml;rg Schaible
 */
public class AssimilatingComponentAdapterFactoryTest extends AbstractComponentAdapterFactoryTestCase {

    /**
     * @see org.picocontainer.tck.AbstractComponentAdapterFactoryTestCase#createComponentAdapterFactory()
     */
    protected ComponentAdapterFactory createComponentAdapterFactory() {
        return new AssimilatingComponentAdapterFactory(new ConstructorInjectionComponentAdapterFactory(), Touchable.class);
    }

    /**
     * Test automatic assimilation of registered components.
     */
    public void testAutomaticAssimilation() {
        picoContainer = new DefaultPicoContainer(createComponentAdapterFactory());
        picoContainer.registerComponentImplementation(SimpleTouchable.class);
        picoContainer.registerComponentImplementation(AlternativeTouchable.class);
        picoContainer.registerComponentImplementation(CompatibleTouchable.class);
        final List list = picoContainer.getComponentInstancesOfType(Touchable.class);
        assertEquals(3, list.size());
    }

    /**
     * Test automatic assimilation of registered components.
     */
    public void testOnlyOneTouchableComponentKeyPossible() {
        picoContainer = new DefaultPicoContainer(createComponentAdapterFactory());
        picoContainer.registerComponentImplementation(Touchable.class, SimpleTouchable.class);
        try {
            picoContainer.registerComponentImplementation(CompatibleTouchable.class);
            fail("DuplicateComponentKeyRegistrationException expected");
        } catch(final DuplicateComponentKeyRegistrationException e) {
            // fine
        }
    }

    /**
     * Test automatic assimilation of registered components.
     */
    public void testMultipleAssimilatedComponentsWithUserDefinedKeys() {
        picoContainer = new DefaultPicoContainer(createComponentAdapterFactory());
        picoContainer.registerComponentImplementation(Touchable.class, SimpleTouchable.class);
        picoContainer.registerComponentImplementation("1", CompatibleTouchable.class);
        picoContainer.registerComponentImplementation("2", CompatibleTouchable.class);
        picoContainer.registerComponentImplementation("3", CompatibleTouchable.class);
        final List list = picoContainer.getComponentInstancesOfType(Touchable.class);
        assertEquals(4, list.size());
    }
}
