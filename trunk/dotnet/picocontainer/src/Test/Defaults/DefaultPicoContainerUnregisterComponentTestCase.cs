using NUnit.Framework;
using PicoContainer.Defaults;
using PicoContainer.Tests.TestModel;

namespace Test.Defaults
{
	[TestFixture]
	public class DefaultPicoContainerUnregisterComponentTestCase
	{
		private DefaultPicoContainer picoContainer;

		[SetUp]
		protected void SetUp()
		{
			picoContainer = new DefaultPicoContainer();
		}

		public void testCannotInstantiateAnUnregisteredComponent()
		{
			picoContainer.RegisterComponentImplementation(typeof (Touchable), typeof (SimpleTouchable));
			object o = picoContainer.ComponentInstances;
			picoContainer.UnregisterComponent(typeof (Touchable));

			Assert.IsTrue(picoContainer.ComponentInstances.Count == 0);
		}

		public void testCanInstantiateReplacedComponent()
		{
			picoContainer.RegisterComponentImplementation(typeof (Touchable), typeof (SimpleTouchable));
			object o = picoContainer.ComponentInstances;
			picoContainer.UnregisterComponent(typeof (Touchable));

			picoContainer.RegisterComponentImplementation(typeof (Touchable), typeof (AlternativeTouchable));

			Assert.AreEqual(1, picoContainer.ComponentInstances.Count);
		}

		public void testUnregisterAfterInstantiateComponents()
		{
			picoContainer.RegisterComponentImplementation(typeof (Touchable), typeof (SimpleTouchable));
			object o = picoContainer.ComponentInstances;
			picoContainer.UnregisterComponent(typeof (Touchable));
			Assert.IsNull(picoContainer.GetComponentInstance(typeof (Touchable)));
		}


		public void testReplacedInstantiatedComponentHasCorrectClass()
		{
			picoContainer.RegisterComponentImplementation(typeof (Touchable), typeof (SimpleTouchable));
			object o = picoContainer.ComponentInstances;
			picoContainer.UnregisterComponent(typeof (Touchable));

			picoContainer.RegisterComponentImplementation(typeof (Touchable), typeof (AlternativeTouchable));
			object component = picoContainer.ComponentInstances[0];

			Assert.AreEqual(typeof (AlternativeTouchable), component.GetType());
		}

	}
}