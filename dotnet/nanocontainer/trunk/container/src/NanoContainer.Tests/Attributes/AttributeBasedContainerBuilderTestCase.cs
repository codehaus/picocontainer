using System;
using NanoContainer.Attributes;
using NanoContainer.Test.TestModel;
using NanoContainer.Tests.Attributes.Custom;
using NUnit.Framework;
using PicoContainer;
using PicoContainer.Defaults;

namespace NanoContainer.Tests.Attributes
{
	[TestFixture]
	public class AttributeBasedContainerBuilderTestCase
	{
		private IPicoContainer picoContainer;

		[SetUp]
		public void SetUp()
		{
			ContainerBuilderFacade containerBuilderFacade = new AttributeBasedContainerBuilderFacade();
			picoContainer = containerBuilderFacade.Build(new string[] {"NanoContainer.Tests.dll"});
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

		[Test]
		public void NonCachingCtorBasedInjectionAdapterIsUsed()
		{
			IComponentAdapter ca = picoContainer.GetComponentAdapter(typeof(NonCachingCtorBasedComponent));

			Assert.IsTrue(ca is ConstructorInjectionComponentAdapter);
		}

		[Test]
		public void CustomComponentAdapterIsUsed()
		{
			IComponentAdapter ca = picoContainer.GetComponentAdapter(typeof(CustomComponent));

			Assert.AreEqual(typeof(TestCustomComponentAdapter),ca.GetType());
		}

		[Test]
		public void NonCachingSetterBasedInjectionAdapterIsUsed()
		{
			IComponentAdapter ca = picoContainer.GetComponentAdapter(typeof(NonCachingSetterBasedComponent));
			Assert.IsTrue(ca is SetterInjectionComponentAdapter);

			ITestComponent component = picoContainer.GetComponentInstance(typeof(NonCachingSetterBasedComponent)) as ITestComponent;
			Assert.IsNotNull(component.WebServer);
		}

		[Test]
		public void CachingCtorBasedInjectionAdapterIsUsed()
		{
			Type cachingCtorType = typeof(CachingCtorBasedComponent);
			IComponentAdapter ca = picoContainer.GetComponentAdapter(cachingCtorType);
			Assert.IsTrue(ca is CachingComponentAdapter);

			ITestComponent component = picoContainer.GetComponentInstance(cachingCtorType) as ITestComponent;
			Assert.IsNotNull(component.WebServer);

			Assert.AreSame(component, picoContainer.GetComponentInstance(cachingCtorType));
		}

		[Test]
		public void CachingSetterBasedInjectionAdapterIsUsed()
		{
			Type cachingSetterType = typeof(CachingSetterBasedComponent);
			IComponentAdapter ca = picoContainer.GetComponentAdapter(cachingSetterType);
			Assert.IsTrue(ca is CachingComponentAdapter);

			ITestComponent component = picoContainer.GetComponentInstance(cachingSetterType) as ITestComponent;
			Assert.IsNotNull(component.WebServer);
			Assert.AreSame(component, picoContainer.GetComponentInstance(cachingSetterType));
		}
	}
}
