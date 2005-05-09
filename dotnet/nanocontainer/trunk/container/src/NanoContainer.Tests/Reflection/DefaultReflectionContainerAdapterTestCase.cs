using System;
using NanoContainer.Reflection;
using NanoContainer.Test.TestModel;
using NUnit.Framework;

/// Removed all classloader juggling testcases. 
/// Classloaders are not available use 
/// AppDomain.CurrentDomain.AssemblyLoad.AssemblyResolve += new ResolveEventHandler(MyResolveEventHandler)
/// For resolving types. 
namespace Test.Reflection
{
	[TestFixture]
	public class DefaultReflectionContainerAdapterTestCase
	{
		[Test]
		public void Basic()
		{
			IReflectionContainerAdapter reflectionFrontEnd = new DefaultReflectionContainerAdapter();
			reflectionFrontEnd.RegisterComponentImplementation("NanoContainer.Test.TestModel.DefaultWebServerConfig");
			reflectionFrontEnd.RegisterComponentImplementation("NanoContainer.Test.TestModel.WebServer", "NanoContainer.Test.TestModel.DefaultWebServer");
		}

		[Test]
		public void Provision()
		{
			IReflectionContainerAdapter reflectionFrontEnd = new DefaultReflectionContainerAdapter();
			reflectionFrontEnd.RegisterComponentImplementation("NanoContainer.Test.TestModel.DefaultWebServerConfig");
			reflectionFrontEnd.RegisterComponentImplementation("NanoContainer.Test.TestModel.DefaultWebServer");

			Assert.IsNotNull(reflectionFrontEnd.PicoContainer.GetComponentInstance(typeof (DefaultWebServer)));
			Assert.IsTrue(reflectionFrontEnd.PicoContainer.GetComponentInstance(typeof (DefaultWebServer)) is DefaultWebServer);
		}

		[Test]
		[ExpectedException(typeof (TypeLoadException))]
		public void NoGenerationRegistration()
		{
			IReflectionContainerAdapter reflectionFrontEnd = new DefaultReflectionContainerAdapter();
			reflectionFrontEnd.RegisterComponentImplementation("Ping");
		}

		[Test]
		public void ParametersCanBePassedInStringForm()
		{
			IReflectionContainerAdapter reflectionFrontEnd = new DefaultReflectionContainerAdapter();
			string className = typeof (ThingThatTakesParamsInConstructor).FullName;

			reflectionFrontEnd.RegisterComponentImplementation(
				"thing",
				className,
				new string[]
					{
						"System.String",
						"System.Int32",
						typeof (bool).FullName
					},
				new string[]
					{
						"hello",
						"22",
						"true"
					}
				);

			ThingThatTakesParamsInConstructor thing =
				(ThingThatTakesParamsInConstructor) reflectionFrontEnd.PicoContainer.GetComponentInstance("thing");
			Assert.IsNotNull(thing);
			Assert.AreEqual("hello22" + bool.TrueString, thing.Value);
		}
	}
}