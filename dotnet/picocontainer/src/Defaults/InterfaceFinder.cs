/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 * C# port by Maarten Grootendorst                                           *
 *****************************************************************************/
using System;
using System.Collections;

namespace PicoContainer.Defaults {
  public class InterfaceFinder {
    public Type[] GetInterfaces(object obj) {
      ArrayList l = new ArrayList();
      l.Add(obj);
      return GetInterfaces(ArrayList.ReadOnly(l));
    }

    public Type[] GetInterfaces(IList objects) {
      ArrayList interfaces = new ArrayList();
      foreach (object o in objects) {
            
        Type type = o.GetType();
        Type[] implemented = type.GetInterfaces();
        foreach (Type t in implemented) {
          if (!interfaces.Contains(t)) {
            interfaces.Add(t);
          }
        }
      }

      Type[] result = new Type[interfaces.Count];
      interfaces.CopyTo(result,0);
      return result;
    }
  }
}
