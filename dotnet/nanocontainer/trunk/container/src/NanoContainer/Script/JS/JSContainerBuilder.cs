using System.CodeDom.Compiler;
using System.IO;
using Microsoft.JScript;

namespace NanoContainer.Script.JS
{
	public class JSContainerBuilder : ScriptedContainerBuilder
	{
		public JSContainerBuilder(StreamReader stream) : base(stream)
		{
		}

		protected override CodeDomProvider CodeDomProvider
		{
			get { return new JScriptCodeProvider(); }
		}
	}
}