using System;
using NUnit.Framework;
using PicoContainer.Defaults;
using PicoContainer;
using PicoContainer.Tests.TestModel;

namespace Test.Defaults {
  [TestFixture]
  public class DelegatingPicoContainerTestCase {
    private IMutablePicoContainer parent;
    private DefaultPicoContainer child;

    [SetUp]
    public void SetUp() {
      parent = new DefaultPicoContainer();
      child = new DefaultPicoContainer(parent);
    }

    public void testChildGetsFromParent() {
      parent.RegisterComponentImplementation(typeof(SimpleTouchable));
      child.RegisterComponentImplementation(typeof(DependsOnTouchable));
      DependsOnTouchable dependsOnTouchable = (DependsOnTouchable) child.GetComponentInstance(typeof(DependsOnTouchable));

      Assert.IsNotNull(dependsOnTouchable);
    }

    [ExpectedException(typeof(UnsatisfiableDependenciesException))]
    public void testParentDoesntGetFromChild() {
      child.RegisterComponentImplementation(typeof(SimpleTouchable));
      parent.RegisterComponentImplementation(typeof(DependsOnTouchable));

      parent.GetComponentInstance(typeof(DependsOnTouchable));
    }

    public void testChildOverridesParent() {
      parent.RegisterComponentImplementation(typeof(SimpleTouchable));
      child.RegisterComponentImplementation(typeof(SimpleTouchable));

      SimpleTouchable parentTouchable = (SimpleTouchable) parent.GetComponentInstance(typeof(SimpleTouchable));
      SimpleTouchable childTouchable = (SimpleTouchable) child.GetComponentInstance(typeof(SimpleTouchable));
      Assert.AreEqual(1, child.ComponentInstances.Count);
      Assert.IsFalse(parentTouchable.Equals( childTouchable));
    }


  }
}
