/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 * C# port by Maarten Grootendorst                                           *
 *****************************************************************************/

using System;
using System.Runtime.InteropServices;
using System.Collections;

namespace PicoContainer
{
  public interface PicoContainer 
  {
    /**
     * Gets a component instance.
     *
     * @param componentKey key the component was Registered with.
     * @return an instantiated component.
     * @throws PicoException if the component could not be instantiated or dependencies
     *    could not be properly resolved.
     */
    object GetComponentInstance(object componentKey);

    /**
     * Gets all the Registered component instances in the container.
     * The components are returned in their order of instantiation, which
     * depends on the dependency order between components.
     *
     * @return all the components.
     * @throws PicoException if one of the components could not be instantiated or dependencies
     *    could not be properly resolved.
     */
    ArrayList ComponentInstances {
      get;
    }

    /**
     * Checks for the presence of a particular component.
     *
     * @param componentKey key of the component to look for.
     * @return true if there is a component for this key.
     */
    bool HasComponent(object componentKey);

    /**
     * Returns an object (in fact, a dynamic proxy) that implements the union
     * of all the interfaces of the currently Registered components.
     * <p>
     * Casting this object to any of those interfaces and then calling a method
     * on it will result in that call being multicast to all the components implementing
     * that given interface.
     * <p>
     * This is a simple yet extremely powerful way to handle lifecycle of components.
     * Component writers can invent their own lifecycle interfaces, and then use the multicaster
     * to invoke the method in one go.
     *
     * @param callInInstantiationOrder whether or not to call the method in the order of instantiation,
     *    which depends on the components' inter-dependencies.
     * @param callUnmanagedComponents whether or not to multicast to components that are not managed
     *    by this container.
     * @return a multicaster object.
     */
    object GetComponentMulticaster(bool callInInstantiationOrder, bool callUnmanagedComponents);

    /**
     * Shorthand for {@link #GetComponentMulticaster(bool, bool)}<pre>(true, false)</pre>,
     * which is the most common usage scenario.
     *
     * @return a multicaster object.
     * @throws PicoException
     */
    object GetComponentMulticaster();

    /**
     * Get all the component keys.
     * @return all the component keys.
     */
    ICollection ComponentKeys {
      get;
    }

    /**
     * Get the child containers of this container. Any given container instance should not use
     * the child containers to resolve components, but rahter their parents. This method
     * is available merely to be able to traverse trees of containers, and is not used by the
     * container itself.
     * @return a Collection of {@link PicoContainer}.
     * @see #GetParentContainers()
     */
    ArrayList ChildContainers {
      get;
    }

    /**
     * Get the parent containers of this container. In a purely hierarchical (tree structure) container,
     * there will be 0..1 parents. However, it is possible to have several parents.
     * A container will look in its parents if a component can be found in self.
     *
     * @return a Collection of {@link PicoContainer}.
     */
    ArrayList ParentContainers{
      get;
    }

    /**
     * Finds a ComponentAdapter matching the key. This method is an "expert" method, and should
     * normally not be called by clients of this API. (It is called by the implementation).
     *
     * @param componentKey key of the component.
     */
    ComponentAdapter FindComponentAdapter(object componentKey);
  }
}
