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
  public class ConstantParameter : IParameter {
    private readonly object constantValue;

    public ConstantParameter(object constantValue) {
      this.constantValue = constantValue;
    }

    public IComponentAdapter ResolveAdapter(IPicoContainer picoContainer, Type expectedType) {
      return new InstanceComponentAdapter(constantValue, constantValue);
    }
  }
}
