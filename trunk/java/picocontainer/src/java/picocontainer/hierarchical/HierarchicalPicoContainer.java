/*****************************************************************************
 * Copyright (Cc) PicoContainer Organization. All rights reserved.            *
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

package picocontainer.hierarchical;


import picocontainer.defaults.DefaultPicoContainer;
import picocontainer.ClassRegistrationPicoContainer;
import picocontainer.ComponentFactory;
import picocontainer.PicoContainer;
import picocontainer.defaults.DefaultComponentFactory;
import picocontainer.defaults.NullContainer;
import picocontainer.defaults.AmbiguousComponentResolutionException;

import java.util.List;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class HierarchicalPicoContainer extends DefaultPicoContainer implements ClassRegistrationPicoContainer {

    private final PicoContainer parentContainer;

    public HierarchicalPicoContainer(ComponentFactory componentFactory, PicoContainer parentContainer) {
        super(componentFactory);

        if (parentContainer == null) {
            throw new NullPointerException("parentContainer cannot be null");
        }
        this.parentContainer = parentContainer;
    }

    public static class Default extends HierarchicalPicoContainer {
        public Default() {
            super(new DefaultComponentFactory(), new NullContainer());
        }

    }

    public static class WithParentContainer extends HierarchicalPicoContainer {
        public WithParentContainer(PicoContainer parentContainer) {
            super(new DefaultComponentFactory(), parentContainer);
        }
    }

    public static class WithComponentFactory extends HierarchicalPicoContainer {
        public WithComponentFactory(ComponentFactory componentFactory) {
            super(componentFactory, new NullContainer());
        }
    }

    public Object getComponent(Class componentType) {
        // First look in myself
        Object result = super.getComponent(componentType);

        // Then look in parent if we had nothing
        if (result == null) {
            result = parentContainer.getComponent(componentType);
        }
        return result;
    }

    public Class[] getComponentTypes() {
        // Get my own types
        List myTypes = Arrays.asList(super.getComponentTypes());

        // Get those from my parent.
        Set types = new HashSet(myTypes);
        types.addAll(Arrays.asList(parentContainer.getComponentTypes()));

        return (Class[]) types.toArray(new Class[types.size()]);
    }

}
