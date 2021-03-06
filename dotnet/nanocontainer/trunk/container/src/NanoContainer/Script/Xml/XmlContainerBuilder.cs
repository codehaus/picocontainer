using System;
using System.CodeDom;
using System.CodeDom.Compiler;
using System.Collections;
using System.IO;
using System.Reflection;
using System.Xml;
using Microsoft.CSharp;
using NanoContainer.IntegrationKit;
using PicoContainer;
using PicoContainer.Defaults;

namespace NanoContainer.Script.Xml
{
	/// <summary>
	/// This class is responsible for parsing the xml script to build PicoContainer.  The xml is
	/// parsed and C# code is dynamically created and compiled.
	/// </summary>
	public class XMLContainerBuilder : ScriptedContainerBuilder
	{
		private static readonly string CONTAINER = "container";
		private static readonly string ASSEMBLIES = "assemblies";
		private static readonly string COMPONENT = "component";
		private static readonly string COMPONENT_IMPLEMENTATION = "component-implementation";
		private static readonly string COMPONENT_INSTANCE = "component-instance";
		private static readonly string COMPONENT_ADAPTER = "component-adapter";

		//private static readonly string REGISTER_COMPONENT_ADAPTER = "RegisterComponentAdapter";
		//private static readonly string COMPONENT_ADAPTER_FACTORY = "component-adapter-factory";
		//private static readonly string COMPONENT_INSTANCE_FACTORY = "component-instance-factory";
		private static readonly string TYPE = "type";
		private static readonly string KEY = "key";
		private static readonly string PARAMETER = "parameter";

		private XmlElement rootNode = null;
		private int registerComponentsRecursions = 0;

		private ComposeMethodBuilder composeMethodBuilder = new ComposeMethodBuilder();
		private ParentMemberAndPropertyBuilder parentMemberAndPropertyBuilder = new ParentMemberAndPropertyBuilder();
		private ContainerStatementBuilder containerStatementBuilder = new ContainerStatementBuilder();

		public delegate void CallBack(CodeMemberMethod composeMethod,
		                              CodeVariableReferenceExpression picoContainerVariableRefr,
		                              XmlElement element,
		                              IList assemblies);

		public XMLContainerBuilder(StreamReader streamReader) : base(streamReader)
		{
		}

		protected CodeVariableReferenceExpression PicoParentVariableReference
		{
			get { return new CodeVariableReferenceExpression("p" + registerComponentsRecursions); }
		}

		protected void ParseStream(TextReader scriptCode)
		{
			XmlDocument doc = new XmlDocument();
			doc.Load(scriptCode);
			rootNode = doc.DocumentElement;
		}

