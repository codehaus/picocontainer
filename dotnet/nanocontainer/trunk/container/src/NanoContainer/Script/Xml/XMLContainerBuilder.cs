using System;
using System.CodeDom;
using System.CodeDom.Compiler;
using System.Collections;
using System.IO;
using System.Reflection;
using System.Xml;
using Microsoft.CSharp;
using NanoContainer.IntegrationKit;
using PicoContainer.Defaults;

namespace NanoContainer.Script.Xml
{
	public class XMLContainerBuilder : AbstractFrameworkContainerBuilder
	{
		private static readonly string CONTAINER = "container";
		private static readonly string ASSEMBLIES = "assemblies";
		private static readonly string COMPONENT = "component";
		private static readonly string COMPONENT_IMPLEMENTATION = "component-implementation";
		private static readonly string COMPONENT_INSTANCE = "component-instance";
		private static readonly string COMPONENT_ADAPTER = "component-adapter";
		private static readonly string REGISTER_COMPONENT_IMPLEMENTATION = "RegisterComponentImplementation";
		
		//private static readonly string REGISTER_COMPONENT_ADAPTER = "RegisterComponentAdapter";
		//private static readonly string COMPONENT_ADAPTER_FACTORY = "component-adapter-factory";
		//private static readonly string COMPONENT_INSTANCE_FACTORY = "component-instance-factory";
		private static readonly string CLASS = "class";
		//private static readonly string FACTORY = "factory";
		private static readonly string FILE = "file";
		private static readonly string KEY = "key";
		private static readonly string PARAMETER = "parameter";
		private static readonly string URL = "url";

		private static readonly string NAMESPACE_IMPORT0 = "PicoContainer";
		private static readonly string NAMESPACE_IMPORT1 = "PicoContainer.Defaults";
		private static readonly string PARENT_PROPERTY = "Parent";
		private static readonly string PARENT_ARGUMENT = "parent";
		private static readonly string PARENT_TYPE = "IPicoContainer";

		

		private XmlElement rootNode;
		private int registerComponentsRecursions;

		private ComposeMethodBuilder composeMethodBuilder = new ComposeMethodBuilder();
		private ContainerStatementBuilder containerStatementBuilder = new ContainerStatementBuilder();

		public delegate void CallBack(CodeMemberMethod composeMethod, 
			CodeVariableReferenceExpression picoContainerVariableRefr, 
			XmlElement element, 
			IList assemblies);

		public XMLContainerBuilder(StreamReader streamReader)
			: base(streamReader)
		{
			registerComponentsRecursions = 0;
		}

		protected CodeVariableReferenceExpression PicoParentVariableReference
		{
			get { return new CodeVariableReferenceExpression("p" + registerComponentsRecursions);}
		}

		protected void ParseStream(TextReader scriptCode)
		{
			XmlDocument doc = new XmlDocument();
			doc.Load(scriptCode);
			rootNode = doc.DocumentElement;
		}
		
		protected void RegisterAssemblies(XmlElement assembliesElement, IList assemblies)
		{
			foreach(XmlElement childElement in assembliesElement.ChildNodes)
			{
				string fileName = childElement.GetAttribute(FILE);
				string urlSpec = childElement.GetAttribute(URL);

				if (urlSpec != string.Empty)
				{
					assemblies.Add(new Uri(urlSpec));
				}
				else
				{
					if (!File.Exists(fileName))
					{
						throw new FileNotFoundException(Environment.CurrentDirectory + Path.DirectorySeparatorChar + fileName + " doesn't exist");
					}

					assemblies.Add(fileName);
				}
			}
		}

		protected void RegisterComponentsAndChildContainers(CodeMemberMethod composeMethod, 
			CodeVariableReferenceExpression picoContainerVariableRefr, 
			XmlElement element, 
			IList assemblies)
		{
			XmlNodeList children = element.ChildNodes;

			// register assemblies first, regardless of order in the document.
			foreach(XmlElement child in children)
			{
				if (ASSEMBLIES.Equals(child.Name))
				{
					RegisterAssemblies(child, assemblies);
				}
			}

			foreach(XmlElement child in children)
			{
				CodeMethodInvokeExpression registerComponent = new CodeMethodInvokeExpression();

				if (CONTAINER.Equals(child.Name))
				{
					RegisterContainer(composeMethod, picoContainerVariableRefr, child, assemblies);
				}
				else if (COMPONENT_IMPLEMENTATION.Equals(child.Name) || COMPONENT.Equals(child.Name))
				{
					RegisterComponentImplementation(child, registerComponent, picoContainerVariableRefr);
					composeMethod.Statements.Add(registerComponent);
				}
				else if (COMPONENT_INSTANCE.Equals(child.Name))
				{
					RegisterComponentInstance(child, registerComponent, picoContainerVariableRefr);
					composeMethod.Statements.Add(registerComponent);
				}
				else if (COMPONENT_ADAPTER.Equals(child.Name))
				{
					RegisterComponentAdapter(child, registerComponent, picoContainerVariableRefr);
					composeMethod.Statements.Add(registerComponent);
				}
				else if (ASSEMBLIES.Equals(child.Name))
				{
					// ignore handled elsewhere
				}
				else
				{
					throw new PicoCompositionException("Unsupported element:" + child.Name);
				}
			}
		}

