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
using System.Collections;

namespace PicoContainer.Defaults
{
  /// <summary>
  /// Summary description for NoSatisfiableConstructorsException.
  /// </summary>
  [Serializable]
  public class NoSatisfiableConstructorsException : PicoIntrospectionException
  {
    private Type componentImplementation;
    private ArrayList failedDependencies;
    private string message;

    public NoSatisfiableConstructorsException(Type componentImplementation, ArrayList failedDependencies) 
    {

      this.componentImplementation = componentImplementation;
      this.failedDependencies = failedDependencies;
      // TODO improve
      message = componentImplementation.Name + " doesn't have any satisfiable constructors. Unsatisfiable dependencies: " + ((Type)failedDependencies[0]).Name;
    }

    public Type UnsatisfiableComponentImplementation
    {
      get {
        return componentImplementation;
      }
    }

    public ArrayList UnsatisfiableDependencies
    {
      get {
        return failedDependencies;
      }
    }

    public override string Message {
      get {
        return message;
      }
    }

  }
}
