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

namespace PicoContainer.Tests.Tck
{
  /// <summary>
  /// Summary description for AbstractLazyInstantiationTestCase.
  /// </summary>
  [TestFixture]
  public abstract class AbstractLazyInstantiationTestCase
  {
    protected abstract IMutablePicoContainer createPicoContainer();

    public class Kilroy 
    {
      public Kilroy(Havana havana) 
      {
        havana.graffiti("Kilroy was here");
      }
    }

    public class Havana 
    {
      public String paint = "Clean wall";

      public void graffiti(String paint) 
      {
        this.paint = paint;
      }
    }

    [Test]
    public void testLazyInstantiation() 
    {
      IMutablePicoContainer pico = createPicoContainer();

      pico.RegisterComponentImplementation(typeof(Kilroy));
      pico.RegisterComponentImplementation(typeof(Havana));

      Assert.AreSame(pico.GetComponentInstance(typeof(Havana)),pico.GetComponentInstance(typeof(Havana)));
      Assert.IsNotNull(pico.GetComponentInstance(typeof(Havana)));
      Assert.AreEqual("Clean wall", ((Havana) pico.GetComponentInstance(typeof(Havana))).paint);
      Assert.IsNotNull(pico.GetComponentInstance(typeof(Kilroy)));
      Assert.AreEqual("Kilroy was here", ((Havana) pico.GetComponentInstance(typeof(Havana))).paint);
    }
  }
}