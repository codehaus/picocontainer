/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 * C# port by Maarten Grootendorst                                           *
 *****************************************************************************/

using System;
using System.Collections;

using System.Reflection;

using PicoContainer.Extras;
namespace PicoContainer.Defaults
{
  public class DefaultComponentMulticasterFactory : ComponentMulticasterFactory{
    private static MethodInfo equals;
    private static MethodInfo hashCode;

    static DefaultComponentMulticasterFactory (){
      try {          
        equals = typeof(object).GetMethod("Equals", new Type[]{typeof(object)});
        hashCode = typeof(object).GetMethod("GetHashCode", null);
      } catch (Exception ) {
      }
    }

    private InterfaceFinder interfaceFinder = new InterfaceFinder();

    
    public object CreateComponentMulticaster(ArrayList objectsToAggregateCallFor, bool callInReverseOrder) {
      Type[] interfaces = interfaceFinder.GetInterfaces(objectsToAggregateCallFor);
      ArrayList copy = new ArrayList(objectsToAggregateCallFor);

      if (!callInReverseOrder) {
        copy.Reverse();
      }

      Type t = GetAggregatingInterface(interfaces);
    

      AggregatingProxy proxy = new AggregatingProxy(t,copy);

      return proxy.GetTransparentProxy();
    }

    public static Type CompileType(string code, ArrayList assemblies) {
      

      Microsoft.CSharp.CSharpCodeProvider cp = new Microsoft.CSharp.CSharpCodeProvider();   
      System.CodeDom.Compiler.ICodeCompiler ic = cp.CreateCompiler();   
      System.CodeDom.Compiler.CompilerParameters cpar = new System.CodeDom.Compiler.CompilerParameters();   
      cpar.GenerateInMemory = true;   
      cpar.GenerateExecutable = false;   
      cpar.ReferencedAssemblies.Add("system.dll");   
      foreach (Assembly a in assemblies) {
        cpar.ReferencedAssemblies.Add(a.Location);   
      }

      
      System.CodeDom.Compiler.CompilerResults cr = ic.CompileAssemblyFromSource(cpar,code);    

      foreach (System.CodeDom.Compiler.CompilerError ce in cr.Errors)    
        Console.Out.WriteLine(ce.ErrorText); 
      return cr.CompiledAssembly.GetType("DeltaN.Picocontainer.PAggregate");    

      
    }

    public static ArrayList GetUsedAssemblies(Type [] interfaces) {

      ArrayList l =  new ArrayList();
      foreach( Type t in interfaces) {
        Assembly a = Assembly.GetAssembly(t);
        if (!l.Contains(a)) {
          l.Add(a);
        }
      }
      
      return l;
    }

    public static Type GetAggregatingInterface (Type [] interfaces) {

      int x = interfaces.Length;
      string src = "using System;"+
        "namespace DeltaN.Picocontainer {interface PAggregate :";
      
      foreach (Type t in interfaces) {
        src+=    t.FullName;
        if (--x != 0) {
          src +=",";
        }
      }
      src +=     "{ }}";   
     
      return CompileType(src,GetUsedAssemblies(interfaces));
    }
  }
}
