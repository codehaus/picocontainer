using System.Collections;
using NanoContainer.Script.JSharp;
using NUnit.Framework;
using PicoContainer.Core;
using PicoContainer.Defaults;

namespace Test.Script.Java
{
	[TestFixture]
	public class JSharpContainerBuilderTestCase : AbstractScriptedContainerBuilderTestCase
	{
		[Test]
		public void ContainerCanBeBuiltWithParent()
		{
			IMutablePicoContainer parent = new DefaultPicoContainer();
			IPicoContainer pico = BuildContainer(new JSharpBuilder(GetStreamReader(@"NanoContainer.Test.TestScripts.test.java")), parent, new ArrayList());
			Assert.IsNotNull(pico);
			Assert.AreSame(parent, pico.Parent);
			Assert.AreEqual("J#", pico.GetComponentInstance("hello"));
		}
	}
}