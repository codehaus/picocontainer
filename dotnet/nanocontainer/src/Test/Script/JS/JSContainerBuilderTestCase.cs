using System.Collections;
using NanoContainer.Script.JS;
using NUnit.Framework;
using PicoContainer.Core;
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
			IPicoContainer pico = BuildContainer(new JSBuilder(GetStreamReader(@"NanoContainer.Test.TestScripts.test.js")), parent, new ArrayList());
			Assert.IsNotNull(pico);
			Assert.AreSame(parent, pico.Parent);
			Assert.AreEqual("JScript", pico.GetComponentInstance("hello"));
		}
	}
}