using System;
using System.CodeDom;
using System.CodeDom.Compiler;
using System.Collections;
using System.ComponentModel;
using System.IO;
using System.Reflection;
using System.Xml;
using Microsoft.CSharp;
using NanoContainer.IntegrationKit;
using PicoContainer.Defaults;

// TODO this needs a major refactoring
namespace NanoContainer.Script.Xml
{
	public class XMLContainerBuilder : AbstractFrameworkContainerBuilder
	{
		private static readonly string CONTAINER = "container";
		private static readonly string CLASSPATH = "classpath";
		private static readonly string COMPONENT = "component";
		private static readonly string COMPONENT_IMPLEMENTATION = "component-implementation";
		private static readonly string COMPONENT_INSTANCE = "component-instance";
		private static readonly string COMPONENT_ADAPTER = "component-adapter";
		private static readonly string REGISTER_COMPONENT_IMPLEMENTATION = "RegisterComponentImplementation";
		private static readonly string REGISTER_COMPONENT_INSTANCE = "RegisterComponentInstance";
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
		private static readonly string COMPOSE_METHOD = "Compose";
		private static readonly string PARENT_PROPERTY = "Parent";
		private static readonly string PARENT_ARGUMENT = "parent";
		private static readonly string PARENT_TYPE = "IPicoContainer";
		private static readonly string DEFAULT_CONTAINER = "DefaultPicoContainer";
		private static readonly string COMPOSE_METHOD_RETURN_TYPE = "IMutablePicoContainer";

		private XmlElement rootNode;
		private int registerComponentsRecursions;

		public XMLContainerBuilder(StreamReader script)
			: base(script)
		{
			registerComponentsRecursions = 0;
		}

		private void ParseStream(TextReader scriptCode)
		{
			XmlDocument doc = new XmlDocument();

			doc.Load(scriptCode);

			rootNode = doc.DocumentElement;
		}

		private void RegisterClasspath(CodeMemberMethod composeMethod, XmlElement classpathElement, IList assemblies)
		{
			XmlNodeList children = classpathElement.ChildNodes;
			for (int i = 0; i < children.Count ; i++) 
			{
				if (children[i] is XmlElement) 
				{
					XmlElement childElement = (XmlElement) children[i];

					string fileName = childElement.GetAttribute(FILE);
					string urlSpec = childElement.GetAttribute(URL);

					UriBuilder url = null;
					if (urlSpec != null && urlSpec.Length > 0) 
					{
						url = new UriBuilder(urlSpec);
						assemblies.Add(url.ToString());
					} 
					else 
					{
						if (!File.Exists(fileName)) 
						{
							throw new IOException(Environment.CurrentDirectory + Path.DirectorySeparatorChar + fileName + " doesn't exist");
						}

						assemblies.Add(fileName);
					}
				}
			}
		}

		private void RegisterComponentsAndChildContainers(CodeMemberMethod composeMethod, CodeVariableReferenceExpression picoContainerVariableRefr, XmlElement element, IList assemblies)
		{
			XmlNodeList children = element.ChildNodes;

			 // register classpath first, regardless of order in the document.
			for (int i = 0; i < children.Count; i++) 
			{
				if (children[i] is XmlElement) 
				{
					XmlElement childElement = (XmlElement) children[i];
					string name = childElement.Name;
					if (CLASSPATH.Equals(name)) 
					{
						RegisterClasspath(composeMethod, childElement, assemblies);
					}
				}
			}

			for (int i = 0; i < children.Count; i++)
			{
				if (children.Item(i) is XmlElement)
				{
					CodeMethodInvokeExpression registerComponent = new CodeMethodInvokeExpression();

					XmlElement childElement = (XmlElement) children.Item(i);

					string name = childElement.Name;

					if (CONTAINER.Equals(name))
					{
						registerComponentsRecursions++;

						CodeVariableReferenceExpression picoParentContainerVariableRefr = new CodeVariableReferenceExpression("p" + registerComponentsRecursions.ToString());

						composeMethod.Statements.Add(
							new CodeVariableDeclarationStatement(
								DEFAULT_CONTAINER,
								"p" + registerComponentsRecursions.ToString(),
								new CodeObjectCreateExpression(
									DEFAULT_CONTAINER,
									new CodeExpression[] {picoContainerVariableRefr})));

						composeMethod.Statements.Add(
							new CodeMethodInvokeExpression(
								picoContainerVariableRefr,
								REGISTER_COMPONENT_INSTANCE,
								new CodeExpression[] {picoParentContainerVariableRefr}));

						RegisterComponentsAndChildContainers(composeMethod, picoParentContainerVariableRefr, childElement, assemblies);
					}
					else if (CLASSPATH.Equals(name))
					{
						// Already registered ( Actualy not yet but will be :) )
					}
					else if (COMPONENT_IMPLEMENTATION.Equals(name) || COMPONENT.Equals(name))
					{
						RegisterComponentImplementation(childElement, registerComponent, picoContainerVariableRefr);
						composeMethod.Statements.Add(registerComponent);
					}
					else if (COMPONENT_INSTANCE.Equals(name))
					{
						RegisterComponentInstance(childElement, registerComponent, picoContainerVariableRefr);
						composeMethod.Statements.Add(registerComponent);
					}
					else if (COMPONENT_ADAPTER.Equals(name))
					{
						RegisterComponentAdapter(childElement, registerComponent, picoContainerVariableRefr);
						composeMethod.Statements.Add(registerComponent);
					}
					else
					{
						throw new PicoCompositionException("Unsupported element:" + name);
					}
				}
			}
		}

