/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 * Ported to .NET by Jeremey Stell-Smith                                     *
 *****************************************************************************/


using System;

using NUnit.Framework;

using PicoContainer;
using PicoContainer.Test.TestModel;

namespace PicoContainer.Test
{
	[TestFixture]
	public class NoPicoTestCase : Assertion 
	{
		/// <summary>
		/// A demonstration of using components WITHOUT Pico (or Nano)
		/// This was one of the design goals.
		/// 
		/// This is manual lacing of components.
		/// </summary>
		[Test]
		public void TestWilmaWithoutPicoTestCase() 
		{
			WilmaImpl wilma = new WilmaImpl();
			FredImpl fred = new FredImpl(wilma);

			Assert("Wilma should have had her hello method called",
				wilma.HelloCalled());
		}
	}
}
