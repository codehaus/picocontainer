using System.Collections;
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
			IPicoContainer pico = BuildContainer(new VBBuilder(GetStreamReader(@"NanoContainer.Test.TestScripts.test.vb")), parent, new ArrayList());
			Assert.IsNotNull(pico);
			Assert.AreSame(parent, pico.Parent);
			Assert.AreEqual("VB", pico.GetComponentInstance("hello"));
		}
	}
}