		private CodeCompileUnit GenerateDomTreeFromXML(TextReader scriptCode, IList assemblies)
		{
			CodeCompileUnit domTree;
			CodeNamespace codeNamespace;
			CodeTypeDeclaration classDeclaration;
			CodeMemberProperty parentProperty;
			CodeMemberMethod composeMethod;

			ParseStream(scriptCode);

			//DomTree Header
			domTree = new CodeCompileUnit();
			codeNamespace = new CodeNamespace("GeneratedNamespaceFromXmlScript"); //??Read from XML

			codeNamespace.Imports.Add(new CodeNamespaceImport(NAMESPACE_IMPORT0));
			codeNamespace.Imports.Add(new CodeNamespaceImport(NAMESPACE_IMPORT1));

			//TODO ?Read name from XML?
			classDeclaration = new CodeTypeDeclaration("GeneratedClassFromXmlScript");

			CodeMemberField picocontainer = new CodeMemberField(PARENT_TYPE, PARENT_ARGUMENT);
			classDeclaration.Members.Add(picocontainer);

			parentProperty = new CodeMemberProperty();
			parentProperty.Name = PARENT_PROPERTY;
			parentProperty.Type = new CodeTypeReference(PARENT_TYPE);
			parentProperty.Attributes = MemberAttributes.Public;
			parentProperty.SetStatements.Add(new CodeAssignStatement(new CodeFieldReferenceExpression(new CodeThisReferenceExpression(), PARENT_ARGUMENT), new CodePropertySetValueReferenceExpression()));
			classDeclaration.Members.Add(parentProperty);

			composeMethod = new CodeMemberMethod();
			composeMethod.Name = COMPOSE_METHOD;
			composeMethod.ReturnType = new CodeTypeReference(COMPOSE_METHOD_RETURN_TYPE);

			composeMethod.Statements.Add(
				new CodeVariableDeclarationStatement(
					DEFAULT_CONTAINER,
					"p",
					new CodeObjectCreateExpression(
						DEFAULT_CONTAINER,
						new CodeExpression[]
							{
								new CodeArgumentReferenceExpression(PARENT_ARGUMENT)
							})));

			//Cache this statmment for it will be used offen
			CodeVariableReferenceExpression picoContainerVariableRefr = new CodeVariableReferenceExpression("p");

			//DomTree Body
			RegisterComponentsAndChildContainers(composeMethod, picoContainerVariableRefr, rootNode, assemblies);

			//DomTree Footer
			composeMethod.Statements.Add(new CodeMethodReturnStatement(new CodeVariableReferenceExpression("p")));
			composeMethod.Attributes = MemberAttributes.Public;
			classDeclaration.Members.Add(composeMethod);

			codeNamespace.Types.Add(classDeclaration);
			domTree.Namespaces.Add(codeNamespace);

			//Uncomment to see generated code
			//	ICodeGenerator cg =  CodeDomProvider.CreateGenerator();
			//	StreamWriter sm = new StreamWriter("generated.cs");
			//	CodeGeneratorOptions genOptions = new CodeGeneratorOptions();
			//	genOptions.BlankLinesBetweenMembers = true;
			//	genOptions.BracingStyle = "C";
			//	genOptions.ElseOnClosing = true;
			//	genOptions.IndentString = "    ";
			//	cg.GenerateCodeFromCompileUnit(domTree, sm, genOptions );
			//	sm.Close();

			return domTree;
		}

