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

namespace PicoContainer {
  /// <summary>
  /// This is the core interface for registration of components.
  /// </summary>
  public interface IMutablePicoContainer : IPicoContainer {

    /// <summary>
    /// Registers a component.</summary>
    /// <param name="componentKey">a key that identifies the compoent. Must be unique within the container.<remarks>The type of the key object has no semantic significance unless explicitly specified
    /// in the implementing container.</remarks></param>
    /// <param name="componentImplementation">the concrete component type</param>
    /// <returns>the associated ComponentAdapter.</returns>
    /// <exception cref="PicoContainer.PicoRegistrationException">if the registration fails</exception>
    IComponentAdapter RegisterComponentImplementation(object componentKey, Type componentImplementation);

    /// <summary>
    /// Registers a component.</summary>
    /// <param name="componentKey">a key that identifies the compoent. Must be unique within the container.
    /// <remarks>The type of the key object has no semantic significance unless explicitly specified
    /// in the implementing container.</remarks></param>
    /// <param name="componentImplementation">the concrete component type</param>
    /// <param name="parameters">an array of parameters that gives the container hints about what arguments
    /// to pass to the constructor when it is instantiated.</param>
    /// <returns>the associated ComponentAdapter.</returns>
    /// <exception cref="PicoContainer.PicoRegistrationException">if the registration fails</exception>
    IComponentAdapter RegisterComponentImplementation(object componentKey, Type componentImplementation, IParameter[] parameters);

    /// <summary>
    /// Registers a component using the componentImplementation type as key.</summary>
    /// <param name="componentImplementation">the concrete component type</param>
    /// <returns>the associated ComponentAdapter.</returns>
    /// <exception cref="PicoContainer.PicoRegistrationException">if the registration fails</exception>
    IComponentAdapter RegisterComponentImplementation(Type componentImplementation);

    /// <summary>
    /// Registers an arbitrary object, using it's class as a key.
    /// </summary>
    /// <param name="componentInstance">the object to register</param>
    /// <returns>the associated ComponentAdapter.</returns>
    /// <exception cref="PicoContainer.PicoRegistrationException">if the registration fails</exception>
    IComponentAdapter RegisterComponentInstance(object componentInstance);

    /// <summary>
    /// Registers an arbitrary object as a component in the container.
    /// <remarks>This is handy when other components in the same container have dependencies on this
    /// kind of object, but where letting the container manage and instantiate it is impossible.<br/>
    /// Beware that too much use of this method is an antipattern.
    /// <a href="http://docs.codehaus.org/display/PICO/Instance+Registration">antipattern</a>.
    /// </remarks>
    /// </summary>
    /// <param name="componentKey">a key that identifies the compoent. Must be unique within the container    
    /// <remarks>The type of the key object has no semantic significance unless explicitly specified
    /// in the implementing container.</remarks></param>
    /// <param name="componentInstance">an arbitrary object.</param>
    /// <returns>the associated ComponentAdapter.</returns>
    /// <exception cref="PicoContainer.PicoRegistrationException">if the registration fails</exception>
    IComponentAdapter RegisterComponentInstance(object componentKey, object componentInstance);


    /**
     * Registers a component via a ComponentAdapter. Use this if you need fine grained control over what ComponentAdapter
     * to use for a specific component.
     *
     * @param componentAdapter the adapter
     * @throws PicoRegistrationException if registration fails.
     * @return the same adapter that was passed as an argument.
     */

    /// <summary>
    /// Registers a component via an <see cref="PicoContainer.IComponentAdapter"/>. Use this if you need fine grained control over what ComponentAdapter
    /// to use for a specific component.</summary>
    /// <param name="componentAdapter">the adapter to register</param>
    /// <returns>the associated ComponentAdapter.</returns>
    /// <exception cref="PicoContainer.PicoRegistrationException">if the registration fails</exception>    
    void RegisterComponent(IComponentAdapter componentAdapter);

    /// <summary>
    /// Unregisters a component.</summary>
    /// <param name="componentKey">key of the component to unregister.</param>
    /// <returns>the associated ComponentAdapter.</returns>
    IComponentAdapter UnregisterComponent(object componentKey);

    /// <summary>
    /// Unregisters a component using the instance of the component.</summary>
    /// <param name="componentInstance">instance of the component to unregister.</param>
    /// <returns>the associated ComponentAdapter.</returns>
    IComponentAdapter UnregisterComponentByInstance(object componentInstance);
  }
}
