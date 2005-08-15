using System.IO;
using System.Reflection;
using NanoContainer.Tests;

namespace Test.Script
{
	public abstract class AbstractScriptedContainerBuilderTestCase : AbstractContainerBuilderTestCase
	{
		public StreamReader GetStreamReader(string scriptName)
		{
			Stream strm = Assembly.GetExecutingAssembly().GetManifestResourceStream(scriptName);
			return new StreamReader(strm);
		}
	}
}