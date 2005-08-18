using System.CodeDom;
using System.Collections;
using System.Xml;
using PicoContainer;

namespace NanoContainer.Script.Xml
{
	public class ComposeMethodBuilder
	{
		private static readonly string methodName = "Compose";

		/// <summary>
		/// Builds the Compose() method.
		/// </summary>
		public CodeMemberMethod Build(XmlElement rootNode, IList assemblies, XMLContainerBuilder.CallBack callBack)
		{
			CodeMemberMethod composeMethod = new CodeMemberMethod();
			composeMethod.Attributes = MemberAttributes.Public;
			composeMethod.ReturnType = new CodeTypeReference(typeof(IMutablePicoContainer));
			composeMethod.Name = methodName;
			AddInitialAssignmentStatement(composeMethod);

			CodeVariableReferenceExpression picoContainerVariableRefr = new CodeVariableReferenceExpression("p");
			callBack(composeMethod, picoContainerVariableRefr, rootNode, assemblies); // continue reading from DOM and add as statements
			composeMethod.Statements.Add(new CodeMethodReturnStatement(picoContainerVariableRefr));

			return composeMethod;
		}

		/// <summary>
		/// Builds:	"DefaultPicoContainer p = new DefaultPicoContainer(parent);"
		/// </summary>
		protected void AddInitialAssignmentStatement(CodeMemberMethod composeMethod)
		{
			// Right Hand Side of statement
			CodeExpression[] arguments = new CodeExpression[] { new CodeArgumentReferenceExpression("parent") };
			CodeObjectCreateExpression rightHandSide = new CodeObjectCreateExpression(Constants.DefaultPicoContainerType, arguments);

			// build entire statment and add to method
			CodeStatement statement = new CodeVariableDeclarationStatement(Constants.DefaultPicoContainerType, "p", rightHandSide);
			composeMethod.Statements.Add(statement);
		}
	}
}
