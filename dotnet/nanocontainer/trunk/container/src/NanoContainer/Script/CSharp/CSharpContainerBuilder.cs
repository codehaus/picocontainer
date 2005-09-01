using System.CodeDom.Compiler;
using System.IO;
using Microsoft.CSharp;

namespace NanoContainer.Script.CSharp
{
	public class CSharpContainerBuilder : ScriptedContainerBuilder
	{
		public CSharpContainerBuilder(StreamReader stream) : base(stream)
		{
		}

		protected override CodeDomProvider CodeDomProvider
		{
			get { return new CSharpCodeProvider(); }
		}
	}
}