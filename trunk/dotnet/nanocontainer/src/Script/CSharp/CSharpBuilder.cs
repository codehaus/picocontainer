using System;
using PicoContainer;
using System.IO;
using System.Text;
          
namespace NanoContainer.Script.CSharp
{
  /// <summary>
  /// Summary description for CSharpBuilder.
  /// </summary>
  public class CSharpBuilder : AbstractFrameworkContainerBuilder
  {
    public CSharpBuilder(StreamReader stream): base(stream)
    {
    }
    protected override System.CodeDom.Compiler.CodeDomProvider CodeDomProvider
    {
      get 
      {

        return new Microsoft.CSharp.CSharpCodeProvider();   
      }
    }

  }
}
