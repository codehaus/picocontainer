using System;
using NUnit.Framework;
using NanoContainer.Reflection;
using NanoContainer.Test.TestModel;

/// Removed all classloader juggling testcases. 
/// Classloaders are not available use 
/// AppDomain.CurrentDomain.AssemblyLoad.AssemblyResolve += new ResolveEventHandler(MyResolveEventHandler)
/// For resolving types. 

namespace Test.Reflection {
  [TestFixture]
  public class DefaultReflectionContainerAdapterTestCase {
    [Test]
    public void TestBasic() {
      IReflectionContainerAdapter reflectionFrontEnd = new DefaultReflectionContainerAdapter();
      reflectionFrontEnd.RegisterComponentImplementation("NanoContainer.Test.TestModel.DefaultWebServerConfig");
      reflectionFrontEnd.RegisterComponentImplementation("NanoContainer.Test.TestModel.WebServer", "NanoContainer.Test.TestModel.DefaultWebServer");
    }

    [Test]
    public void TestProvision() {
      IReflectionContainerAdapter reflectionFrontEnd = new DefaultReflectionContainerAdapter();
      reflectionFrontEnd.RegisterComponentImplementation("NanoContainer.Test.TestModel.DefaultWebServerConfig");
      reflectionFrontEnd.RegisterComponentImplementation("NanoContainer.Test.TestModel.DefaultWebServer");

      Assert.IsNotNull(reflectionFrontEnd.PicoContainer.GetComponentInstance(typeof(DefaultWebServer)));
      Assert.IsTrue(reflectionFrontEnd.PicoContainer.GetComponentInstance(typeof(DefaultWebServer)) is DefaultWebServer);
    }

    [Test]
    [ExpectedException(typeof(TypeLoadException))]
    public void TestNoGenerationRegistration() {
      IReflectionContainerAdapter reflectionFrontEnd = new DefaultReflectionContainerAdapter();
      reflectionFrontEnd.RegisterComponentImplementation("Ping");
    }

    [Test]
    public void TestParametersCanBePassedInStringForm() {
      IReflectionContainerAdapter reflectionFrontEnd = new DefaultReflectionContainerAdapter();
      string className = typeof(ThingThatTakesParamsInConstructor).FullName;

      reflectionFrontEnd.RegisterComponentImplementation(
        "thing",
        className,
        new String[]{
                      "System.String",
                      "System.Int32",
        typeof(bool).FullName
                    },
        new String[]{
                      "hello",
                      "22",
                      "true"
                    }
        );

      ThingThatTakesParamsInConstructor thing =
        (ThingThatTakesParamsInConstructor) reflectionFrontEnd.PicoContainer.GetComponentInstance("thing");
      Assert.IsNotNull(thing);
      Assert.AreEqual("hello22"+bool.TrueString, thing.Value);
    }
  }
}
