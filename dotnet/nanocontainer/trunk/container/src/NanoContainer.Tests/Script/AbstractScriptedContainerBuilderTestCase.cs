using System.IO;
using System.Reflection;

namespace Test.Script
{
	public abstract class AbstractScriptedContainerBuilderTestCase
	{
		public StreamReader GetStreamReader(string scriptName)
		{
			Stream stream = Assembly.GetExecutingAssembly().GetManifestResourceStream(scriptName);
			return new StreamReader(stream);
		}
	}
}