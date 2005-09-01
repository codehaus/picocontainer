using System.Collections;
using NanoContainer;
using NanoContainer.Script.JS;
using NUnit.Framework;
using PicoContainer;
using PicoContainer.Defaults;

namespace Test.Script.JS
{
	[TestFixture]
	public class JSContainerBuilderTestCase : AbstractScriptedContainerBuilderTestCase
	{
		[Test]
		public void ContainerCanBeBuiltWithParent()
		{
			IMutablePicoContainer parent = new DefaultPicoContainer();
			ContainerBuilderFacade cbf = new JSContainerBuilderFacade(GetStreamReader(@"NanoContainer.Tests.TestScripts.test.js"));
			IPicoContainer pico = cbf.Build(parent, new ArrayList());
			Assert.IsNotNull(pico);
			Assert.AreSame(parent, pico.Parent);
			Assert.AreEqual("JScript", pico.GetComponentInstance("hello"));
		}
	}
}