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
import org.picocontainer.tck.AbstractComponentAdapterFactoryTestCase;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.internals.ComponentAdapter;
import org.picocontainer.internals.ComponentAdapterFactory;

import java.util.ArrayList;
import java.util.List;

public class ImplementationHidingComponentAdapterFactoryTestCase extends AbstractComponentAdapterFactoryTestCase {

    private static boolean addCalled = false;

    public static class OneConstructorArrayList extends ArrayList {
        public OneConstructorArrayList() {
            super();
        }

        public boolean add(Object o) {
            addCalled = true;
            return super.add(o);
        }
    }

    public void testCreatedComponentAdapterCreatesInstancesWhereImplementationIsHidden()
            throws NoSuchMethodException, PicoInitializationException {
        ComponentAdapter componentAdapter = createComponentAdapterFactory().createComponentAdapter(List.class, OneConstructorArrayList.class, null);
        Object o = componentAdapter.instantiateComponent(componentRegistry);
        assertTrue(o instanceof List);
        assertFalse(o instanceof OneConstructorArrayList);
        ((List) o).add("hello");
        assertTrue("Add was called", addCalled);
    }

    protected ComponentAdapterFactory createComponentAdapterFactory() {
        return new ImplementationHidingComponentAdapterFactory(new DefaultComponentAdapterFactory());
    }

}
