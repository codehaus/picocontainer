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
  /// This is the core interface for registration of components.</summary>
  public interface MutablePicoContainer : PicoContainer {

    /// <summary>
    /// Registers a component.</summary>
    /// <param name="componentKey">a key that identifies the compoent. Must be unique within the container.</param>
    /// <param name="componentImplementation">the concrete component type</param>
    /// <returns>the associated ComponentAdapter.</returns>
    /// <exception cref="PicoRegistrationException">if the registration fails</exception>
    ComponentAdapter RegisterComponentImplementation(object componentKey, Type componentImplementation);

    /// <summary>
    /// Registers a component.</summary>
    /// <param name="componentKey">a key that identifies the compoent. Must be unique within the container.</param>
    /// <param name="componentImplementation">the concrete component type</param>
    /// <param name="parameters">an array of parameters that gives the container hints about what arguments
    /// to pass to the constructor when it is instantiated.</param>
    /// <returns>the associated ComponentAdapter.</returns>
    /// <exception cref="PicoRegistrationException">if the registration fails</exception>
    ComponentAdapter RegisterComponentImplementation(object componentKey, Type componentImplementation, Parameter[] parameters);

    /**
     * Registers a component using the componentImplementation as key.
     *
     * @param componentImplementation the concrete component class.
     * @throws PicoRegistrationException if registration fails.
     * @return the associated ComponentAdapter.
     */
    
    /// <summary>
    /// Registers a component using the componentImplementation type as key.</summary>
    /// <param name="componentImplementation">the concrete component type</param>
    /// <returns>the associated ComponentAdapter.</returns>
    /// <exception cref="PicoRegistrationException">if the registration fails</exception>
    ComponentAdapter RegisterComponentImplementation(Type componentImplementation);

    /**
     * Registers an arbitrary object, using itself as a key.
     *
     * @param componentInstance
     * @throws PicoRegistrationException if registration fails.
     * @return the associated ComponentAdapter.
     */

    /// <summary>
    /// Registers an arbitrary object, using itself as a key.
    /// </summary>
    /// <param name="componentInstance">the object to register</param>
    /// <returns>the associated ComponentAdapter.</returns>
    /// <exception cref="PicoRegistrationException">if the registration fails</exception>
    ComponentAdapter RegisterComponentInstance(object componentInstance);

    /// <summary>
    /// Registers an arbitrary object as a component in the container.</summary>
    /// <remarks>This is handy when other components in the same container have dependencies on this
    /// kind of object, but where letting the container manage and instantiate it is impossible.</remarks>
    /// <param name="componentKey">a key that identifies the compoent. Must be unique within the container.</param>
    /// <param name="componentInstance">an arbitrary object.</param>
    /// <returns>the associated ComponentAdapter.</returns>
    /// <exception cref="PicoRegistrationException">if the registration fails</exception>
    ComponentAdapter RegisterComponentInstance(object componentKey, object componentInstance);
    /**
     * Registers a ComponentAdapter.
     *
     * @param componentAdapter the adapter
     * @throws PicoRegistrationException if registration fails.
     */

    /// <summary>
    /// Registers a component adapter</summary>
    /// <param name="componentAdapter">the adapter to register</param>
    /// <returns>the associated ComponentAdapter.</returns>
    /// <exception cref="PicoRegistrationException">if the registration fails</exception>    
    void RegisterComponent(ComponentAdapter componentAdapter);

    /// <summary>
    /// Unregisters a component.</summary>
    /// <param name="componentKey">key of the component to unregister.</param>
    /// <returns>the associated ComponentAdapter.</returns>
    ComponentAdapter UnRegisterComponent(object componentKey);

    /// <summary>
    /// Adds a component instance to the container. Do not call this method explicitly. 
    /// </summary>
    /// <remarks>It is used by the internals. Use <see cref="RegisterComponentInstance"/>
    /// instead if you wish to register externally instantiated objects.</remarks>
    /// <param name="componentAdapter"></param>
    void AddOrderedComponentAdapter(ComponentAdapter componentAdapter);

    /**
     * Adds a Child container. Will also add this instance as a parent to child,
     * so calling {@link #addParent} is not necessary.
     *
     * @param child child container.
     * @return true if the child was actually added.
     */
    
    /// <summary>
    /// Adds a Child container.</summary>
    /// <remarks>Will also add this instance as a parent to child,
    /// so calling <see cref="AddParent"/> is not necessary.</remarks>
    /// <param name="child">the child container.</param>
    /// <returns>true if the child was actually added</returns>
    bool AddChild(MutablePicoContainer child);

    /// <summary>
    /// Adds a Parent container.</summary>
    /// <remarks>Will also add this instance as a child to parent,
    /// so calling <see cref="AddChild"/> is not necessary.</remarks>
    /// <param name="parent">the parent container.</param>
    /// <returns>true if the parent was actually added.</returns>
    bool AddParent(MutablePicoContainer parent);

    /// <summary>
    /// Removes a child container.
    /// </summary>
    /// <remarks>Will also remove this instance as a parent to child,
    ///  so calling RemoveParent is not necessary.</remarks>
    /// <param name="child"></param>
    /// <returns>true if the child was actually removed.</returns>
    bool RemoveChild(MutablePicoContainer child);

    /// <summary>
    /// Removes a parent container.
    /// </summary>
    /// <remarks>Will also remove this instance as a child to parent,
    /// so calling RemoveChild is not necessary.</remarks>
    /// <param name="parent">the parent container</param>
    /// <returns>true if the parent was actually removed.</returns>
    bool RemoveParent(MutablePicoContainer parent);

  }
}
