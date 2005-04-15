using System.CodeDom.Compiler;
using System.IO;
using Microsoft.VisualBasic;

namespace NanoContainer.Script.VB
{
	public class VBBuilder : AbstractFrameworkContainerBuilder
	{
		public VBBuilder(StreamReader stream) : base(stream)
		{
		}

		protected override CodeDomProvider CodeDomProvider
		{
			get { return new VBCodeProvider(); }
		}

	}
}