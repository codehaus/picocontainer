using System.CodeDom;

namespace NanoContainer.Script.Xml
{
	public class ContainerStatementBuilder
	{
		public void Build(CodeMemberMethod composeMethod, 
			CodeVariableReferenceExpression picoContainer, 
			CodeVariableReferenceExpression childContainer)
		{
			CodeStatement childContainerStatement = createChildContainerStatement(picoContainer, childContainer);
			CodeExpression registerChildInstanceExpression = createRegisterChildInstanceExpression(picoContainer, childContainer);
			
			composeMethod.Statements.Add(childContainerStatement);
			composeMethod.Statements.Add(registerChildInstanceExpression);
		}

		private CodeStatement createChildContainerStatement(CodeVariableReferenceExpression picoContainer,
			CodeVariableReferenceExpression childContainer)
		{
			CodeExpression[] arguments = new CodeExpression[] {picoContainer};
			CodeExpression rightHandSide = new CodeObjectCreateExpression(Constants.DefaultPicoContainerType, arguments);

			return new CodeVariableDeclarationStatement(Constants.DefaultPicoContainerType, 
				childContainer.VariableName, 
				rightHandSide);
		}

		private CodeExpression createRegisterChildInstanceExpression(CodeVariableReferenceExpression picoContainer,
			CodeVariableReferenceExpression childContainer)
		{
			CodeExpression[] arguments = new CodeExpression[] {childContainer};
			return new CodeMethodInvokeExpression(picoContainer, Constants.REGISTER_COMPONENT_INSTANCE, arguments);
		}
	}
}
