using System.CodeDom.Compiler;
using System.IO;
using Boo.Lang.CodeDom;

namespace NanoContainer.Script.Boo
{
	/// <summary>
	/// Builds a container using the Boo scripting language.
	/// </summary>
	public class BooContainerBuilder : ScriptedContainerBuilder
	{
		/// <summary>
		/// Initializes an object of this class.
		/// </summary>
		/// <param name="stream">Contains the Boo script to build the container.</param>
		public BooContainerBuilder(StreamReader stream) : base(stream)
		{
			// intentionally left blank
		}

		/// <summary>
		/// Gets the Boo code provider.
		/// </summary>
		protected override CodeDomProvider CodeDomProvider
		{
			get { return new BooCodeProvider(); }
		}
	}
}