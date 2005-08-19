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

		protected CompilerParameters GetCompilerParameters(IList assemblies)
		{
			CompilerParameters compilerParameters = new CompilerParameters();
			AddAssembliesFromWorkingDirectory(compilerParameters);

			foreach (string assembly in assemblies)
			{
				// TODO mward ....
				// This should be replaced with a better solution than copying files into current working directoy
				// looking into creating an additional AppDomain so assemblies can be added and removed whic is
				// NOT possible with the current approach.
				int dirpos = assembly.LastIndexOf(Path.DirectorySeparatorChar);

				if ( dirpos > 0 )
				{
					string dest = string.Format("{0}{1}{2}", Directory.GetCurrentDirectory(), Path.DirectorySeparatorChar, assembly.Substring(dirpos + 1, assembly.Length - dirpos - 1));
					File.Copy(assembly, dest, true);
					
					compilerParameters.ReferencedAssemblies.Add(assembly.Substring(dirpos + 1, assembly.Length - dirpos - 1));
				}
				else
				{
					compilerParameters.ReferencedAssemblies.Add(assembly);
				}
			}

			compilerParameters.GenerateInMemory = true;
			compilerParameters.GenerateExecutable = false;

			return compilerParameters;
		}

		protected void AddAssembliesFromWorkingDirectory(CompilerParameters compilerParameters)
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