/***********************************************************************  ******
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
  /// This class represents an argument to a constructor. </summary>
  /// <remarks>It can be used to
  /// have finer control over what arguments are passed to a particular constructor.</remarks>
  public interface IParameter {
    /// <summary>
    /// Method the value of the parameter to an object of the required type.
    /// <remarks>This method exist only to keep in sync with the java version of the PicoContainer. Primitive types in
    /// the .Net framework are objects, so no translation is required.</remarks>
    /// </summary>
    /// <param name="componentRegistry">picoContainer the container where dependencies are resolved from</param>
    /// <param name="expectedType">the expected (dependant) type</param>
    /// <exception cref="PicoContainer.PicoIntrospectionException"></exception>
    /// <returns></returns>
    IComponentAdapter ResolveAdapter(IPicoContainer componentRegistry, Type expectedType);
  }
}
