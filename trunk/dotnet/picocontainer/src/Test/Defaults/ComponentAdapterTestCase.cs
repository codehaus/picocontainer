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
using PicoContainer.Tests.TestModel;

namespace PicoContainer.Tests.Defaults
{
  [TestFixture]
  public class ComponentAdapterTest
  {
    
    public void testEquals()  
    {
      ComponentAdapter componentAdapter =
        createComponentAdapter();

      Assert.Equals(componentAdapter, componentAdapter);
    }

    private ComponentAdapter createComponentAdapter() 
    {
      return new DefaultComponentAdapter(typeof(Touchable), typeof(SimpleTouchable));
    }
  }
}
