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
using PicoContainer;
using PicoContainer.Defaults;
using PicoContainer.Lifecycle;


namespace PicoContainer.Extras {
  public class ImplementationHidingComponentAdapterFactory : DecoratingComponentAdapterFactory {
    private static InterfaceFinder interfaceFinder = new InterfaceFinder();

    public ImplementationHidingComponentAdapterFactory() : base (new DefaultComponentAdapterFactory()) {
    }

    public ImplementationHidingComponentAdapterFactory(ComponentAdapterFactory theDelegate) : base (theDelegate) {
    }

    public override ComponentAdapter CreateComponentAdapter(object componentKey,
      Type componentImplementation,
      Parameter[] parameters) {
      return new Adapter(base.CreateComponentAdapter(componentKey, componentImplementation, parameters));
    }

    public void HotSwap(Type type) {
    }

    public class Adapter : DecoratingComponentAdapter {
      public Adapter(ComponentAdapter theDelegate) : base (theDelegate) {
      }

      public override Object GetComponentInstance(MutablePicoContainer picoContainer) {
        Object component = base.GetComponentInstance(picoContainer);
        Type[] interfaces = interfaceFinder.GetInterfaces(component);
        if(interfaces.Length == 0) {
          throw new Exception("Can't hide implementation");
          //          throw new PicoIntrospectionException("Can't hide implementation for " + component.getClass().getName() + ". It doesn't implement any interfaces.");
        }
        Type t = DefaultComponentMulticasterFactory.GetAggregatingInterface(interfaces);
        ArrayList list = new ArrayList();
        list.Add(component);
        return new AggregatingProxy(t ,list).GetTransparentProxy();
      }
    }
  }
}
