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
	
  [TestFixture]
	public class TransientComponentAdapterTestCase
	{
		public virtual void  testNonCachingComponentAdapterReturnsNewInstanceOnEachCallToGetComponentInstance()
		{
			TransientComponentAdapter componentAdapter = new TransientComponentAdapter(null, typeof(object));
			object o1 = componentAdapter.GetComponentInstance(null);
			object o2 = componentAdapter.GetComponentInstance(null);
			Assert.IsTrue(o1 != o2);
		}
		
		public class Service
		{
		}
		
		public class TransientComponent
		{
			public Service service;
			
			public TransientComponent(Service service)
			{
				this.service = service;
			}
		}
		
		public virtual void  testDefaultPicoContainerReturnsNewInstanceForEachCallWhenUsingTransientComponentAdapter()
		{
			DefaultPicoContainer picoContainer = new DefaultPicoContainer();
			picoContainer.RegisterComponentImplementation(typeof(Service));
			picoContainer.RegisterComponent(new TransientComponentAdapter(typeof(TransientComponent), typeof(TransientComponent)));
			TransientComponent c1 = (TransientComponent) picoContainer.GetComponentInstance(typeof(TransientComponent));
			TransientComponent c2 = (TransientComponent) picoContainer.GetComponentInstance(typeof(TransientComponent));
			Assert.IsTrue(c1 != c2);
			Assert.AreEqual(c1.service, c2.service);
		}
	}
}