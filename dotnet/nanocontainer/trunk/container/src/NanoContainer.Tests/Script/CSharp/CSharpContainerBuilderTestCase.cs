using System.Collections;
using System.Collections.Specialized;
using System.IO;
using NanoContainer;
using NanoContainer.Script.CSharp;
using NanoContainer.Tests.Script;
using NUnit.Framework;
using PicoContainer;
using PicoContainer.Defaults;

namespace Test.Script.CSharp
{
	[TestFixture]
	public class CSharpContainerBuilderTestCase : AbstractScriptedContainerBuilderTestCase
	{
		[Test]
		public void ContainerCanBeBuiltWithParent()
		{
			IMutablePicoContainer parent = new DefaultPicoContainer();
			ContainerBuilderFacade cbf = new CSharpContainerBuilderFacade(GetStreamReader(@"NanoContainer.Tests.TestScripts.test.cs"));
			IPicoContainer pico = cbf.Build(parent, new ArrayList());
			Assert.IsNotNull(pico);
			Assert.AreSame(parent, pico.Parent);
			Assert.AreEqual("C#", pico.GetComponentInstance("hello"));
		}

		[Test]
		public void NanoContainerCanBeBuiltFromCodeOnTheFly()
		{
			string code = @"
				using PicoContainer;
				using PicoContainer.Defaults;
				namespace Test 
				{
					public class NameTranslator 
					{
						private IPicoContainer parent;
						public IPicoContainer Parent {
							set { parent = value; } 
						}
				
						public IMutablePicoContainer Compose() {
							DefaultPicoContainer p = new DefaultPicoContainer(parent);
							p.RegisterComponentInstance(10, 1000);
							return p; 
						}
					}
				}";

			IMutablePicoContainer parent = new DefaultPicoContainer();
			ContainerBuilderFacade cbf = new CSharpContainerBuilderFacade(ScriptFixture.BuildStreamReader(code));
			IPicoContainer pico = cbf.Build(parent, new ArrayList());

			Assert.IsNotNull(pico);
			Assert.AreSame(parent, pico.Parent);
			Assert.AreEqual(1000, pico.GetComponentInstance(10));
		}

		[Test]
		public void ReferenceExternalAssembly()
		{
			string code = @"
				using PicoContainer;
				using PicoContainer.Defaults;
				
				public class MyNanoScript
				{
					private IPicoContainer parent;
					public IPicoContainer Parent {
						set { parent = value; } 
					}
				
					public IMutablePicoContainer Compose() {
						DefaultPicoContainer p = new DefaultPicoContainer(parent);
						p.RegisterComponentImplementation(""testcomp"", typeof(TestComp));
						return p; 
					}
				}";


			ContainerBuilderFacade cbf = new CSharpContainerBuilderFacade(ScriptFixture.BuildStreamReader(code));
			StringCollection assemblies = new StringCollection();

			FileInfo testCompDll = new FileInfo("../../../TestComp/bin/Debug/TestComp.dll");
			assemblies.Add(testCompDll.FullName);
			IPicoContainer pico = cbf.Build(assemblies);

			Assert.IsNotNull(pico);
			Assert.IsNotNull(pico.GetComponentInstance("testcomp"));
		}
	}
}