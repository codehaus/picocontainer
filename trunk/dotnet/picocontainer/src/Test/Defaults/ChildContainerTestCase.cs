using PicoContainer;
using PicoContainer.Defaults;
using NUnit.Framework;
using PicoContainer.Tests.TestModel;

namespace Test.Defaults
{
	[TestFixture]
	public class ChildContainerTestCase
	{
		public void testParentContainerWithComponentWithEqualKeyShouldBeShadowedByChild()
		{
			DefaultPicoContainer parent = new DefaultPicoContainer();
			DefaultPicoContainer child = new DefaultPicoContainer(parent);

			parent.RegisterComponentImplementation("key", typeof (AlternativeTouchable));
			child.RegisterComponentImplementation("key", typeof (SimpleTouchable));
			child.RegisterComponentImplementation(typeof (DependsOnTouchable));

			DependsOnTouchable dot = (DependsOnTouchable) child.GetComponentInstanceOfType(typeof (DependsOnTouchable));
			Assert.AreEqual(typeof (SimpleTouchable), dot.getTouchable().GetType());
		}

		public void testParentComponentRegisteredAsClassShouldBePreffered()
		{
			DefaultPicoContainer parent = new DefaultPicoContainer();
			DefaultPicoContainer child = new DefaultPicoContainer(parent);

			parent.RegisterComponentImplementation(typeof (Touchable), typeof (AlternativeTouchable));
			child.RegisterComponentImplementation("key", typeof (SimpleTouchable));
			child.RegisterComponentImplementation(typeof (DependsOnTouchable));

			DependsOnTouchable dot = (DependsOnTouchable) child.GetComponentInstanceOfType(typeof (DependsOnTouchable));
			Assert.AreEqual(typeof (AlternativeTouchable), dot.getTouchable().GetType());
		}

		public void testResolveFromParentByType()
		{
			IMutablePicoContainer parent = new DefaultPicoContainer();
			parent.RegisterComponentImplementation(typeof (Touchable), typeof (SimpleTouchable));

			IMutablePicoContainer child = new DefaultPicoContainer(parent);
			child.RegisterComponentImplementation(typeof (DependsOnTouchable));

			Assert.IsNotNull(child.GetComponentInstance(typeof (DependsOnTouchable)));
		}

		public void testResolveFromParentByKey()
		{
			IMutablePicoContainer parent = new DefaultPicoContainer();
			parent.RegisterComponentImplementation(typeof (Touchable), typeof (SimpleTouchable));

			IMutablePicoContainer child = new DefaultPicoContainer(parent);
			child.RegisterComponentImplementation(typeof (DependsOnTouchable), typeof (DependsOnTouchable),
			                                      new IParameter[] {new ComponentParameter(typeof (Touchable))});

			Assert.IsNotNull(child.GetComponentInstance(typeof (DependsOnTouchable)));
		}

		public void testResolveFromGrandParentByType()
		{
			IMutablePicoContainer grandParent = new DefaultPicoContainer();
			grandParent.RegisterComponentImplementation(typeof (Touchable), typeof (SimpleTouchable));

			IMutablePicoContainer parent = new DefaultPicoContainer(grandParent);

			IMutablePicoContainer child = new DefaultPicoContainer(parent);
			child.RegisterComponentImplementation(typeof (DependsOnTouchable));

			Assert.IsNotNull(child.GetComponentInstance(typeof (DependsOnTouchable)));
		}

		public void testResolveFromGrandParentByKey()
		{
			IMutablePicoContainer grandParent = new DefaultPicoContainer();
			grandParent.RegisterComponentImplementation(typeof (Touchable), typeof (SimpleTouchable));

			IMutablePicoContainer parent = new DefaultPicoContainer(grandParent);

			IMutablePicoContainer child = new DefaultPicoContainer(parent);
			child.RegisterComponentImplementation(typeof (DependsOnTouchable), typeof (DependsOnTouchable),
			                                      new IParameter[] {new ComponentParameter(typeof (Touchable))});

			Assert.IsNotNull(child.GetComponentInstance(typeof (DependsOnTouchable)));
		}

	}
}