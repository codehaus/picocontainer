using System;
using PicoContainer;
using PicoContainer.Defaults;
using PicoContainer.Tests.Tck;
using PicoContainer.Tests.TestModel;
using NUnit.Framework;

namespace Test.Defaults
{
	/// <summary>
	/// Summary description for CachingComponentAdapterFactoryTestCase.
	/// </summary>
	[TestFixture]
	public class CachingComponentAdapterFactoryTestCase : AbstractComponentAdapterFactoryTestCase
	{
		[SetUp]
		protected void setUp()
		{
			picoContainer = new DefaultPicoContainer(CreateComponentAdapterFactory());
		}

		protected override IComponentAdapterFactory CreateComponentAdapterFactory()
		{
			return new CachingComponentAdapterFactory(new ConstructorInjectionComponentAdapterFactory());
		}

		[Test]
		public void testContainerReturnsSameInstaceEachCall()
		{
			picoContainer.RegisterComponentImplementation(typeof (Touchable), typeof (SimpleTouchable));
			Touchable t1 = (Touchable) picoContainer.GetComponentInstance(typeof (Touchable));
			Touchable t2 = (Touchable) picoContainer.GetComponentInstance(typeof (Touchable));
			Assert.AreSame(t1, t2);
		}


	}
}