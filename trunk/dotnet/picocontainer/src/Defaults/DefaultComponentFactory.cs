/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 * C# port by Maarten Grootendorst                                           *
 *****************************************************************************/

using System;

namespace PicoContainer.Defaults
{
  public class DefaultComponentAdapter : TransientComponentAdapter 
  {

    private object componentInstance;

    public DefaultComponentAdapter(Object componentKey,
      Type componentImplementation,
      Parameter[] parameters) : base(componentKey, componentImplementation, parameters)
    {
    
    }

    public DefaultComponentAdapter(Object componentKey,
      Type componentImplementation) :  base(componentKey, componentImplementation, null){}


    public override object GetComponentInstance(MutablePicoContainer picoContainer)
    {

      if (componentInstance == null ) 
      {

        componentInstance = base.GetComponentInstance(picoContainer);
        picoContainer.AddOrderedComponentAdapter(this);

      }

      return componentInstance;
    }

  }
}
