using System;
using NanoContainer.Test.TestModel;
using NUnit.Framework;

namespace NanoContainer.Tests
{
	[TestFixture]
	public class DefaultNanoContainerTestCase
	{
		[Test]
		public void RegisterComponentByName()
		{
			DefaultNanoContainer nanoContainer = new DefaultNanoContainer();
			nanoContainer.RegisterComponentImplementation("NanoContainer.Test.TestModel.DefaultWebServer");
			nanoContainer.RegisterComponentImplementation("NanoContainer.Test.TestModel.DefaultWebServerConfig");

			Assert.IsNotNull(nanoContainer.Pico.GetComponentInstanceOfType(typeof(WebServer)));
			Assert.IsNotNull(nanoContainer.Pico.GetComponentInstance(typeof(DefaultWebServerConfig)));
		}

		[Test]
		public void RegisterComponentByNameWithKey()
		{
			DefaultNanoContainer nanoContainer = new DefaultNanoContainer();
			nanoContainer.RegisterComponentImplementation("webserver", "NanoContainer.Test.TestModel.DefaultWebServer");
			nanoContainer.RegisterComponentImplementation("config", "NanoContainer.Test.TestModel.DefaultWebServerConfig");

			Assert.IsNotNull(nanoContainer.Pico.GetComponentInstance("webserver"));
			Assert.IsNotNull(nanoContainer.Pico.GetComponentInstance("config"));
		}

		[Test]
		public void EnsureStandardTypesCanBeRegisteredByDefault()
		{
			DefaultNanoContainer nanoContainer = new DefaultNanoContainer();
			string typeName = "System.Text.StringBuilder";
			
			nanoContainer.RegisterComponentImplementation(typeName, typeName);
			Assert.IsNotNull(nanoContainer.Pico.GetComponentInstance(typeName));
		}

		[Test]
		[ExpectedException(typeof(TypeLoadException))]
		public void RegisterFailsForUnknownTypeName()
		{
			DefaultNanoContainer nanoContainer = new DefaultNanoContainer();
			nanoContainer.RegisterComponentImplementation("this is not a valid type name");
		}

		[Test]
		[ExpectedException(typeof(TypeLoadException))]
		public void EnsureThatTypeFromExternalAssemblyIsNotFound()
		{
			DefaultNanoContainer nanoContainer = new DefaultNanoContainer();
			nanoContainer.RegisterComponentImplementation("TestComp");
		}
	}
}
