using System.Collections;
using NanoContainer.Attributes;
using NanoContainer.IntegrationKit;
using NanoContainer.Test.TestModel;
using NUnit.Framework;
using PicoContainer;
using PicoContainer.Defaults;
using Test.Script;

namespace NanoContainer.Tests.Attributes
{
	[TestFixture]
	public class AttributeBasedNanoContainerTestCase : AbstractScriptedContainerBuilderTestCase
	{
		private IPicoContainer picoContainer;
		[SetUp]
		public void SetUp()
		{
			ContainerBuilder containerBuilder = new AttributeBasedContainerBuilder(this.GetType().Assembly);
			IMutablePicoContainer parent = new DefaultPicoContainer();
			picoContainer = BuildContainer(containerBuilder, parent, new ArrayList());

			Assert.IsNotNull(picoContainer);
			Assert.AreSame(parent, picoContainer.Parent);
		}

		[Test]
		public void ComponentsAreRegisteredWithCorrectKeys()
		{
			WebServer ws = picoContainer.GetComponentInstance("webserver") as WebServer;
			WebServerConfig config = picoContainer.GetComponentInstance(typeof(WebServerConfig)) as WebServerConfig;
			
			Assert.IsNotNull(ws);
			Assert.IsNull(config);

			config = picoContainer.GetComponentInstance(typeof(DefaultWebServerConfig)) as WebServerConfig;
			Assert.IsNotNull(config);
		}

		[Test]
		public void ComponentsAreRegisteredToCorrectTypes()
		{
			WebServer ws = picoContainer.GetComponentInstanceOfType(typeof(WebServer)) as WebServer;
			WebServerConfig config = picoContainer.GetComponentInstanceOfType(typeof(WebServerConfig)) as WebServerConfig;
			
			Assert.IsNotNull(ws);
			Assert.IsNotNull(config);
		}
	}
}
