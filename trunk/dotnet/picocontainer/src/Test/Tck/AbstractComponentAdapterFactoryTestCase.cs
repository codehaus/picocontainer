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
using PicoContainer.Defaults;
using PicoContainer.Tests.TestModel;
using csUnit;

namespace PicoContainer.Tests.Tck
{
  /// <summary>
  /// Summary description for AbstractComponentAdapterFactoryTestCase.
  /// </summary>
  [TestFixture]
  public abstract class AbstractComponentAdapterFactoryTestCase
  {
    protected DefaultPicoContainer picoContainer;

    protected abstract ComponentAdapterFactory CreateComponentAdapterFactory();

    [SetUp]
    public void SetUp() 
    {
      picoContainer = new DefaultPicoContainer();
    }

    [Test]
    public void testRegisterComponent() 
    {

      ComponentAdapter componentAdapter = CreateComponentAdapterFactory().CreateComponentAdapter(typeof(Touchable), typeof(SimpleTouchable), null);

      picoContainer.RegisterComponent(componentAdapter);

      Assert.True(picoContainer.HasComponentAdapter(componentAdapter));
    }

    [Test]
    public void testUnRegisterComponent() 
    {

      ComponentAdapter componentAdapter =
        CreateComponentAdapterFactory().CreateComponentAdapter(typeof(Touchable), typeof(SimpleTouchable), null);

      picoContainer.RegisterComponent(componentAdapter);
      picoContainer.UnRegisterComponent(typeof(Touchable));

      Assert.False(picoContainer.HasComponentAdapter(componentAdapter));
    }
  }


}
