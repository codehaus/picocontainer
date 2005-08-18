using System;
using System.Collections;
using System.IO;
using System.Text;
using NanoContainer.IntegrationKit;
using NanoContainer.Script.Xml;
using NanoContainer.Test.TestModel;
using NUnit.Framework;
using PicoContainer;
using PicoContainer.Defaults;

namespace Test.Script.Xml
{
	[TestFixture]
	public class XmlScriptsTestCase : AbstractScriptedContainerBuilderTestCase
	{
		[Test]
		public void SimpleContent()
		{
			string xmlScript = @"
				<container>
					<component-instance key='Hello'>XML</component-instance>
					<component-instance key='Hei'>XMLContinerBuilder</component-instance>
				</container>";

			StreamReader scriptStream = new StreamReader(new MemoryStream(new ASCIIEncoding().GetBytes(xmlScript)));

			IMutablePicoContainer parent = new DefaultPicoContainer();
			IPicoContainer pico = BuildContainer(new XMLContainerBuilder(scriptStream), parent, new ArrayList());

			Assert.AreEqual("XML", pico.GetComponentInstance("Hello"));
			Assert.AreEqual("XMLContinerBuilder", pico.GetComponentInstance("Hei"));
		}

		[Test]
		public void CreateSimpleContainer()
		{
			string xmlScript = @"
				<container>
					<component-implementation class='System.Text.StringBuilder'/>
					<component-implementation class='NanoContainer.Test.TestModel.DefaultWebServerConfig'/>
					<component-implementation key='NanoContainer.Test.TestModel.WebServer' class='NanoContainer.Test.TestModel.DefaultWebServer'/>
				</container>";

			StreamReader scriptStream = new StreamReader(new MemoryStream(new ASCIIEncoding().GetBytes(xmlScript)));
			IMutablePicoContainer parent = new DefaultPicoContainer();
			IPicoContainer pico = BuildContainer(new XMLContainerBuilder(scriptStream), parent, new ArrayList());

			Assert.IsNotNull(pico.GetComponentInstance(typeof (StringBuilder)));
			Assert.IsNotNull(pico.GetComponentInstance(typeof (DefaultWebServerConfig)));
			Assert.IsNotNull(pico.GetComponentInstance("NanoContainer.Test.TestModel.WebServer"));
		}

		[Test]
		public void CreateSimpleContainerWithExplicitKeysAndParameters()
		{
			string xmlScript = @"
					<container>
						<component-implementation key='aBuffer' class='System.Text.StringBuilder'/>
						<component-implementation key='NanoContainer.Test.TestModel.WebServerConfig' class='NanoContainer.Test.TestModel.DefaultWebServerConfig'/>
						<component-implementation key='NanoContainer.Test.TestModel.WebServer' class='NanoContainer.Test.TestModel.DefaultWebServer'>
				 				<parameter key='NanoContainer.Test.TestModel.WebServerConfig'/>
				 				<parameter key='aBuffer'/>
						</component-implementation>
					</container>";

			StreamReader scriptStream = new StreamReader(new MemoryStream(new ASCIIEncoding().GetBytes(xmlScript)));

			IMutablePicoContainer parent = new DefaultPicoContainer();
			IPicoContainer pico = BuildContainer(new XMLContainerBuilder(scriptStream), parent, new ArrayList());

			Assert.AreEqual(3, pico.ComponentInstances.Count);
			Assert.IsNotNull(pico.GetComponentInstance("aBuffer"));
			Assert.IsNotNull(pico.GetComponentInstance("NanoContainer.Test.TestModel.WebServerConfig"));
			Assert.IsNotNull(pico.GetComponentInstance("NanoContainer.Test.TestModel.WebServer"));
		}

		[Test]
		public void NonParameterElementsAreIgnoredInComponentImplementation()
		{
			string xmlScript = @"
					<container>
						<component-implementation key='aBuffer' class='System.Text.StringBuilder'/>
						<component-implementation key='NanoContainer.Test.TestModel.WebServerConfig' class='NanoContainer.Test.TestModel.DefaultWebServerConfig'/>
						<component-implementation key='NanoContainer.Test.TestModel.WebServer' class='NanoContainer.Test.TestModel.DefaultWebServer'>
				 				<parameter key='NanoContainer.Test.TestModel.WebServerConfig'/>
				 				<parameter key='aBuffer'/>
								<any-old-stuff/>
						</component-implementation>
					</container>";

			StreamReader scriptStream = new StreamReader(new MemoryStream(new ASCIIEncoding().GetBytes(xmlScript)));

			IMutablePicoContainer parent = new DefaultPicoContainer();
			IPicoContainer pico = BuildContainer(new XMLContainerBuilder(scriptStream), parent, new ArrayList());

			Assert.AreEqual(3, pico.ComponentInstances.Count);
			Assert.IsNotNull(pico.GetComponentInstance("aBuffer"));
			Assert.IsNotNull(pico.GetComponentInstance("NanoContainer.Test.TestModel.WebServerConfig"));
			Assert.IsNotNull(pico.GetComponentInstance("NanoContainer.Test.TestModel.WebServer"));
		}

