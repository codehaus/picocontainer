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
using System.Reflection;

using NUnit.Framework;

using PicoContainer;
using PicoContainer.Test.TestModel;

namespace PicoContainer.Test
{
	[TestFixture]
	public class ComponentInteroperationTestCase : Assertion
	{
		[Test]
		public void TestBasic()
		{
			WilmaImpl wilma = new WilmaImpl();
			IPicoContainer pico = new OveriddenPicoTestContainerWithLifecycleManager(wilma, new NullStartableLifecycleManager());

			pico.RegisterComponent(typeof(FredImpl));
			pico.RegisterComponent(typeof(WilmaImpl));

			pico.Start();

			Assert("hello should have been called in wilma", wilma.HelloCalled());
		}
	}
}
