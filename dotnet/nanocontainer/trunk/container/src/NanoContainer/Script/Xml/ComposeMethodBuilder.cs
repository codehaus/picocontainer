using System.CodeDom;
using System.Collections;
using System.Xml;
using PicoContainer;

namespace NanoContainer.Script.Xml
{
	public class ComposeMethodBuilder
	{
		/// <summary>
		/// Builds the Compose() method.
		/// </summary>
		public CodeMemberMethod Build(XmlElement rootNode, IList assemblies, XMLContainerBuilder.CallBack callBack)
		{
			CodeMemberMethod composeMethod = new CodeMemberMethod();
			composeMethod.Attributes = MemberAttributes.Public;
			composeMethod.ReturnType = new CodeTypeReference(typeof(IMutablePicoContainer));
			composeMethod.Name = "Compose";
			composeMethod.Statements.Add(new CodeSnippetStatement("DefaultPicoContainer p = new DefaultPicoContainer(parent);"));

			CodeVariableReferenceExpression picoContainerVariableRefr = new CodeVariableReferenceExpression("p");
			callBack(composeMethod, picoContainerVariableRefr, rootNode, assemblies); // continue reading from DOM and add as statements
			composeMethod.Statements.Add(new CodeMethodReturnStatement(picoContainerVariableRefr));

			return composeMethod;
		}
	}
}
