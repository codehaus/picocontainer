using System;
using NUnit.Framework;
using NanoContainer.Script;
using PicoContainer.Defaults;
using PicoContainer;
using System.Collections;
using System.IO;

namespace Test.Script
{
  public class AbstractScriptedContainerBuilderTestCase
	{
    protected IPicoContainer buildContainer(ScriptedContainerBuilder builder, IPicoContainer parentContainer, IList assemblies) {
      SimpleReference sr = new SimpleReference();
      SimpleReference pr = new SimpleReference();
      pr.Set(parentContainer);
      builder.BuildContainer(sr,pr, assemblies);
      return (IPicoContainer)sr.Get();
    }

    public StreamReader GetStreamReader (string scriptName) {
      System.IO.Stream strm = null;

      strm = System.Reflection.Assembly.GetExecutingAssembly().GetManifestResourceStream(scriptName);

      return new StreamReader(strm);
    }
  }
}
