using System.CodeDom.Compiler;
using System.IO;
using Microsoft.CSharp;

namespace NanoContainer.Script.CSharp
{
	public class CSharpBuilder : ScriptedContainerBuilder
	{
		public CSharpBuilder(StreamReader stream) : base(stream)
		{
		}

		protected override CodeDomProvider CodeDomProvider
		{
			get { return new CSharpCodeProvider(); }
		}
	}
}