		protected void RegisterAssemblies(XmlElement assembliesElement, IList assemblies)
		{
			foreach (XmlElement childElement in assembliesElement.ChildNodes)
			{
				string fileName = childElement.GetAttribute("file");
				string urlSpec = childElement.GetAttribute("url");

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
			foreach (XmlElement child in children)
			{
				if (ASSEMBLIES.Equals(child.Name))
				{
					RegisterAssemblies(child, assemblies);
				}
			}

			foreach (XmlElement child in children)
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

		private CodeNamespace buildCodeNamespace()
		{
			CodeNamespace codeNamespace = new CodeNamespace( /*"GeneratedNamespaceFromXmlScript"*/); //??Read from XML
			codeNamespace.Imports.Add(new CodeNamespaceImport(typeof (IPicoContainer).Namespace));
			codeNamespace.Imports.Add(new CodeNamespaceImport(typeof (DefaultPicoContainer).Namespace));

			return codeNamespace;
		}

		protected CodeCompileUnit GenerateCodeCompileUnitFromXml(TextReader scriptCode, IList assemblies)
		{
			ParseStream(scriptCode);
			CodeNamespace codeNamespace = buildCodeNamespace();
			CodeCompileUnit codeCompileUnit = new CodeCompileUnit();
			codeCompileUnit.Namespaces.Add(codeNamespace);

			CodeTypeDeclaration classDeclaration = new CodeTypeDeclaration("GeneratedClassFromXmlScript"); // Read name from XML?
			classDeclaration.BaseTypes.Add(typeof (IScript));
			classDeclaration.Members.Add(parentMemberAndPropertyBuilder.Build());

			// Build the "Compose" Method
			CallBack callBack = new CallBack(RegisterComponentsAndChildContainers);
			CodeMemberMethod composeMethod = composeMethodBuilder.Build(rootNode, assemblies, callBack);
			classDeclaration.Members.Add(composeMethod);

			codeNamespace.Types.Add(classDeclaration);
			return codeCompileUnit;
		}

		protected void RegisterComponentImplementation(XmlElement element, CodeMethodInvokeExpression method, CodeVariableReferenceExpression objectRef)
		{
			string typeName = element.GetAttribute(TYPE);
			if (typeName == string.Empty)
			{
				throw new PicoCompositionException("'" + TYPE + "' attribute not specified for " + element.Name);
			}

			CodeTypeOfExpression typeOfExpression = new CodeTypeOfExpression(new CodeTypeReference(typeName));
			string key = element.GetAttribute(KEY);
			XmlNodeList children = element.SelectNodes("child::parameter");

			method.Method = new CodeMethodReferenceExpression(objectRef, Constants.REGISTER_COMPONENT_IMPLEMENTATION);

			if (key == string.Empty)
			{
				RegisterComponentImplementationWithoutKey(children, method, typeOfExpression);
			}
			else
			{
				RegisterComponentImplementationWithKey(children, method, key, typeOfExpression);
			}
		}

		private void RegisterComponentImplementationWithKey(XmlNodeList children, CodeMethodInvokeExpression method, string key, CodeTypeOfExpression typeOfExpression)
		{
			if(key.StartsWith("typeof("))
			{
				method.Parameters.Add(new CodeSnippetExpression(key));
			}
			else
			{
				method.Parameters.Add(new CodePrimitiveExpression(key));
			}
			

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

		private void RegisterComponentImplementationWithoutKey(XmlNodeList children, CodeMethodInvokeExpression method, CodeTypeOfExpression typeOfExpression)
		{
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

			method.Method = new CodeMethodReferenceExpression(objectRef, Constants.REGISTER_COMPONENT_INSTANCE);

			if (key.Length == 0)
			{
				method.Parameters.Add(new CodePrimitiveExpression(instanceName));
			}
			else
			{
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

					if (key != string.Empty)
					{
						initializers[i] = new CodeObjectCreateExpression(
							typeof (ComponentParameter),
							new CodeExpression[] {new CodePrimitiveExpression(key)});
					}
					else if (element.InnerText != string.Empty)
					{
						initializers[i] = new CodeObjectCreateExpression(
							typeof (ConstantParameter),
							new CodeExpression[] {new CodeSnippetExpression(element.InnerText)});
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

		protected override Assembly GetCompiledAssembly(StreamReader scriptCode, IList assemblies)
		{
			CodeCompileUnit script = GenerateCodeCompileUnitFromXml(scriptCode, assemblies);
			outputGeneratedCodeToFile(script);
			return FrameworkCompiler.Compile(CodeDomProvider, script, assemblies);
		}

		private void outputGeneratedCodeToFile(CodeCompileUnit script)
		{
#if DEBUG
			ICodeGenerator cg = CodeDomProvider.CreateGenerator();
			StreamWriter sm = new StreamWriter("generated.cs");
			CodeGeneratorOptions genOptions = new CodeGeneratorOptions();
			genOptions.BlankLinesBetweenMembers = true;
			genOptions.BracingStyle = "C";
			genOptions.ElseOnClosing = true;
			genOptions.IndentString = "    ";
			cg.GenerateCodeFromCompileUnit(script, sm, genOptions);
			sm.Close();
#endif
		}

		protected override CodeDomProvider CodeDomProvider
		{
			get { return new CSharpCodeProvider(); }
		}
	}
}