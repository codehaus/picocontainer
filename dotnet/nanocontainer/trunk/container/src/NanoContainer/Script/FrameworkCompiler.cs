using System;
using System.CodeDom;
using System.CodeDom.Compiler;
using System.Collections;
using System.Collections.Specialized;
using System.IO;
using System.Reflection;
using System.Text;
using NanoContainer.IntegrationKit;

namespace NanoContainer.Script
{
	public class FrameworkCompiler
	{
		private static AssemblyReferenceCache assemblyReferenceCache = new AssemblyReferenceCache();

		static FrameworkCompiler()
		{
			AppDomain.CurrentDomain.AssemblyResolve += new ResolveEventHandler(assemblyReferenceCache.FindAssembly);
		}

		public Assembly Compile(CodeDomProvider cp, StreamReader reader, IList assemblies)
		{
			return Compile(cp, GetScriptAsString(reader), assemblies);
		}

		public Assembly Compile(CodeDomProvider codeDomProvider, object scriptCode, IList assemblies )
		{
			ICodeCompiler codeCompiler = codeDomProvider.CreateCompiler();
			CompilerParameters compilerParameters = GetCompilerParameters(assemblies);
			CompilerResults compilerResults = null;

			if(scriptCode is string) 
			{
				compilerResults = codeCompiler.CompileAssemblyFromSource(compilerParameters, scriptCode as string);
			}
			else
			{
				compilerResults = codeCompiler.CompileAssemblyFromDom(compilerParameters, scriptCode as CodeCompileUnit);
			}

			handleErrors(compilerResults);
			return compilerResults.CompiledAssembly;
		}

		private void handleErrors(CompilerResults compilerResults)
		{
			if (compilerResults.Errors.Count > 0) 
			{
				StringBuilder sb = new StringBuilder("Error compiling the composition script:\n");

				foreach (CompilerError compilerError in compilerResults.Errors) 
				{
					if (!compilerError.IsWarning) 
					{
						sb.Append("\nError number:\t")
							.Append(compilerError.ErrorNumber)
							.Append("\nMessage:\t ")
							.Append(compilerError.ErrorText)
							.Append("\nLine number:\t")
							.Append(compilerError.Line);
					}
				}
				
				if (!sb.Length.Equals(0)) 
				{
					throw new PicoCompositionException(sb.ToString());
				}
			}
		}

		protected CompilerParameters GetCompilerParameters(IList assemblies)
		{
			CompilerParameters compilerParameters = new CompilerParameters();
			StringCollection compilerOptionPaths = new StringCollection();
			AddAssemblies(compilerParameters);

			foreach (string assembly in assemblies)
			{
				string[] directories = assembly.Split(Path.DirectorySeparatorChar);

				if(directories.Length > 1)
				{
					string dllName = directories[directories.Length - 1];
					string dllPath = assembly.Replace(dllName, string.Empty);

					compilerParameters.ReferencedAssemblies.Add(dllName);
					compilerOptionPaths.Add(dllPath);
					assemblyReferenceCache.add(dllName, assembly);
				}
				else
				{
					compilerParameters.ReferencedAssemblies.Add(assembly);
				}
			}

			setCompilerOptions(compilerParameters, compilerOptionPaths);
			compilerParameters.GenerateInMemory = true;
			compilerParameters.GenerateExecutable = false;

			return compilerParameters;
		}

		/// <summary>
		/// appends the directories paths for external assemblies as a compiler option
		/// </summary>
		private void setCompilerOptions(CompilerParameters compilerParameters, IList compilerOptionPaths)
		{
			if(compilerOptionPaths.Count > 0)
			{
				StringBuilder sb = new StringBuilder("/lib:");
				foreach(string path in compilerOptionPaths)
				{
					sb.AppendFormat("{0};", path);
				}

				compilerParameters.CompilerOptions = sb.ToString();
			}
		}

		protected virtual void AddAssemblies(CompilerParameters compilerParameters)
		{
			compilerParameters.ReferencedAssemblies.Add("PicoContainer.dll");
			compilerParameters.ReferencedAssemblies.Add("NanoContainer.dll");
		}

		private string GetScriptAsString(StreamReader stream)
		{
			StringBuilder sb = new StringBuilder();
			string line;

			while ((line = stream.ReadLine()) != null)
			{
				sb.Append(line).Append("\n");
			}

			return sb.ToString();
		}
	}
}