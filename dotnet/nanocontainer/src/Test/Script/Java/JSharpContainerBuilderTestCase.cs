using System;
using NUnit.Framework;
using PicoContainer.Defaults;
using PicoContainer;
using NanoContainer.Script.JSharp;
using System.IO;
using System.Collections;

namespace Test.Script.Java {
  
  [TestFixture]
  public class JSharpContainerBuilderTestCase : AbstractScriptedContainerBuilderTestCase {

    [Test]
    public void TestContainerCanBeBuiltWithParent() {
      IMutablePicoContainer parent = new DefaultPicoContainer();
      IPicoContainer pico = buildContainer(new JSharpBuilder(GetStreamReader(@"NanoContainer.Test.TestScripts.test.java")), parent, new ArrayList());
      Assert.IsNotNull(pico);
      Assert.AreSame(parent, pico.Parent);
      Assert.AreEqual("J#", pico.GetComponentInstance("hello"));
    }
  }
}
