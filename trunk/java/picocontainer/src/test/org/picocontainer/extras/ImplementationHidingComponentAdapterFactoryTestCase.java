/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.extras;

import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.tck.AbstractComponentAdapterFactoryTestCase;
import org.picocontainer.defaults.*;

public class ImplementationHidingComponentAdapterFactoryTestCase extends AbstractComponentAdapterFactoryTestCase {

    private static boolean addCalled = false;

    public static class ReferencesSwappables {
        public final Swappable swappable;

        public ReferencesSwappables(Swappable swappable) {
            this.swappable = swappable;
        }
    }

    public static interface Swappable {
        String getCheese();
    }

    public static class ConcreteSwappable implements Swappable {
        public String getCheese() {
            return "Edam";
        }
    }

    public void testCreatedComponentAdapterCreatesInstancesWhereImplementationIsHidden()
            throws NoSuchMethodException, PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        ComponentAdapter componentAdapter = createComponentAdapterFactory().createComponentAdapter(Swappable.class, ConcreteSwappable.class, null);
        Swappable swappable = (Swappable) componentAdapter.getComponentInstance(picoContainer);
        assertFalse(swappable instanceof ConcreteSwappable);
        assertEquals("Edam", swappable.getCheese());
    }

    public void testHotSwap() {
        ImplementationHidingComponentAdapterFactory componentAdapterFactory = (ImplementationHidingComponentAdapterFactory) createComponentAdapterFactory();
        ComponentAdapter componentAdapter = componentAdapterFactory.createComponentAdapter(Swappable.class, ConcreteSwappable.class, null);
        Swappable swappable = (Swappable) componentAdapter.getComponentInstance(picoContainer);
        assertFalse(swappable instanceof ConcreteSwappable);
        assertEquals("Edam", swappable.getCheese());

        //TODO - what is this last line for, throws something on errror or should have an assert following ?
        componentAdapterFactory.hotSwap(Swappable.class);
    }


    protected ComponentAdapterFactory createComponentAdapterFactory() {
        return new ImplementationHidingComponentAdapterFactory(new DefaultComponentAdapterFactory());
    }

}
