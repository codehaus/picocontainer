using System;
using System.Reflection;

namespace PicoContainer.Utils
{
	/// <summary>
	/// Summary description for TypeUtls.
	/// </summary>
	public class TypeUtils
	{

    private TypeUtils() {}

    public static Type[] GetParameterTypes(ParameterInfo[] pis) {
      Type[] types =new Type[pis.Length];
      int x = 0;
      foreach (System.Reflection.ParameterInfo pi in pis) {
        types[x++] = pi.ParameterType;
      }
    
      return types;
    }

    public static Type[] GetParameterTypes(ConstructorInfo ci) {
      return GetParameterTypes(ci.GetParameters());
    }

    public static Type[] GetParameterTypes(MethodInfo ci) {
      return GetParameterTypes(ci.GetParameters());
    }

  }
}
