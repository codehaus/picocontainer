using System;
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
		public void BasicInstantiationAndContainment()
		{
			DefaultPicoContainer pico = (DefaultPicoContainer) CreatePicoContainerWithTouchableAndDependsOnTouchable();

			Assert.IsTrue(typeof (ITouchable).IsAssignableFrom(
				pico.GetComponentAdapterOfType(typeof (ITouchable)).ComponentImplementation));
		}

		[Test]
		public void ComponentInstancesFromParentsAreNotDirectlyAccessible()
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
		[ExpectedException(typeof (UnsatisfiableDependenciesException))]
		public void UpDownDependenciesCannotBeFollowed()
		{
			IMutablePicoContainer parent = CreatePicoContainer();
			IMutablePicoContainer child = CreatePicoContainer(parent);

			// ComponentF -> ComponentA -> ComponentB+ComponentC
			child.RegisterComponentImplementation(typeof (ComponentF));
			parent.RegisterComponentImplementation(typeof (ComponentA));
			child.RegisterComponentImplementation(typeof (ComponentB));
			child.RegisterComponentImplementation(typeof (ComponentC));

			// This should fail
			child.GetComponentInstance(typeof (ComponentF));
		}

		[Test]
		public void ComponentsCanBeRemovedByInstance()
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