		private void RegisterContainer(CodeMemberMethod composeMethod, CodeVariableReferenceExpression picoContainerVariableRefr, XmlElement child, IList assemblies)
		{
			registerComponentsRecursions++;
			containerStatementBuilder.Build(composeMethod, picoContainerVariableRefr, PicoParentVariableReference);
			RegisterComponentsAndChildContainers(composeMethod, PicoParentVariableReference, child, assemblies);
		}

		private CodeNamespace BuildCodeNamespace()
		{
			CodeNamespace codeNamespace = new CodeNamespace("GeneratedNamespaceFromXmlScript"); //??Read from XML
			codeNamespace.Imports.Add(new CodeNamespaceImport(NAMESPACE_IMPORT0));
			codeNamespace.Imports.Add(new CodeNamespaceImport(NAMESPACE_IMPORT1));

			return codeNamespace;
		}

		private CodeMemberProperty BuildParentProperty()
		{
			// Define property
			CodeMemberProperty parentProperty = new CodeMemberProperty();
			parentProperty.Name = PARENT_PROPERTY;
			parentProperty.Type = new CodeTypeReference(PARENT_TYPE);
			parentProperty.Attributes = MemberAttributes.Public;

			// build setter expression
			CodeExpression leftSide = new CodeFieldReferenceExpression(new CodeThisReferenceExpression(), PARENT_ARGUMENT);
			CodeExpression rightSide = new CodePropertySetValueReferenceExpression();
			CodeStatement setter = new CodeAssignStatement(leftSide, rightSide);
			parentProperty.SetStatements.Add(setter);

			return parentProperty;
		}

		protected CodeCompileUnit GenerateCodeCompileUnitFromXml(TextReader scriptCode, IList assemblies)
		{
			ParseStream(scriptCode);

			CodeCompileUnit codeCompileUnit = new CodeCompileUnit();
			CodeNamespace codeNamespace = BuildCodeNamespace();

			//TODO ?Read name from XML?
			CodeTypeDeclaration classDeclaration = new CodeTypeDeclaration("GeneratedClassFromXmlScript");
			classDeclaration.Members.Add(new CodeMemberField(PARENT_TYPE, PARENT_ARGUMENT));
			classDeclaration.Members.Add(BuildParentProperty());

			// Build the "Compose" Method
			CallBack callBack = new CallBack(RegisterComponentsAndChildContainers);
			CodeMemberMethod composeMethod = composeMethodBuilder.Build(rootNode, assemblies, callBack);
			classDeclaration.Members.Add(composeMethod);

			codeNamespace.Types.Add(classDeclaration);
			codeCompileUnit.Namespaces.Add(codeNamespace);

			//Uncomment to see generated code
				ICodeGenerator cg =  CodeDomProvider.CreateGenerator();
				StreamWriter sm = new StreamWriter("generated.cs");
				CodeGeneratorOptions genOptions = new CodeGeneratorOptions();
				genOptions.BlankLinesBetweenMembers = true;
				genOptions.BracingStyle = "C";
				genOptions.ElseOnClosing = true;
				genOptions.IndentString = "    ";
				cg.GenerateCodeFromCompileUnit(codeCompileUnit, sm, genOptions );
				sm.Close();

			return codeCompileUnit;
		}

		protected void RegisterComponentImplementation(XmlElement element, CodeMethodInvokeExpression method, CodeVariableReferenceExpression objectRef)
		{
			string typeName = element.GetAttribute(CLASS);
			if (typeName == string.Empty)
			{
				throw new PicoCompositionException("'" + CLASS + "' attribute not specified for " + element.Name);
			}

			CodeTypeOfExpression typeOfExpression = new CodeTypeOfExpression(new CodeTypeReference(typeName));
			string key = element.GetAttribute(KEY);
			XmlNodeList children = element.SelectNodes("child::parameter");

			if (key == string.Empty)
			{
				RegisterComponentImplementationWithoutKey(children, objectRef, method, typeOfExpression);
			}
			else
			{
				RegisterComponentImplementationWithKey(children, objectRef, method, key, typeOfExpression);
			}
		}

