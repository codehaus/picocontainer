using System;
using System.Reflection;
using System.Collections;

namespace NanoContainer.Reflection {
  /// <summary>
  /// Summary description for TypeLoader.
  /// </summary>
  public class TypeLoader {
    public TypeLoader() {
    }
    private static Hashtable TypeMap = new Hashtable();
    public static Type GetType(string typeSettings) {
      return GetType(new ObjectTypeSettings(typeSettings));
    }

    public static Type GetType(ObjectTypeSettings typeSettings) {
      
      Type t = TypeMap[typeSettings.Name] as Type;
      if (t != null) {
        return t;
      }

      Assembly assemblyInstance	= null;

      try {
        if (typeSettings.Assembly != null) { 
            assemblyInstance = Assembly.Load( typeSettings.Assembly );
        } else
        {
          assemblyInstance = Assembly.GetExecutingAssembly();          
        }
      } 
      catch (System.IO.FileNotFoundException e) {
        // Maybe it is a in memory assembly try it
        foreach (Assembly a in AppDomain.CurrentDomain.GetAssemblies())
        {
          if (a.FullName.Replace(" ","") == typeSettings.Assembly)
          {
            assemblyInstance = a;
            break;
          }
        }

        if (assemblyInstance == null) {
          throw new TypeLoadException("Can not load the assembly "+typeSettings.Assembly+" needed for the type: "+typeSettings.Type,e);
        }
      }

      try {
        t = assemblyInstance.GetType( typeSettings.Type, true, false);
        TypeMap.Add(typeSettings.Name,t);

        return t;
      } 
      catch (Exception ex) {
        Assembly[] assm = AppDomain.CurrentDomain.GetAssemblies();
        foreach (Assembly a in assm) {
          try {
            t = a.GetType(typeSettings.Type, true, false);
            TypeMap.Add(typeSettings.Name,t);
            
            return t;
          }
          catch (Exception ) {
          }          
        }
        throw ex;
      }

    }
  }
}
