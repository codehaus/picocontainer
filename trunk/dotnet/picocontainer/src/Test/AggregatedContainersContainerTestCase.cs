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
	public class AggregatedContainersContainerTestCase : Assertion 
	{
		private IPicoContainer pico;
		private AggregatedContainersContainer.Filter filter;

		[SetUp]
		public void setUp() 
		{
			pico = new HierarchicalPicoContainer.Default();
			pico.RegisterComponent(typeof(WilmaImpl));
			filter = new AggregatedContainersContainer.Filter(pico);
		}

		[TearDown]
		public void tearDown() 
		{
			try 
			{
				pico.Stop();
			} 
			catch (InvalidOperationException) 
			{
			}
			pico = null;
			filter = null;
		}

		[Test]
		public void TestGetComponents() 
		{
			AssertEquals("Content of Component arrays should be the same", pico, filter.Subject);
		}

		[Test]
		public void TestGetComponentTypes() 
		{
			AssertEquals("Content of Component type arrays should be the same", pico, filter.Subject);
		}

		[Test]
		public void TestGetComponent() 
		{
			AssertSame("Wilma should be the same", pico.GetComponent(typeof(WilmaImpl)), filter.GetComponent(typeof(WilmaImpl)));
		}

		[Test]
		public void TestHasComponent() 
		{
			AssertEquals("Containers should contain the same", pico.HasComponent(typeof(WilmaImpl)), filter.HasComponent(typeof(WilmaImpl)));
		}

		[Test, ExpectedException(typeof(PicoNullReferenceException))]
		public void TestNullContainer() 
		{
			AggregatedContainersContainer.Filter badOne = new AggregatedContainersContainer.Filter(null);
		}

		[Test, ExpectedException(typeof(PicoNullReferenceException))]
		public void TestNullArrayContainer() 
		{
			AggregatedContainersContainer badOne = new AggregatedContainersContainer(null);
		}

		[Test]
		public void TestGetToFilterFor() 
		{
			AssertSame("The Container to filter for should be the one made in setUp", pico, filter.Subject);
		}

		class ContainerWhichIDontUnderstand : IContainer
		{
			private Type type;
			private object component;
			public ContainerWhichIDontUnderstand(Type type, object component) 
			{
				this.type = type;
				this.component = component;
			}

			public bool HasComponent(Type compType) 
			{
				return compType == type;
			}

			public object GetComponent(Type compType) 
			{
				return compType == type ? component : null;
			}

			public object[] Components
			{
				get { return new object[] {component}; }
			}

			public Type[] ComponentTypes
			{
				get { return new Type[] {type}; }
			}
		}

		[Test]
		public void TestBasic() 
		{
			string acomp = "hello";
			int bcomp = 123;

			IContainer a = new ContainerWhichIDontUnderstand(typeof(string), acomp);

			IContainer b = new ContainerWhichIDontUnderstand(typeof(int), bcomp);

			AggregatedContainersContainer acc = new AggregatedContainersContainer(new IContainer[] {a, b});

			Assert(acc.HasComponent(typeof(string)));
			Assert(acc.HasComponent(typeof(int)));
			AssertEquals(acomp, acc.GetComponent(typeof(string)));
			AssertEquals(bcomp, acc.GetComponent(typeof(int)));
			AssertEquals(2, acc.Components.Length);

		}

		[Test]
		public void TestEmpty() 
		{

			AggregatedContainersContainer acc = new AggregatedContainersContainer(new IContainer[0]);
			Assert(acc.HasComponent(typeof(string)) == false);
			Assert(acc.GetComponent(typeof(string)) == null);
			Assert(acc.Components.Length == 0);

		}
	}
}