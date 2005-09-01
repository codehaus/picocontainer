using System.Collections;
using NanoContainer;
using NanoContainer.Script.VB;
using NUnit.Framework;
using PicoContainer;
using PicoContainer.Defaults;

namespace Test.Script.VB
{
	[TestFixture]
	public class JSContainerBuilderTestCase : AbstractScriptedContainerBuilderTestCase
	{
		[Test]
		public void ContainerCanBeBuiltWithParent()
		{
			IMutablePicoContainer parent = new DefaultPicoContainer();
			ContainerBuilderFacade cbf = new VBContainerBuilderFacade(GetStreamReader(@"NanoContainer.Tests.TestScripts.test.vb"));
			IPicoContainer pico = cbf.Build(parent, new ArrayList());
			Assert.IsNotNull(pico);
			Assert.AreSame(parent, pico.Parent);
			Assert.AreEqual("VB", pico.GetComponentInstance("hello"));
		}
	}
}