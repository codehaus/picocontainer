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
using PicoContainer;
using PicoContainer.Defaults;
using PicoContainer.Lifecycle;


namespace PicoContainer.Extras
{
  public class DecoratingComponentAdapter : ComponentAdapter
  {
    private ComponentAdapter theDelegate;

    public DecoratingComponentAdapter(ComponentAdapter theDelegate) {
      this.theDelegate = theDelegate;
    }

    virtual public object ComponentKey
    {
      get {
        return theDelegate.ComponentKey;
      }
    }

    virtual public Type ComponentImplementation
    {
      get {
        return theDelegate.ComponentImplementation;
      }
    }

    virtual public ComponentAdapter Delegate
    {
      get
      {
        return theDelegate;
      }
			
    }
		
 			
    public virtual object GetComponentInstance(MutablePicoContainer componentRegistry)
    {
      return theDelegate.GetComponentInstance(componentRegistry);
    }
  }
}