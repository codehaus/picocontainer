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
using PicoContainer.Defaults;
using PicoContainer.Tests.TestModel;
using NUnit.Framework;

namespace PicoContainer.Tests.Tck {
  /// <summary>
  /// Summary description for AbstractComponentAdapterFactoryTestCase.
  /// </summary>
  [TestFixture]
  public abstract class AbstractComponentAdapterFactoryTestCase {
    protected DefaultPicoContainer picoContainer;

    protected abstract IComponentAdapterFactory CreateComponentAdapterFactory();

    [SetUp]
    public void SetUp() {
      picoContainer = new DefaultPicoContainer();
    }

    [Test]
    public void testEquals() {
      IComponentAdapter componentAdapter = CreateComponentAdapterFactory().CreateComponentAdapter(typeof(Touchable),typeof(SimpleTouchable), null);
      Assert.AreEqual(componentAdapter,componentAdapter);
    }

    [Test]
    public void testRegisterComponent() {
      IComponentAdapter componentAdapter = CreateComponentAdapterFactory().CreateComponentAdapter(typeof(Touchable), typeof(SimpleTouchable), null);

      picoContainer.RegisterComponent(componentAdapter);

      Assert.IsTrue(picoContainer.ComponentAdapters.Contains(componentAdapter));
    }

    [Test]
    public void testUnRegisterComponent() {

      IComponentAdapter componentAdapter =
        CreateComponentAdapterFactory().CreateComponentAdapter(typeof(Touchable), typeof(SimpleTouchable), null);

      picoContainer.RegisterComponent(componentAdapter);
      Assert.IsNotNull(picoContainer.UnregisterComponent(typeof(Touchable)));

      Assert.IsFalse(picoContainer.ComponentAdapters.Contains(componentAdapter));
    }
  }


}
