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
  /// <summary>This is the core interface for PicoContainer. It only has accessor methods.</summary>
  /// <remarks>In order to register components in a PicoContainer, use a <see cref="PicoContainer.IMutablePicoContainer"/>,
  /// such as <see cref="PicoContainer.Defaults.DefaultPicoContainer"/>.</remarks>
  public interface IPicoContainer : IStartable, IDisposable
  {

    /// <summary>
    /// Gets a component instance registered with a specific key.</summary>
    /// <param name="componentKey">the key the component was registered with.</param>
    /// <returns>an instantiated component.</returns>
    object GetComponentInstance (object componentKey);

    /// <summary>
    /// Finds a component instance matching the type, looking in parent if
    /// not found in self (unless parent is null).
    /// </summary>
    /// <param name="componentType">type of the compontent</param>
    /// <returns>the adapter matching the type</returns>
    object GetComponentInstanceOfType (Type componentType);

    /// <summary>
    /// Gets all the registered component instances in the container (not including 
    /// those in the parent container).</summary>
    /// <remarks>The components are returned in their order of instantiation, which
    /// depends on the dependency order between components.</remarks>
    /// <returns>all the components</returns>
    IList ComponentInstances { get; }

    /// <summary>
    /// Get the parent of this container
    /// </summary>
    IPicoContainer Parent { get; }

    /// <summary>
    /// Finds a ComponentAdapter matching the key, looking in parent if
    /// not found in self (unless parent is null).
    /// </summary>
    /// <param name="componentKey">key of the component</param>
    /// <returns>the adapter matching the key.</returns>
    IComponentAdapter GetComponentAdapter (object componentKey);

    /// <summary>
    /// Finds a ComponentAdapter matching the type, looking in parent if
    /// not found in self (unless parent is null).
    /// </summary>
    /// <param name="componentType">type of the component.</param>
    /// <returns>the adapter matching the type.</returns>
    IComponentAdapter GetComponentAdapterOfType (Type componentType);

    /// <summary>
    /// Returns all adapters (not including the adapters from the parent).
    /// </summary>
    /// <returns>List of <see cref="PicoContainer.IComponentAdapter"/></returns>
    IList ComponentAdapters { get; }

    /// <summary>
    /// Verifies that the dependencies for all the registered components can be satisfied
    /// None of the components are instantiated during the verification process.
    /// </summary>
    /// <exception cref="PicoVerificationException">if there are unsatisifiable dependencies.</exception>
    void Verify ();

    /// <summary>
    /// Callback method from the implementation to keep track of the instantiation
    /// order. This method is not intended to be called explicitly by clients of the API!
    /// </summary>
    /// <param name="componentAdapter">the adapter</param>
    void AddOrderedComponentAdapter (IComponentAdapter componentAdapter);
 
    IList GetComponentAdaptersOfType(Type componentType);
  }
}