/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.txt file.                                                     *
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
     * @param componentKey key the component was registered with.
     * @return an instantiated component.
     * @throws PicoException if the component could not be instantiated or dependencies
     *    could not be properly resolved.
     */

    /// <summary>
    /// Gets a component instance.</summary>
    /// <param name="componentKey">key the component was registered with.</param>
    /// <returns>an instantiated component.</returns>
    /// <exception cref="PicoException">if the component could not be instantiated or dependencies
    /// could not be properly resolved.</exception>
    object GetComponentInstance(object componentKey);
    /**
     * Gets all the registered component instances in the container.
     The components are returned in their order of instantiation, which
     depends on the dependency order between components.
     *
     * @return all the components.
     * @throws PicoException if one of the components could not be instantiated or dependencies
     *    could not be properly resolved.
     */

    /// <summary>
    /// Gets all the registered component instances in the container.</summary>
    /// <remarks>The components are returned in their order of instantiation, which
    /// depends on the dependency order between components.</remarks>
    IList ComponentInstances {
      get;
    }

    /// <summary>
    /// Checks for the presence of a particular component.</summary>
    /// <param name="componentKey">key of the component to look for.</param>
    /// <returns>true if there is a component for this key.</returns>
    bool HasComponent(object componentKey);


    /// <summary>
    /// Get all the component keys.
    /// </summary>
    IList ComponentKeys {
      get;
    }

    /// <summary>
    /// Get the child containers of this container.</summary>
    /// <remarks>Any given container instance should not use
    /// the child containers to resolve components, but rahter their parents. This method
    /// is available merely to be able to traverse trees of containers, and is not used by the
    /// container itself.</remarks>
    IList ChildContainers {
      get;
    }

    /**
     * Get the parent containers of this container. In a purely hierarchical (tree structure) container,
     * there will be 0..1 parents. However, it is possible to have several parents.
     * A container will look in its parents if a component can be found in self.
     *
     * @return a Collection of {@link PicoContainer}.
     */

    /// <summary>
    /// Get the parent containers of this container.</summary>
    /// <remarks>In a purely hierarchical (tree structure) container,
    /// there will be 0..1 parents. However, it is possible to have several parents.
    /// A container will look in its parents if a component can be found in self.
    /// </remarks>
    IList ParentContainers{
      get;
    }

    /**
     * Finds a ComponentAdapter matching the key. 
     *
     * @param componentKey key of the component.
     */

    /// <summary>
    /// Finds a ComponentAdapter matching the key.</summary>
    /// <remarks>This method is an "expert" method, and should
    /// normally not be called by clients of this API. (It is called by the implementation).</remarks>
    /// <param name="componentKey">key of the component.</param>
    /// <returns></returns>
    ComponentAdapter FindComponentAdapter(object componentKey);
  }
}
