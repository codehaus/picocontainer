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
		public static Assembly Compile(System.CodeDom.Compiler.CodeDomProvider cp, System.CodeDom.CodeCompileUnit scriptCode, IList assemblies ) 
		{
			ICodeCompiler ic = cp.CreateCompiler();
			CompilerParameters cpar = GetCompilerParameters(assemblies);
			CompilerResults cr = ic.CompileAssemblyFromDom(cpar, scriptCode);   

			bool errors = false;

			if (cr.Errors.Count > 0) 
			{
    
				StringBuilder sb = new StringBuilder("Error compiling the composition script:\n");
				foreach (CompilerError ce in cr.Errors) 
				{
					if (!ce.IsWarning) 
					{
						errors = true;
						sb.Append("\nError number:\t")
							.Append(ce.ErrorNumber)
							.Append("\nMessage:\t ")
							.Append(ce.ErrorText)
							.Append("\nLine number:\t")
							.Append(ce.Line);
					}
				}
				if (errors) 
				{
					throw new PicoCompositionException(sb.ToString());
				}
			}

			return cr.CompiledAssembly;
		}


		public static Assembly Compile(CodeDomProvider cp, string scriptCode, IList assemblies)
		{
			ICodeCompiler ic = cp.CreateCompiler();
			CompilerParameters cpar = GetCompilerParameters(assemblies);
			CompilerResults cr = ic.CompileAssemblyFromSource(cpar, scriptCode);

			bool errors = false;

			if (cr.Errors.Count > 0)
			{
				StringBuilder sb = new StringBuilder("Error compiling the composition script:\n");
				foreach (CompilerError ce in cr.Errors)
				{
					if (!ce.IsWarning)
					{
						errors = true;

						sb.Append("\nError number:\t")
							.Append(ce.ErrorNumber)
							.Append("\nMessage:\t ")
							.Append(ce.ErrorText)
							.Append("\nLine number:\t")
							.Append(ce.Line);
					}
				}
				if (errors)
				{
					throw new PicoCompositionException(sb.ToString());
				}
			}

			return cr.CompiledAssembly;
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
			StringBuilder sb = new StringBuilder("");
			string line;

			while ((line = stream.ReadLine()) != null)
			{
				sb.Append(line).Append("\n");
			}

			return sb.ToString();
		}

	}
}