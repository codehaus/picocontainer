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

namespace PicoContainer.Defaults {

  public abstract class AbstractComponentAdapter : IComponentAdapter {
    private readonly object componentKey;
    private readonly Type componentImplementation;
    private IPicoContainer container;

    protected AbstractComponentAdapter(object componentKey, Type componentImplementation) {
      if(componentImplementation == null) {
        throw new NullReferenceException("componentImplementation");
      }
      this.componentKey = componentKey;
      this.componentImplementation = componentImplementation;
      CheckTypeCompatibility();
      CheckConcrete();
    }

    public object ComponentKey {
      get {
        if(componentKey == null) {
          throw new NullReferenceException("componentKey");
        }
        return componentKey;
      }
    }

    public Type ComponentImplementation {
      get {
        return componentImplementation;
      }
    }

    private void CheckTypeCompatibility() {
      Type componentType = componentKey as Type;
      if (componentType != null) {
        if (!componentType.IsAssignableFrom(componentImplementation)) {
          throw new AssignabilityRegistrationException(componentType, componentImplementation);
        }
      }
    }

    private void CheckConcrete() {
      if (componentImplementation.IsInterface || componentImplementation.IsAbstract) {
        throw new NotConcreteRegistrationException(componentImplementation);
      }
    }

    public override string ToString() {
      return this.GetType().Name+"["+ComponentKey+"]";
    }
    
    public IPicoContainer Container {
      get {
        return container;
      }

      set {
        this.container = value;
      }
    }

    public abstract object ComponentInstance{
      get;
    }

    public abstract void Verify();
  }
}
