using System;
using NUnit.Framework;
using PicoContainer.Defaults;
using PicoContainer;
using NanoContainer.Script.CSharp;
using System.IO;
using System.Collections;

namespace Test.Script.CSharp
{
  [TestFixture]
  public class CSharpContainerBuilderTestCase : AbstractScriptedContainerBuilderTestCase
	{
    [Test]
    public void TestContainerCanBeBuiltWithParent() {

      IMutablePicoContainer parent = new DefaultPicoContainer();
      IPicoContainer pico = buildContainer(new CSharpBuilder(GetStreamReader(@"NanoContainer.Test.TestScripts.test.cs")), parent, new ArrayList());
      Assert.IsNotNull(pico);
      Assert.AreSame(parent, pico.Parent);
      Assert.AreEqual("C#", pico.GetComponentInstance("hello"));

    }
	}
}
