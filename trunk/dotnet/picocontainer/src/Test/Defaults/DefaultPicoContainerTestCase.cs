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
using System.Diagnostics;

using NUnit.Framework;

using PicoContainer.Defaults;
using PicoContainer.Tests.Tck;
using PicoContainer.Tests.TestModel;


namespace PicoContainer.Tests.Defaults
{

	/// Summary description for DefaultPicoContainerTestCase.
	/// </summary>
  [TestFixture]
  public class DefaultPicoContainerTestCase : AbstractPicoContainerTestCase
	{
    protected override MutablePicoContainer createPicoContainer() {
      return new DefaultPicoContainer();
    }

    public override void testBasicInstantiationAndContainment()  {
    DefaultPicoContainer pico = (DefaultPicoContainer) createPicoContainerWithTouchableAndDependency();

    Assert.IsTrue(pico.FindComponentInstance(typeof(Touchable)) is Touchable,"Component should be instance of Touchable");
                                         }

  }
}
