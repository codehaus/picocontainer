using System;
using NUnit.Framework;
using PicoContainer.Defaults;
using PicoContainer;
using NanoContainer.Script.JS;
using System.IO;
using System.Collections;

namespace Test.Script.JS {
  
  [TestFixture]
  public class JSContainerBuilderTestCase : AbstractScriptedContainerBuilderTestCase {

    [Test]
    public void TestContainerCanBeBuiltWithParent() {
      IMutablePicoContainer parent = new DefaultPicoContainer();
      IPicoContainer pico = buildContainer(new JSBuilder(GetStreamReader(@"NanoContainer.Test.TestScripts.test.js")), parent, new ArrayList());
      Assert.IsNotNull(pico);
      Assert.AreSame(parent, pico.Parent);
      Assert.AreEqual("JScript", pico.GetComponentInstance("hello"));
    }
  }
}
