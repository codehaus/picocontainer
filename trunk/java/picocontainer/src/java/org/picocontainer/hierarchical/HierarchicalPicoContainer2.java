/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.hierarchical;

import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.NullContainer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.Serializable;

public class HierarchicalPicoContainer2 implements PicoContainer, Serializable {

    private final PicoContainer parentContainer;
    private final PicoContainer childContainer;

    /**
     * Note - PicoContainer beind specified twice in the args here, makes this component not-naturally pico-compatible.
     * @param parentContainer
     * @param childContainer
     */
    public HierarchicalPicoContainer2(PicoContainer parentContainer, PicoContainer childContainer) {
        if (parentContainer == null) {
            throw new NullPointerException("parentContainer cannot be null");
        }
        if (childContainer == null) {
            throw new NullPointerException("childContainer cannot be null");
        }
        this.parentContainer = parentContainer;
        this.childContainer = childContainer;
    }

    public static class Default extends HierarchicalPicoContainer2 {
        public Default() {
            super(new NullContainer(), new DefaultPicoContainer.Default());
        }

    }

    public static class WithParentContainer extends HierarchicalPicoContainer2 {
        public WithParentContainer(PicoContainer parentContainer) {
            super(parentContainer, new DefaultPicoContainer.Default());
        }
    }


    public Object getComponent(Object componentKey) {
        // First look in child
        Object result = childContainer.getComponent(componentKey);

        // Then look in parent if we had nothing
        if (result == null) {
            result = parentContainer.getComponent(componentKey);
        }
        return result;
    }

    public Object[] getComponentKeys() {
        // Get child types
        List myTypes = Arrays.asList(childContainer.getComponentKeys());

        // Get those from parent.
        Set types = new HashSet(myTypes);
        types.addAll(Arrays.asList(parentContainer.getComponentKeys()));

        return (Class[]) types.toArray(new Class[types.size()]);
    }

    public boolean hasComponent(Object componentKey) {
        return getComponent(componentKey) != null;
    }

    public Object[] getComponents() {
        // Get child types
        List myTypes = Arrays.asList(childContainer.getComponents());

        // Get those from parent.
        Set types = new HashSet(myTypes);
        types.addAll(Arrays.asList(parentContainer.getComponents()));

        return (Class[]) types.toArray(new Class[types.size()]);
    }

    public void instantiateComponents() throws PicoInitializationException {
        childContainer.instantiateComponents();
    }

    public Object getCompositeComponent() {
        throw new java.lang.UnsupportedOperationException();
    }

    public Object getCompositeComponent(boolean callInInstantiationOrder, boolean callUnmanagedComponents) {
        throw new java.lang.UnsupportedOperationException();
    }
}
