using System.Collections;
using NanoContainer.Script.CSharp;
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
			IPicoContainer pico = BuildContainer(new CSharpBuilder(GetStreamReader(@"NanoContainer.Tests.TestScripts.test.cs")), parent, new ArrayList());
			Assert.IsNotNull(pico);
			Assert.AreSame(parent, pico.Parent);
			Assert.AreEqual("C#", pico.GetComponentInstance("hello"));
		}

		/*[Test]
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
			IPicoContainer pico = BuildContainer(new CSharpBuilder(BuildStreamReader(code)), parent, new ArrayList());

			Assert.IsNotNull(pico);
			Assert.AreSame(parent, pico.Parent);
			Assert.AreEqual(1000, pico.GetComponentInstance(10));
		}

		private StreamReader BuildStreamReader(string code)
		{
			Stream stream = new MemoryStream();
			IFormatter formatter = new BinaryFormatter();
			formatter.Serialize(stream, code);

			return new StreamReader(stream);
		}*/
	}
}