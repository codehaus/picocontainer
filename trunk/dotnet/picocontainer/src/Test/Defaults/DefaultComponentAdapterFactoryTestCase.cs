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
using System.Diagnostics;

using csUnit;

using PicoContainer.Defaults;
using PicoContainer.Tests.Tck;
using PicoContainer.Tests.TestModel;

namespace PicoContainer.Tests.Defaults
{
  [TestFixture]
  public class DefaultComponentAdapterFactoryTestCase : AbstractComponentAdapterFactoryTestCase
  {

    [SetUp]
    protected new void SetUp() 
    {
      picoContainer = new DefaultPicoContainer();
    }

    protected override ComponentAdapterFactory CreateComponentAdapterFactory() 
    {
      return new DefaultComponentAdapterFactory();
    }

    [Test]
    public void testInstantiateComponentWithNoDependencies() 
    {
       ComponentAdapter componentAdapter =
        CreateComponentAdapterFactory().CreateComponentAdapter(typeof(Touchable), typeof(SimpleTouchable), null);

      Object comp = componentAdapter.GetComponentInstance(picoContainer);
      Assert.NotNull(comp);
      Assert.True(comp is SimpleTouchable);
    }

    [Test]
    public void testSingleUseComponentCanBeInstantiatedByDefaultComponentAdapter() 
    {
      ComponentAdapter componentAdapter = new DefaultComponentAdapter(null, typeof(object));
      Object component = componentAdapter.GetComponentInstance(new DefaultPicoContainer());
      Assert.NotNull(component);
    }

  }


}
