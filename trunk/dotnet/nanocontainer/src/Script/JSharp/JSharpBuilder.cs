using System;
using System.IO;

namespace NanoContainer.Script.JSharp
{
	/// <summary>
	/// Summary description for JSBuilder.
	/// </summary>
    public class JSharpBuilder : AbstractFrameworkContainerBuilder
    {
      public JSharpBuilder(StreamReader stream): base(stream)
      {
      }
      protected override System.CodeDom.Compiler.CodeDomProvider CodeDomProvider
      {
        get 
        {
          return new Microsoft.VJSharp.VJSharpCodeProvider();
        }
      }

    }
  }
