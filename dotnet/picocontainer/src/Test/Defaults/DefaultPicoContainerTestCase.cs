using System.Collections;
using NUnit.Framework;
using PicoContainer;
using PicoContainer.Defaults;
using PicoContainer.Tests.Tck;
using PicoContainer.Tests.TestModel;

namespace Test.Defaults
{
	/// <summary>
	/// Summary description for DefaultPicoContainerTestCase.
	/// </summary>
	[TestFixture]
	public class DefaultPicoContainerTestCase : AbstractPicoContainerTestCase
	{
		protected override IMutablePicoContainer CreatePicoContainer(IPicoContainer parent)
		{
			return new DefaultPicoContainer(parent);
		}

		protected override IMutablePicoContainer CreatePicoContainer(IPicoContainer parent, ILifecycleManager lifecycleManager)
		{
			return new DefaultPicoContainer(new DefaultComponentAdapterFactory(), parent, lifecycleManager);
		}

		[Test]
		public void TestBasicInstantiationAndContainment()
		{
			DefaultPicoContainer pico = (DefaultPicoContainer) CreatePicoContainerWithTouchableAndDependsOnTouchable();

			Assert.IsTrue(typeof (Touchable).IsAssignableFrom(
				pico.GetComponentAdapterOfType(typeof (Touchable)).ComponentImplementation));
		}

		[Test]
		public void TestComponentInstancesFromParentsAreNotDirectlyAccessible()
		{
			IMutablePicoContainer a = new DefaultPicoContainer();
			IMutablePicoContainer b = new DefaultPicoContainer(a);
			IMutablePicoContainer c = new DefaultPicoContainer(b);

			object ao = new object();
			object bo = new object();
			object co = new object();

			a.RegisterComponentInstance("a", ao);
			b.RegisterComponentInstance("b", bo);
			c.RegisterComponentInstance("c", co);

			Assert.AreEqual(1, a.ComponentInstances.Count);
			Assert.AreEqual(1, b.ComponentInstances.Count);
			Assert.AreEqual(1, c.ComponentInstances.Count);
		}

		[Test]
		public void TestUpDownDependenciesCannotBeFollowed()
		{
			IMutablePicoContainer parent = CreatePicoContainer();
			IMutablePicoContainer child = CreatePicoContainer(parent);

			// ComponentF -> ComponentA -> ComponentB+ComponentC
			child.RegisterComponentImplementation(typeof (ComponentF));
			parent.RegisterComponentImplementation(typeof (ComponentA));
			child.RegisterComponentImplementation(typeof (ComponentB));
			child.RegisterComponentImplementation(typeof (ComponentC));

			try
			{
				child.GetComponentInstance(typeof (ComponentF));
				Assert.Fail();
			}
			catch (UnsatisfiableDependenciesException)
			{
			}
		}

		[Test]
		public void TestComponentsCanBeRemovedByInstance()
		{
			IMutablePicoContainer pico = CreatePicoContainer();
			pico.RegisterComponentImplementation(typeof (ArrayList));
			IList list = (IList) pico.GetComponentInstanceOfType(typeof (IList));
			pico.UnregisterComponentByInstance(list);
			Assert.AreEqual(0, pico.ComponentAdapters.Count);
			Assert.AreEqual(0, pico.ComponentInstances.Count);
			Assert.IsNull(pico.GetComponentInstanceOfType(typeof (IList)));
		}


	}
}