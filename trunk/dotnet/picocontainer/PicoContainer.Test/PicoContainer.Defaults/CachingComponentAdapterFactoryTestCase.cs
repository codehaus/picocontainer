using PicoContainer.Defaults;
using PicoContainer.Tests.Tck;
using PicoContainer.Tests.TestModel;
using NUnit.Framework;

namespace Test.Defaults
{
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
			picoContainer.RegisterComponentImplementation(typeof (ITouchable), typeof (SimpleTouchable));
			ITouchable t1 = (ITouchable) picoContainer.GetComponentInstance(typeof (ITouchable));
			ITouchable t2 = (ITouchable) picoContainer.GetComponentInstance(typeof (ITouchable));
			Assert.AreSame(t1, t2);
		}
	}
}