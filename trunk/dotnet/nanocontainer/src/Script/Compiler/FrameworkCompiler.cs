using System;
using System.Reflection;
using System.CodeDom.Compiler;
using System.Collections;
using NanoContainer.IntegrationKit;
using System.Text;
using System.IO;

namespace NanoContainer.Script.Compiler
{
	/// <summary>
	/// Summary description for FrameworkCompiler.
	/// </summary>
  public class FrameworkCompiler {

    public static Assembly Compile(System.CodeDom.Compiler.CodeDomProvider cp, StreamReader reader, IList assemblies ) {
    
      return Compile(cp,GetScriptAsString(reader),assemblies);
  }
    public static Assembly Compile(System.CodeDom.Compiler.CodeDomProvider cp, string scriptCode, IList assemblies ) {
      ICodeCompiler ic = cp.CreateCompiler();
      CompilerParameters cpar = GetCompilerParameters(assemblies);
      CompilerResults cr = ic.CompileAssemblyFromSource(cpar,scriptCode);   

      bool errors = false;

      if (cr.Errors.Count > 0) {
        
        StringBuilder sb = new StringBuilder("Error compiling the composition script:\n");
        foreach (CompilerError ce in cr.Errors) {
          if (!ce.IsWarning) {
            errors = true;
          
            sb.Append("\nError number:\t").Append(ce.ErrorNumber).Append("\nMessage:\t ").Append(ce.ErrorText).Append("\nLine number:\t").Append(ce.Line);
          }
        }
        if (errors) {
          throw new PicoCompositionException(sb.ToString());
        }
      }

      return cr.CompiledAssembly;
    }

    private static CompilerParameters GetCompilerParameters(IList assemblies) {
      CompilerParameters cpar = new CompilerParameters();   
      
      IList dirs = GetAssemblies();
      int len=dirs.Count;
      // Add all assemblies in the current working directory
      for (int x=0; x<len;x++) {
        cpar.ReferencedAssemblies.Add((string)dirs[x]);
      }
      
      foreach(string assembly in assemblies) {
        cpar.ReferencedAssemblies.Add(assembly.Trim());
      }

      cpar.GenerateInMemory = true;   
      cpar.GenerateExecutable = false;
      
      return cpar;
    }

    private static IList GetAssemblies() {
      ArrayList dirs = new ArrayList();
      DirectoryInfo di = new DirectoryInfo(Directory.GetCurrentDirectory());
      FileInfo[] rgFiles = di.GetFiles("*.dll");
      foreach(FileInfo fi in rgFiles) {
        dirs.Add(fi.Name);       
      }

      return dirs;
    }

    private static string GetScriptAsString(StreamReader stream) {
      StringBuilder sb= new StringBuilder("");
      string line;
      
      while ((line = stream.ReadLine()) != null) {
        sb.Append(line).Append("\n");
      }

      return sb.ToString();
    }

  }
}
