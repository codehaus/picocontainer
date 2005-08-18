using System.CodeDom;
using System.CodeDom.Compiler;
using System.Collections;
using System.IO;
using System.Reflection;
using System.Text;
using NanoContainer.IntegrationKit;

namespace NanoContainer.Script
{
	public class FrameworkCompiler
	{
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

		private CompilerParameters GetCompilerParameters(IList assemblies)
		{
			CompilerParameters compilerParameters = new CompilerParameters();
			AddAssembliesFromWorkingDirectory(compilerParameters);

			foreach (string assembly in assemblies)
			{
				compilerParameters.ReferencedAssemblies.Add(assembly);
			}

			compilerParameters.GenerateInMemory = true;
			compilerParameters.GenerateExecutable = false;

			return compilerParameters;
		}

		private void AddAssembliesFromWorkingDirectory(CompilerParameters compilerParameters)
		{
			DirectoryInfo directoryInfo = new DirectoryInfo(Directory.GetCurrentDirectory());

			foreach(FileInfo fileInfo in directoryInfo.GetFiles("*.dll"))
			{
				compilerParameters.ReferencedAssemblies.Add(fileInfo.Name);
			}
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