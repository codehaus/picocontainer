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

namespace PicoContainer.Defaults {

  public abstract class AbstractComponentAdapter : ComponentAdapter {
    private object componentKey;
    private Type componentImplementation;

    protected AbstractComponentAdapter(object componentKey, Type componentImplementation) {
      if(componentImplementation == null) {
        throw new NullReferenceException("componentImplementation");
      }
      CheckTypeCompatibility(componentKey, componentImplementation);
      CheckConcrete(componentImplementation);
      this.componentKey = componentKey;
      this.componentImplementation = componentImplementation;
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

    private void CheckTypeCompatibility(object componentKey, Type componentImplementation) {
      if (componentKey is Type) {
        Type componentType = (Type) componentKey;

        if (!componentType.IsAssignableFrom(componentImplementation)) {
          throw new AssignabilityRegistrationException(componentType, componentImplementation);
        }
      }
    }

    private void CheckConcrete(Type componentImplementation) {

      if (componentImplementation.IsInterface || componentImplementation.IsAbstract) {
        throw new NotConcreteRegistrationException(componentImplementation);
      }
    }

    public abstract object GetComponentInstance(MutablePicoContainer dependencyContainer);
  }

}
