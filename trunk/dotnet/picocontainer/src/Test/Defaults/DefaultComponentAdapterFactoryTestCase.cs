using System;
using NUnit.Framework;
using PicoContainer;

using PicoContainer.Defaults;

using PicoContainer.Tests.TestModel;

namespace Test.Defaults {
  /// <summary>
  /// Summary description for DefaultComponentAdapterFactoryTestCase.
  /// </summary>
  /// 
  [TestFixture]
  public class DefaultComponentAdapterFactoryTestCase {

    protected IComponentAdapterFactory CreateComponentAdapterFactory() {
      return new DefaultComponentAdapterFactory();
    }

    [Test]
    public void testInstantiateComponentWithNoDependencies() {
      IComponentAdapter componentAdapter =
        CreateComponentAdapterFactory().CreateComponentAdapter(typeof(Touchable), typeof(SimpleTouchable), null);

      Object comp = componentAdapter.ComponentInstance;
      Assert.IsNotNull(comp);
      Assert.IsTrue(comp is SimpleTouchable);
    }

    [Test]
    public void testSingleUseComponentCanBeInstantiatedByDefaultIComponentAdapter() {
      IComponentAdapter componentAdapter = CreateComponentAdapterFactory().CreateComponentAdapter("o", typeof(object), null);
      Object component = componentAdapter.ComponentInstance;
      Assert.IsNotNull(component);
    }

  }
}