		private void RegisterComponentImplementationWithKey(XmlNodeList children, CodeVariableReferenceExpression objectRef, CodeMethodInvokeExpression method, string key, CodeTypeOfExpression typeOfExpression)
		{
			method.Method = new CodeMethodReferenceExpression(objectRef, REGISTER_COMPONENT_IMPLEMENTATION);
			method.Parameters.Add(new CodePrimitiveExpression(key));

			if (children.Count == 0)
			{
				method.Parameters.Add(typeOfExpression);
			}
			else
			{
				method.Parameters.Add(typeOfExpression);
				CreateRegistrationParameters(method.Parameters, children);
			}
		}

		private void RegisterComponentImplementationWithoutKey(XmlNodeList children, CodeVariableReferenceExpression objectRef, CodeMethodInvokeExpression method, CodeTypeOfExpression typeOfExpression)
		{
			method.Method = new CodeMethodReferenceExpression(objectRef, REGISTER_COMPONENT_IMPLEMENTATION);

			if (children.Count == 0)
			{
				method.Parameters.Add(typeOfExpression);
			}
			else
			{
				method.Parameters.Add(typeOfExpression);
				method.Parameters.Add(typeOfExpression);
				CreateRegistrationParameters(method.Parameters, children);
			}
		}

		protected void RegisterComponentInstance(XmlElement element, CodeMethodInvokeExpression method, CodeVariableReferenceExpression objectRef)
		{
			string instanceName = element.InnerText;
			string key = element.GetAttribute(KEY);

			if (key.Length == 0)
			{
				method.Method = new CodeMethodReferenceExpression(objectRef, Constants.REGISTER_COMPONENT_INSTANCE);
				method.Parameters.Add(new CodePrimitiveExpression(instanceName));
			}
			else
			{
				method.Method = new CodeMethodReferenceExpression(objectRef, Constants.REGISTER_COMPONENT_INSTANCE);
				method.Parameters.Add(new CodePrimitiveExpression(key));
				method.Parameters.Add(new CodePrimitiveExpression(instanceName));
			}
		}

		protected void RegisterComponentAdapter(XmlElement element, CodeMethodInvokeExpression method, CodeVariableReferenceExpression objectRef)
		{
			throw new NotImplementedException("Need to implement for ComponentAdapter");
		}

		protected void CreateRegistrationParameters(CodeExpressionCollection methodParameters, XmlNodeList scriptParameters)
		{
			CodeExpression[] initializers = new CodeExpression[scriptParameters.Count];

			for (int i = 0; i < scriptParameters.Count; i++)
			{
				XmlElement element = scriptParameters.Item(i) as XmlElement;

				if (PARAMETER.Equals(element.Name))
				{
					string key = element.GetAttribute(KEY);

					if (key != null && key.Length != 0)
					{
						initializers[i] = new CodeObjectCreateExpression(
							typeof (ComponentParameter),
							new CodeExpression[] {new CodePrimitiveExpression(key)});
					}
					else if (element.Value != null && element.Value.Length != 0)
					{
						initializers[i] = new CodeObjectCreateExpression(
							typeof (ConstantParameter),
							new CodeExpression[] {new CodeTypeOfExpression(element.Value)});
					}
					else
					{
						throw new PicoCompositionException("Parameter not set");
					}
				}
			}

			methodParameters.Add(new CodeArrayCreateExpression(
				"IParameter",
				initializers));
		}

		protected override Type GetCompiledType(StreamReader scriptCode, IList assemblies)
		{
			CodeCompileUnit script = GenerateCodeCompileUnitFromXml(scriptCode, assemblies);
			Assembly createdAssembly = FrameworkCompiler.Compile(CodeDomProvider, script, assemblies);

			foreach (Type type in createdAssembly.GetTypes())
			{
				if (HasValidConstructorAndComposeMethod(type))
				{
					return type;
				}
			}
			throw new PicoCompositionException("The script is not usable. Please specify a correct script.");
		}

		protected override CodeDomProvider CodeDomProvider
		{
			get { return new CSharpCodeProvider(); }
		}
	}
}