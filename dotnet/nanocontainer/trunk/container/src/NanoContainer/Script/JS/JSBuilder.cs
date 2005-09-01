using System.CodeDom.Compiler;
using System.IO;
using Microsoft.JScript;

namespace NanoContainer.Script.JS
{
	public class JSBuilder : ScriptedContainerBuilder
	{
		public JSBuilder(StreamReader stream) : base(stream)
		{
		}

		protected override CodeDomProvider CodeDomProvider
		{
			get { return new JScriptCodeProvider(); }
		}
	}
}