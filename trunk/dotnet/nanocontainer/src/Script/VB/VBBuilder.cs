using System;
using PicoContainer;
using System.IO;
using System.Text;
          
namespace NanoContainer.Script.VB {
  /// <summary>
  /// Summary description for CSharpBuilder.
  /// </summary>
  public class VBBuilder : AbstractFrameworkContainerBuilder {
    public VBBuilder(StreamReader stream): base(stream){
    }

    protected override System.CodeDom.Compiler.CodeDomProvider CodeDomProvider {
      get {
        
        return new Microsoft.VisualBasic.VBCodeProvider();
      }
    }

  }
}
