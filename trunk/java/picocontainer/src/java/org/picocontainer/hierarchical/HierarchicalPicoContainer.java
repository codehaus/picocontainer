/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

/*
TODO (Aslak):

1) Factor out a DependencyAnalyzer:
   public interface DependencyAnalyzer {
	   InstantiationSpecification[] getOrderedInstantiationSpecifications();
   }

   ConstructorDependencyAnalyzer would emerge from refactoring this class.

2) Refactor the ContainerFactory's createComponent method to take a
   InstantiationSpecification argument. This class/intf should contain'
   everything needed to instantiate a component.

*/

package org.picocontainer.hierarchical;

import org.picocontainer.ComponentFactory;
import org.picocontainer.ComponentRegistry;
import org.picocontainer.PicoContainer;
import org.picocontainer.RegistrationPicoContainer;
import org.picocontainer.defaults.DefaultComponentFactory;
import org.picocontainer.defaults.DefaultComponentRegistry;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.NullContainer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HierarchicalPicoContainer extends DefaultPicoContainer implements RegistrationPicoContainer {

    private final PicoContainer parentContainer;

    public HierarchicalPicoContainer(ComponentFactory componentFactory,
                                     PicoContainer parentContainer, ComponentRegistry componentRegistry) {
        super(componentFactory, componentRegistry);

        if (parentContainer == null) {
            throw new NullPointerException("parentContainer cannot be null");
        }
        this.parentContainer = parentContainer;
    }

    public static class Default extends HierarchicalPicoContainer {
        public Default() {
            super(new DefaultComponentFactory(), new NullContainer(), new DefaultComponentRegistry());
        }

    }

    public static class WithParentContainer extends HierarchicalPicoContainer {
        public WithParentContainer(PicoContainer parentContainer) {
            super(new DefaultComponentFactory(), parentContainer, new DefaultComponentRegistry());
        }
    }

    public static class WithComponentFactory extends HierarchicalPicoContainer {
        public WithComponentFactory(ComponentFactory componentFactory) {
            super(componentFactory, new NullContainer(), new DefaultComponentRegistry());
        }
    }

    public Object getComponent(Object componentKey) {
        // First look in myself
        Object result = super.getComponent(componentKey);

        // Then look in parent if we had nothing
        if (result == null) {
            result = parentContainer.getComponent(componentKey);
        }
        return result;
    }

    public Object[] getComponentKeys() {
        // Get my own types
        List myTypes = Arrays.asList(super.getComponentKeys());

        // Get those from my parent.
        Set types = new HashSet(myTypes);
        types.addAll(Arrays.asList(parentContainer.getComponentKeys()));

        return (Class[]) types.toArray(new Class[types.size()]);
    }

}
