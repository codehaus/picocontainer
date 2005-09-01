using System.CodeDom.Compiler;
using System.IO;
using Microsoft.VJSharp;

namespace NanoContainer.Script.JSharp
{
	public class JSharpContainerBuilder : ScriptedContainerBuilder
	{
		public JSharpContainerBuilder(StreamReader stream) : base(stream)
		{
		}

		protected override CodeDomProvider CodeDomProvider
		{
			get { return new VJSharpCodeProvider(); }
		}
	}
}