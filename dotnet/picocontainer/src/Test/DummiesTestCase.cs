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

namespace PicoContainer.Test
{
	[TestFixture]
	public class DummiesTestCase : Assertion 
	{
		[Test]
		public void TestDummyContainer() 
		{
			NullContainer dc = new NullContainer();
			Assert(!dc.HasComponent(typeof(string)));
			AssertNull(dc.GetComponent(typeof(string)));
			AssertEquals(0, dc.Components.Length);
		}

		[Test]
		public void TestDummyStartableLifecycleManager() 
		{
			NullStartableLifecycleManager ds = new NullStartableLifecycleManager();
			object o = new object();
			ds.StartComponent(o);
			ds.StopComponent(o);
			//TODO check no methods were called, via proxy ?
		}

		[Test]
		public void TestDefaultComponentFactory() 
		{
			DefaultComponentFactory dcd = new DefaultComponentFactory();
			object decorated = dcd.CreateComponent(typeof(object), typeof(object).GetConstructor(new Type[0]), null);
			AssertNotNull(decorated);
			//TODO check no methods were called, via proxy ?

		}
	}
}