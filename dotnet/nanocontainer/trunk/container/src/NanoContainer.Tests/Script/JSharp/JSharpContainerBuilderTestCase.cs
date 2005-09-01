using System.Collections;
using NanoContainer;
using NanoContainer.Script.JSharp;
using NUnit.Framework;
using PicoContainer;
using PicoContainer.Defaults;

namespace Test.Script.JSharp
{
	[TestFixture]
	public class JSharpContainerBuilderTestCase : AbstractScriptedContainerBuilderTestCase
	{
		[Test]
		public void ContainerCanBeBuiltWithParent()
		{
			IMutablePicoContainer parent = new DefaultPicoContainer();
			ContainerBuilderFacade cbf = new JSharpContainerBuilderFacade(GetStreamReader(@"NanoContainer.Tests.TestScripts.test.java"));
			IPicoContainer pico = cbf.Build(parent, new ArrayList());
			Assert.IsNotNull(pico);
			Assert.AreSame(parent, pico.Parent);
			Assert.AreEqual("J#", pico.GetComponentInstance("hello"));
		}
	}
}