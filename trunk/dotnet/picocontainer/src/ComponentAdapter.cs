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
  /// A component adapter is responsible for instantiating and caching 
  /// a specific component instance. It is used internally by PicoContainer,
  /// and is not meant to be used directly by clients of the PicoContainer API.
  /// </summary>
  public interface ComponentAdapter {

    /// <summary>
    /// Returns the key of the adapter
    /// </summary>
    object ComponentKey {
      get;
    }

    /// <summary>
    /// Returns the implementing typ
    /// </summary>
    Type ComponentImplementation{
      get;
    }

    /// <summary>
    ///  Gets the component instance. 
    /// </summary>
    /// <param name="dependencyContainer">container where the adapter can look for dependent component instances</param>
    /// <returns>the component instance</returns>
    /// <exception cref="PicoInitializationException">if the component couldn't be instantiated</exception>
    object GetComponentInstance(MutablePicoContainer dependencyContainer);
  }
}