		private void RegisterComponentImplementation(XmlElement element, CodeMethodInvokeExpression method, CodeVariableReferenceExpression objectRef)
		{
			string className = element.GetAttribute(CLASS);
			if (className.Length == 0)
			{
				throw new PicoCompositionException("'" + CLASS + "' attribute not specified for " + element.Name);
			}

			string key = element.GetAttribute(KEY);

			XmlNodeList children = element.SelectNodes("child::parameter");

			if (key.Length == 0)
			{
				if (children.Count == 0)
				{
					method.Method = new CodeMethodReferenceExpression(objectRef, REGISTER_COMPONENT_IMPLEMENTATION);
					method.Parameters.Add(new CodeTypeOfExpression(new CodeTypeReference(className)));
				}
				else
				{
					method.Method = new CodeMethodReferenceExpression(objectRef, REGISTER_COMPONENT_IMPLEMENTATION);
					method.Parameters.Add(new CodeTypeOfExpression(new CodeTypeReference(className)));
					method.Parameters.Add(new CodeTypeOfExpression(new CodeTypeReference(className)));
					CreateRegistrationParameters(method.Parameters, children);
				}
			}
			else
			{
				if (children.Count == 0)
				{
					method.Method = new CodeMethodReferenceExpression(objectRef, REGISTER_COMPONENT_IMPLEMENTATION);
					method.Parameters.Add(new CodePrimitiveExpression(key));
					method.Parameters.Add(new CodeTypeOfExpression(new CodeTypeReference(className)));
				}
				else
				{
					method.Method = new CodeMethodReferenceExpression(objectRef, REGISTER_COMPONENT_IMPLEMENTATION);
					method.Parameters.Add(new CodePrimitiveExpression(key));
					method.Parameters.Add(new CodeTypeOfExpression(new CodeTypeReference(className)));
					CreateRegistrationParameters(method.Parameters, children);
				}
			}
		}

		private void RegisterComponentInstance(XmlElement element, CodeMethodInvokeExpression method, CodeVariableReferenceExpression objectRef)
		{
			string instanceName = element.InnerText;
			string key = element.GetAttribute(KEY);

			if (key.Length == 0)
			{
				method.Method = new CodeMethodReferenceExpression(objectRef, REGISTER_COMPONENT_INSTANCE);
				method.Parameters.Add(new CodePrimitiveExpression(instanceName));
			}
			else
			{
				method.Method = new CodeMethodReferenceExpression(objectRef, REGISTER_COMPONENT_INSTANCE);
				method.Parameters.Add(new CodePrimitiveExpression(key));
				method.Parameters.Add(new CodePrimitiveExpression(instanceName));
			}
		}

		private void RegisterComponentAdapter(XmlElement element, CodeMethodInvokeExpression method, CodeVariableReferenceExpression objectRef)
		{
			throw new NotImplementedException("Need to implement for ComponentAdapter");
		}

		private void CreateRegistrationParameters(CodeExpressionCollection methodParameters, XmlNodeList scriptParameters)
		{
			CodeExpression[] initializers = new CodeExpression[scriptParameters.Count];

			for (int i = 0; i < scriptParameters.Count; i++)
			{
				if (scriptParameters.Item(i) is XmlElement)
				{
					XmlElement element = (XmlElement) scriptParameters.Item(i);

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
			}

			methodParameters.Add(new CodeArrayCreateExpression(
				"IParameter",
				initializers));
		}

		protected override Type GetCompiledType(StreamReader scriptCode, IList assemblies)
		{
			CodeCompileUnit script = GenerateDomTreeFromXML(scriptCode, assemblies);
			Assembly created = FrameworkCompiler.Compile(CodeDomProvider, script, assemblies);

			Type[] types = created.GetTypes();
			for (int x = 0; x < types.Length; x++)
			{
				if (HasValidConstructorAndComposeMethod(types[x]))
				{
					return types[x];
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