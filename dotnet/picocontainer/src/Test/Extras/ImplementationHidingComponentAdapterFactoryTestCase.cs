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
using System.Threading;
using System.Collections;
using csUnit;

using PicoContainer.Extras;
using PicoContainer.Lifecycle;
using PicoContainer.Defaults;
using PicoContainer.Tests.TestModel;
using PicoContainer.Tests.Tck;

namespace PicoContainer.Tests.Extras {
  /// <summary>
  /// Summary description for ImplementationHidingComponentAdapterFactoryTestCase.
  /// </summary>
  public class ReferencesSwappables {
    public Swappable swappable;

    public ReferencesSwappables(Swappable swappable) {
      this.swappable = swappable;
    }
  }

  public interface Swappable {
    String getCheese();
  }

  public class ConcreteSwappable : Swappable {
    public String getCheese() {
      return "Edam";
    }
  }

  public class ImplementationHidingComponentAdapterFactoryTestCase : AbstractComponentAdapterFactoryTestCase {



    public void testCreatedComponentAdapterCreatesInstancesWhereImplementationIsHidden() {
      ComponentAdapter componentAdapter = CreateComponentAdapterFactory().CreateComponentAdapter(typeof(Swappable), typeof(ConcreteSwappable), null);
      Swappable swappable = (Swappable) componentAdapter.GetComponentInstance(picoContainer);
      Assert.False(swappable is ConcreteSwappable);
      Assert.Equals("Edam", swappable.getCheese());
    }

    public void testHotSwap() {
      ImplementationHidingComponentAdapterFactory componentAdapterFactory = (ImplementationHidingComponentAdapterFactory) CreateComponentAdapterFactory();
      ComponentAdapter componentAdapter = componentAdapterFactory.CreateComponentAdapter(typeof(Swappable), typeof(ConcreteSwappable), null);
      Swappable swappable = (Swappable) componentAdapter.GetComponentInstance(picoContainer);
      Assert.False(swappable is ConcreteSwappable);
      Assert.Equals("Edam", swappable.getCheese());

      //TODO - what is this last line for, throws something on errror or should have an Assert. following ?
      componentAdapterFactory.HotSwap(typeof(Swappable));
    }


    protected override ComponentAdapterFactory CreateComponentAdapterFactory() {
      return new ImplementationHidingComponentAdapterFactory(new DefaultComponentAdapterFactory());
    }

    public static void Main() {

      Type c = typeof(ConcreteSwappable);
      
      ImplementationHidingComponentAdapterFactoryTestCase d = new ImplementationHidingComponentAdapterFactoryTestCase ();
      d.SetUp();
      d.testHotSwap();
    }

  }
}
