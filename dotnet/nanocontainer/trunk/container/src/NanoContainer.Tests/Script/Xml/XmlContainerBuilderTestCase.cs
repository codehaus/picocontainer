using System;
using System.Collections;
using System.IO;
using System.Xml;
using NanoContainer.Script.Xml;
using NUnit.Framework;

namespace NanoContainer.Tests.Script.Xml
{
	[TestFixture]
	public class XMLContainerBuilderTestCase
	{
		[Test]
		public void GetCompiledType()
		{
			string xml = @"
				<container>
					<component-instance key='Hello'>XML</component-instance>
					<component-instance key='Hei'>XMLContinerBuilder</component-instance>
				</container>";

			MockXMLContainerBuilder containerBuilder = new MockXMLContainerBuilder(ScriptFixture.BuildStreamReader(xml));
			Type type = containerBuilder.CallGetCompiledType(new ArrayList());

			Assert.IsNotNull(type);
		}

		[Test] 
		public void RegisterAssemblies()
		{
			MockXMLContainerBuilder containerBuilder = new MockXMLContainerBuilder(null);
			FileInfo testCompDll = new FileInfo(@"../../../TestComp/bin/Debug/TestComp.dll");
			FileInfo testComp2Dll = new FileInfo(@"../../../TestComp2/bin/Debug/TestComp2.dll");
			
			string xml = @"<assemblies>
						      <element file='" + testCompDll.FullName + @"'/>
						      <element url='" + new Uri("file://" + testComp2Dll) + @"'/>
						  </assemblies>";

			XmlElement classpathElement = ConvertToXml(xml);
			IList assemblies = new ArrayList();

			containerBuilder.CallRegisterAssemblies(classpathElement, assemblies);

			Assert.AreEqual(2, assemblies.Count);
		}

		[Test]
		[ExpectedException(typeof(FileNotFoundException))]
		public void RegisterAssembliesUnknownFileFails()
		{
			MockXMLContainerBuilder containerBuilder = new MockXMLContainerBuilder(null);
			
			string xml = @"<assemblies>
						      <element file='this.should.fail'/>
						  </assemblies>";

			containerBuilder.CallRegisterAssemblies(ConvertToXml(xml), new ArrayList());
		}

		private XmlElement ConvertToXml(string code)
		{
			XmlDocument doc = new XmlDocument();
			doc.Load(ScriptFixture.BuildStream(code));
			return doc.DocumentElement;
		}
	}

	public class MockXMLContainerBuilder : XMLContainerBuilder
	{
		public MockXMLContainerBuilder(StreamReader streamReader) : base(streamReader)
		{
		}

		public void CallRegisterAssemblies(XmlElement classpathElement,
		                                  IList assemblies)
		{
			RegisterAssemblies(classpathElement, assemblies);
		}

		public Type CallGetCompiledType(IList assemblies)
		{
			return GetCompiledType(GetCompiledAssembly(this.StreamReader, assemblies));
		}
	}
}