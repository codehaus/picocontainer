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
  public class DefaultComponentRegistryTestCase
  {
    private DefaultPicoContainer componentRegistry;

    [SetUp]
    public void SetUp() 
    {
      componentRegistry = new DefaultPicoContainer();
    }

    [Test]
    public void testRegisterComponent() 
    {
      ComponentAdapter componentSpecification = createComponentAdapter();

      componentRegistry.RegisterComponent(componentSpecification);

      Assert.True(componentRegistry.HasComponentAdapter(componentSpecification));
    }

    [Test]
    public void testUnRegisterComponent() 
    {
      ComponentAdapter componentSpecification = createComponentAdapter();

      componentRegistry.RegisterComponent(componentSpecification);

      componentRegistry.UnRegisterComponent(typeof(Touchable));

      Assert.False(componentRegistry.HasComponentAdapter(componentSpecification));
    }

    [Test]
    private ComponentAdapter createComponentAdapter() 
    {
      return new DefaultComponentAdapter(typeof(Touchable), typeof(SimpleTouchable));
    }
  }
}