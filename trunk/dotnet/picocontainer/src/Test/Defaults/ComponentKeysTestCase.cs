using System;

using NUnit.Framework;
using PicoContainer;

using PicoContainer.Defaults;

using PicoContainer.Tests.TestModel;
namespace Test.Defaults {
  /// <summary>
  /// Summary description for ComponentKeysTestCase.
  /// </summary>
  [TestFixture]
  public class ComponentKeysTestCase {
    [Test]
    public void testComponensRegisteredWithClassKeyTakePrecedenceOverOthersWhenThereAreMultipleImplementations() {
      DefaultPicoContainer pico = new DefaultPicoContainer();
      pico.RegisterComponentImplementation("default", typeof(SimpleTouchable));

      /*
       * By using a class as key, this should take precedence over the other Touchable (Simmpe)
       */
      pico.RegisterComponentImplementation(typeof(Touchable), typeof(DecoratedTouchable), new IParameter[] {
                                                                                        new ComponentParameter("default")
                                                                                      });

      Touchable touchable = (Touchable) pico.GetComponentInstanceOfType(typeof(Touchable));
      Assert.AreEqual(typeof(DecoratedTouchable), touchable.GetType());
    }

    [Test]
    public void testIComponentAdapterResolutionIsFirstLookedForByClassKeyToTheTopOfTheContainerHierarchy() {
      DefaultPicoContainer pico = new DefaultPicoContainer();
      pico.RegisterComponentImplementation("default", typeof(SimpleTouchable));

      pico.RegisterComponentImplementation(typeof(Touchable), typeof(DecoratedTouchable), new IParameter[] {
                                                                                        new ComponentParameter("default")
                                                                                      });

      DefaultPicoContainer grandChild = new DefaultPicoContainer(new DefaultPicoContainer(new DefaultPicoContainer(pico)));

      Touchable touchable = (Touchable) grandChild.GetComponentInstanceOfType(typeof(Touchable));
      Assert.AreEqual(typeof(DecoratedTouchable), touchable.GetType());

    }
  }
}
