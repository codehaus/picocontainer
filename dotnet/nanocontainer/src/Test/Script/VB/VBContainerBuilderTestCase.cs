using System;
using NUnit.Framework;
using PicoContainer.Defaults;
using PicoContainer;
using NanoContainer.Script.VB;
using System.IO;
using System.Collections;

namespace Test.Script.VB {
  
  [TestFixture]
  public class JSContainerBuilderTestCase : AbstractScriptedContainerBuilderTestCase {

    [Test]
    public void TestContainerCanBeBuiltWithParent() {
      IMutablePicoContainer parent = new DefaultPicoContainer();
      IPicoContainer pico = buildContainer(new VBBuilder(GetStreamReader(@"NanoContainer.Test.TestScripts.test.vb")), parent, new ArrayList());
      Assert.IsNotNull(pico);
      Assert.AreSame(parent, pico.Parent);
      Assert.AreEqual("VB", pico.GetComponentInstance("hello"));
    }
  }
}
