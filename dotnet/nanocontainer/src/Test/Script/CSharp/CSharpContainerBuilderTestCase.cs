using System.Collections;
using System.IO;
using System.Runtime.Serialization;
using System.Runtime.Serialization.Formatters.Binary;
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
			IPicoContainer pico = BuildContainer(new CSharpBuilder(GetStreamReader(@"NanoContainer.Test.TestScripts.test.cs")), parent, new ArrayList());
			Assert.IsNotNull(pico);
			Assert.AreSame(parent, pico.Parent);
			Assert.AreEqual("C#", pico.GetComponentInstance("hello"));
		}

		/*[Test]
		public void NanoContainerCanBeBuiltFromCodeOnTheFly()
		{
			string code = 
				"using PicoContainer.Core;\n" +
				"using PicoContainer.Defaults;\n" +
				"namespace Test \n" +
				"{\n" +
				"	public class NameTranslator\n" + 
				"	{\n" +
				"		private IPicoContainer parent;\n" +
				"		public IPicoContainer Parent \n{ " +
				"			set { parent = value; } \n" +
				"		}\n" +
				"\n" +
				"		public IMutablePicoContainer Compose() {\n" +
				"			DefaultPicoContainer p = new DefaultPicoContainer(parent);\n" +
				"			p.RegisterComponentInstance(\"hello\", \"C#\");\n" +
				"			return p;\n" + 
				"		}\n" +
				"	}\n" +
				"}\n";

			IMutablePicoContainer parent = new DefaultPicoContainer();
			IPicoContainer pico = BuildContainer(new CSharpBuilder(BuildStreamReader(code)), parent, new ArrayList());

			Assert.IsNotNull(pico);
			Assert.AreSame(parent, pico.Parent);
			Assert.AreEqual("C#", pico.GetComponentInstance("hello"));

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