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
using PicoContainer.Tests.TestModel;

namespace PicoContainer.Tests
{
	
	[TestFixture]
	public class PicoPicoTestCase
	{		
		[Test]
		public void  DefaultPicoContainerCanHostItself()
		{			
			MutablePicoContainer pico = new DefaultPicoContainer();
			pico.RegisterComponentImplementation(typeof(DefaultPicoContainer));
			
			MutablePicoContainer hostedPico = (MutablePicoContainer) pico.GetComponentInstance(typeof(DefaultPicoContainer));
			hostedPico.RegisterComponentImplementation(typeof(DependsOnTouchable));
			hostedPico.RegisterComponentImplementation(typeof(SimpleTouchable));
			
			Assert.IsTrue(hostedPico.HasComponent(typeof(DependsOnTouchable)));
			Assert.IsTrue(hostedPico.HasComponent(typeof(SimpleTouchable)));
		}
	}
}