using System.Collections;
using System.IO;
using System.Reflection;
using NanoContainer.Script;
using PicoContainer.Core;
using PicoContainer.Defaults;

namespace Test.Script
{
	public abstract class AbstractScriptedContainerBuilderTestCase
	{
		protected IPicoContainer BuildContainer(ScriptedContainerBuilder builder, IPicoContainer parentContainer, IList assemblies)
		{
			SimpleReference sr = new SimpleReference();
			SimpleReference pr = new SimpleReference();
			pr.Set(parentContainer);
			builder.BuildContainer(sr, pr, assemblies);
			return (IPicoContainer) sr.Get();
		}

		public StreamReader GetStreamReader(string scriptName)
		{
			Stream strm = Assembly.GetExecutingAssembly().GetManifestResourceStream(scriptName);

			return new StreamReader(strm);
		}
	}
}