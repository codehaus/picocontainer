using System.CodeDom;

namespace NanoContainer.Script.Xml
{
	public class ParentMemberAndPropertyBuilder
	{
		private static readonly string codeSnippet = @"
	private IPicoContainer parent;

	public IPicoContainer Parent 
	{
		set 
		{
			this.parent = value;
		}
	}
";

		public CodeTypeMember Build()
		{
			return new CodeSnippetTypeMember(codeSnippet);
		}
	}
}
