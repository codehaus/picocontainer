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
	/// <summary>
	/// Can Pico host itself ?
	/// </summary>
	[TestFixture]
	public class PicoPicoTestCase : Assertion 
	{
		[Test]
		public void TestDefaultPicoContainer() 
		{
			IPicoContainer pc = new HierarchicalPicoContainer.Default();
			pc.RegisterComponent(typeof(HierarchicalPicoContainer.Default));
			pc.Start();

			tryDefaultPicoContainer((IPicoContainer) pc.GetComponent(typeof(HierarchicalPicoContainer.Default)));
		}

		private void tryDefaultPicoContainer(IPicoContainer pc2) 
		{
			pc2.RegisterComponent(typeof(FredImpl));
			pc2.RegisterComponent(typeof(WilmaImpl));

			pc2.Start();

			Assert( "There should have been a Fred in the container", pc2.HasComponent( typeof(FredImpl) ) );
			Assert( "There should have been a Wilma in the container", pc2.HasComponent( typeof(WilmaImpl) ) );
		}
	}
}