		[Test]
		public void ContainerCanHostAChild()
		{
			string xmlScript = @"
					<container>
						<component-implementation key='NanoContainer.Test.TestModel.WebServerConfig' class='NanoContainer.Test.TestModel.DefaultWebServerConfig'/>
						<component-implementation class='System.Text.StringBuilder'/>
						<container>
							<component-implementation key='NanoContainer.Test.TestModel.WebServer' class='NanoContainer.Test.TestModel.DefaultWebServer'/>
						</container>
					</container>";

			StreamReader scriptStream = new StreamReader(new MemoryStream(new ASCIIEncoding().GetBytes(xmlScript)));

			IMutablePicoContainer parent = new DefaultPicoContainer();
			IPicoContainer pico = BuildContainer(new XMLContainerBuilder(scriptStream), parent, new ArrayList());

			Assert.IsNotNull(pico.GetComponentInstance("NanoContainer.Test.TestModel.WebServerConfig"));

			IPicoContainer childcontainer = (DefaultPicoContainer) pico.GetComponentInstance(typeof (DefaultPicoContainer));

			Assert.IsNotNull(childcontainer);
			Assert.IsNotNull(childcontainer.GetComponentInstance("NanoContainer.Test.TestModel.WebServer"));

			StringBuilder sb = (StringBuilder) pico.GetComponentInstance(typeof (StringBuilder));
			Assert.IsTrue(sb.ToString().IndexOf("-WebServer") != -1);
		}

		[Test]
		public void TypeLoaderHierarchy()
		{
			FileInfo testCompDll = new FileInfo("../../../TestComp/bin/Debug/TestComp.dll");
			FileInfo testCompDll2 = new FileInfo("../../../TestComp2/bin/Debug/TestComp2.dll");
			FileInfo notStartableDll = new FileInfo("../../../NotStartable/bin/Debug/NotStartable.dll");

			Assert.IsTrue(testCompDll.Exists);
			Assert.IsTrue(testCompDll2.Exists);
			Assert.IsTrue(notStartableDll.Exists);
			
			string xmlScript = "" +
					"<container>" +
					"  <assemblies>" +
					"    <element file='" + testCompDll.FullName + "'/>" +
					"  </assemblies>" +
					"  <component-implementation key='foo' class='TestComp'/>" +
					"  <container>" +
					"    <assemblies>" +
					"      <element file='" + testCompDll2.FullName + "'/>" +
					"      <element file='" + notStartableDll.FullName + "'/>" +
					"    </assemblies>" +
					"    <component-implementation key='bar' class='TestComp2'/>" +
					"    <component-implementation key='phony' class='NotStartable'/>" +
					"  </container>" +
					"  <component-implementation class='System.Text.StringBuilder'/>" +
					"</container>";

			StreamReader scriptStream = new StreamReader(new MemoryStream(new ASCIIEncoding().GetBytes(xmlScript)));

			IMutablePicoContainer parent = new DefaultPicoContainer();
			IPicoContainer pico = BuildContainer(new XMLContainerBuilder(scriptStream), parent, new ArrayList());

			object fooTestComp = pico.GetComponentInstance("foo");
			Assert.IsNotNull(fooTestComp, "Container should have a 'foo' component");

			StringBuilder sb = (StringBuilder) pico.GetComponentInstance(typeof(StringBuilder));
			Assert.IsTrue(sb.ToString().IndexOf("-TestComp2") != -1, "Container should have instantiated a 'TestComp2' component because it is Startable");
			// We are using the DefaultLifecycleManager, which only instantiates Startable components, and not non-Startable components.
			Assert.IsTrue(sb.ToString().IndexOf("-NotStartable") == -1, "Container should NOT have instantiated a 'NotStartable' component because it is NOT Startable");
		}

		[Test]
		[ExpectedException(typeof (PicoCompositionException))]
		public void UnknownClassThrowsPicoCompositionException()
		{
			string xmlScript = @"
					<container>
						<component-implementation class='Foo'/>
					</container>";

			StreamReader scriptStream = new StreamReader(new MemoryStream(new ASCIIEncoding().GetBytes(xmlScript)));
			IMutablePicoContainer parent = new DefaultPicoContainer();
			BuildContainer(new XMLContainerBuilder(scriptStream), parent, new ArrayList());
		}

		[Test]
		[ExpectedException(typeof (PicoCompositionException))]
		public void ConstantParameterWithNoChildElementThrowsPicoCompositionException()
		{
			string xmlScript = @"
					<container>
						<component-implementation key='NanoContainer.Test.TestModel.WebServer' class='NanoContainer.Test.TestModel.DefaultWebServer'>
				 			<parameter/>
				 			<parameter/>
					</component-implementation>
					</container>";

			StreamReader scriptStream = new StreamReader(new MemoryStream(new ASCIIEncoding().GetBytes(xmlScript)));
			IMutablePicoContainer parent = new DefaultPicoContainer();
			BuildContainer(new XMLContainerBuilder(scriptStream), parent, new ArrayList());
		}

		[Test]
		public void EmptyScriptDoesNotThrowsEmptyCompositionException()
		{
			string xmlScript = @"<container/>";

			StreamReader scriptStream = new StreamReader(new MemoryStream(new ASCIIEncoding().GetBytes(xmlScript)));
			BuildContainer(new XMLContainerBuilder(scriptStream), null, new ArrayList());
		}

		[Test]
		[ExpectedException(typeof (ArgumentNullException))]
		public void CreateContainerFromNullScriptThrowsArgumentNullException()
		{
			string xmlScript = null;
			StreamReader scriptStream = new StreamReader(new MemoryStream(new ASCIIEncoding().GetBytes(xmlScript)));
			BuildContainer(new XMLContainerBuilder(scriptStream), null, new ArrayList());
		}
	}
}