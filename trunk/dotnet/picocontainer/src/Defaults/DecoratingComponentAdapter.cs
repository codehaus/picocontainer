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
using PicoContainer;
using PicoContainer.Defaults;


namespace PicoContainer.Defaults{
  public class DecoratingComponentAdapter : IComponentAdapter {
    private IComponentAdapter theDelegate;

    public DecoratingComponentAdapter(IComponentAdapter theDelegate) {
      this.theDelegate = theDelegate;
    }

    virtual public object ComponentKey {
      get {
        return theDelegate.ComponentKey;
      }
    }

    virtual public Type ComponentImplementation {
      get {
        return theDelegate.ComponentImplementation;
      }
    }

    public virtual object ComponentInstance{
      get {
        return theDelegate.ComponentInstance;
      }
    }

    public virtual void Verify() {
      theDelegate.Verify();
    }

    public virtual IComponentAdapter Delegate {
      get {
        return theDelegate;
      }			
    }

    public virtual IPicoContainer Container {
      get {
        return theDelegate.Container;
      }
      set {
        theDelegate.Container = value;
      }
    }
  }
}