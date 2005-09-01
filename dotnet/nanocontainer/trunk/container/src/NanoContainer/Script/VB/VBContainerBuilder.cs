using System.CodeDom.Compiler;
using System.IO;
using Microsoft.VisualBasic;

namespace NanoContainer.Script.VB
{
	public class VBContainerBuilder : ScriptedContainerBuilder
	{
		public VBContainerBuilder(StreamReader stream) : base(stream)
		{
		}

		protected override CodeDomProvider CodeDomProvider
		{
			get { return new VBCodeProvider(); }
		}
	}
}