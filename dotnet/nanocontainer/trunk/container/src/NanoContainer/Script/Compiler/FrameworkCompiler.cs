using System.CodeDom;
using System.CodeDom.Compiler;
using System.Collections;
using System.IO;
using System.Reflection;
using System.Text;
using NanoContainer.IntegrationKit;

namespace NanoContainer.Script.Compiler
{
	public class FrameworkCompiler
	{
		public static Assembly Compile(CodeDomProvider cp, StreamReader reader, IList assemblies)
		{
			return Compile(cp, GetScriptAsString(reader), assemblies);
		}

		// overload to compile CodeDom trees
		public static Assembly Compile(CodeDomProvider cp, object scriptCode, IList assemblies )
		{
			ICodeCompiler codeCompiler = cp.CreateCompiler();
			CompilerParameters compilerParameters = GetCompilerParameters(assemblies);
			CompilerResults compilerResults = null;
			
			if(scriptCode is string) 
			{
				compilerResults = codeCompiler.CompileAssemblyFromSource(compilerParameters, (string)scriptCode);
			}
			else
			{
				compilerResults = codeCompiler.CompileAssemblyFromDom(compilerParameters, (CodeCompileUnit)scriptCode);
			}

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

			return compilerResults.CompiledAssembly;
		}

		private static CompilerParameters GetCompilerParameters(IList assemblies)
		{
			CompilerParameters cpar = new CompilerParameters();
			IList dirs = GetAssemblies();
			int len = dirs.Count;

			// Add all assemblies in the current working directory
			for (int i = 0; i < len; i++)
			{
				cpar.ReferencedAssemblies.Add((string) dirs[i]);
			}

			foreach (string assembly in assemblies)
			{
				int dirpos = assembly.LastIndexOf(Path.DirectorySeparatorChar);

				if ( dirpos > 0 )
				{
					string dest = string.Format("{0}{1}{2}", Directory.GetCurrentDirectory(), Path.DirectorySeparatorChar, assembly.Substring(dirpos + 1, assembly.Length - dirpos - 1));
					File.Copy(assembly, dest, true);
					
					cpar.ReferencedAssemblies.Add(assembly.Substring(dirpos + 1, assembly.Length - dirpos - 1));
				}
				else
				{
					cpar.ReferencedAssemblies.Add(assembly);
				}
			}

			cpar.GenerateInMemory = true;
			cpar.GenerateExecutable = false;

			return cpar;
		}

		private static IList GetAssemblies()
		{
			ArrayList dirs = new ArrayList();
			DirectoryInfo di = new DirectoryInfo(Directory.GetCurrentDirectory());
			FileInfo[] rgFiles = di.GetFiles("*.dll");
			foreach (FileInfo fi in rgFiles)
			{
				dirs.Add(fi.Name);
			}

			return dirs;
		}

		private static string GetScriptAsString(StreamReader stream)
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