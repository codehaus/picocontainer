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
using System.Reflection;
using PicoContainer;

namespace PicoContainer.Defaults
{
  [Serializable]
  public class CyclicDependencyException : PicoInitializationException
	{
    private ConstructorInfo constructor;

    public CyclicDependencyException(ConstructorInfo constructor) 
    {
      this.constructor = constructor;
    }

    public ConstructorInfo Constructor
    {
      get {
        return constructor;
      }
    }

    public override String Message 
    {
      get {
        return "Cyclic dependency: " + constructor.Name + "(" + GetCtorParams(constructor) + ")";
      }
    }

    public Type[] GetParameterTypes(ConstructorInfo ci) 
    {
      ParameterInfo[] pis = ci.GetParameters();
      Type[] t=new Type[pis.Length];
      int x = 0;
      foreach (System.Reflection.ParameterInfo pi in pis) 
      {
        t[x++] = pi.ParameterType;
      }
    
      return t;
    }

    private String GetCtorParams(ConstructorInfo constructor) 
    {
      String retval = "";
      Type[] parameterTypes =GetParameterTypes( constructor);
      for (int i = 0; i < parameterTypes.Length; i++) 
      {
        retval = retval + parameterTypes[i].Name;
        if (i+1 < parameterTypes.Length) 
        {
          retval += ",";
        }
      }
      return retval;
    }
  }
}
