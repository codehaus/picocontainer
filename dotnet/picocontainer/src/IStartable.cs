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
  /// Basic lifecycle interface for Pico components. 
  /// support.
  /// </summary>
  /// <remarks>For more advanced and pluggable lifecycle</remarks>
  public interface IStartable {
    /// <summary>
    /// Starts a component, called by the container
    /// </summary>
    void Start();
    /// <summary>
    /// Stops a component, called by the container
    /// </summary>
    void Stop();
  }
}
