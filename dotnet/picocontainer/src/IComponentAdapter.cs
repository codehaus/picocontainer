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
  /// A component adapter is responsible for instantiating a specific component instance. 
  /// </summary>
  public interface IComponentAdapter {
    
    /// <summary>
    /// Returns the component's key
    /// </summary>
    object ComponentKey {
      get;
    }

    /// <summary>
    /// Returns the component's implementing type
    /// </summary>
    Type ComponentImplementation{
      get;
    }

    /// <summary>
    /// Gets the component instance. This method will usually create
    /// a new instance for each call.
    /// </summary>
    /// <remarks>
    /// Not all ComponentAdapters return a new instance for each call an example is the <see cref="PicoContainer.Defaults.CachingComponentAdapter"/>.<BR/>
    /// </remarks>
    /// <returns>a component instance</returns>
    /// <exception cref="PicoContainer.PicoInitializationException">if the component could not be instantiated.</exception>    
    object ComponentInstance {
      get;
    }
    

    
    /// <summary>
    /// Verify that all dependencies for this adapter can be satisifed.
    /// </summary>
    /// <exception cref="PicoContainer.PicoIntrospectionException">if the verification failed</exception>
    void Verify();


    /// 
    /// <summary>
    ///  Property containing the container in which this instance is registered, called by the container upon registration
    /// </summary>
    IPicoContainer Container {
      get;
      set;
    }
  }
}
