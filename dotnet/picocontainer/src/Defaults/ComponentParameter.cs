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

namespace PicoContainer.Defaults {
  public class ComponentParameter : IParameter {
    private object componentKey;

    /**
     * Expect a parameter matching a component of a specific key.
     * @param componentKey the key of the desired component
     */
    public ComponentParameter(object componentKey) {
      this.componentKey = componentKey;
    }

    public ComponentParameter() {   }

    public IComponentAdapter ResolveAdapter(IPicoContainer picoContainer, Type expectedType) {

      IComponentAdapter result;

      if (componentKey != null){
        result = picoContainer.GetComponentAdapter(componentKey);
        if (result != null && !expectedType.IsAssignableFrom(result.ComponentImplementation)) {
          result = null;
        }
      } else {
        result =  picoContainer.GetComponentAdapterOfType(expectedType);
      }

      return result;
    }
  }

  /// <summary>
  /// A ComponentParameter should be used to pass in a particular component
  /// as argument to a different component's constructor. This is particularly
  /// useful in cases where several components of the same type have been registered,
  /// but with a different key. Passing a ComponentParameter as a parameter
  /// when registering a component will give PicoContainer a hint about what
  /// other component to use in the constructor.
  /// </summary

}
