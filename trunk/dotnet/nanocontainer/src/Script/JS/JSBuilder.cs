using System;
using System.IO;

namespace NanoContainer.Script.JS
{
	/// <summary>
	/// Summary description for JSBuilder.
	/// </summary>
    public class JSBuilder : AbstractFrameworkContainerBuilder
    {
      public JSBuilder(StreamReader stream): base(stream)
      {
      }
      protected override System.CodeDom.Compiler.CodeDomProvider CodeDomProvider
      {
        get 
        {
          return new Microsoft.JScript.JScriptCodeProvider();
        }
      }

    }
  }
