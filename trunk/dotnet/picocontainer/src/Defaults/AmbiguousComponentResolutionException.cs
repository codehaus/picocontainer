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

using PicoContainer;
namespace PicoContainer.Defaults
{
  [Serializable]
  public class AmbiguousComponentResolutionException : PicoIntrospectionException 
  {
    private Type ambiguousClass;
    private object[] ambiguousComponentKeys;

    public AmbiguousComponentResolutionException(Type ambiguousClass, object[] componentKeys) 
    {
      this.ambiguousClass = ambiguousClass;
      this.ambiguousComponentKeys = new Type[componentKeys.Length];
      for (int i = 0; i < componentKeys.Length; i++) 
      {
        ambiguousComponentKeys[i] = componentKeys[i];
      }
    }

    public override String Message 
    {
      get 
      {
        String msg = "Ambiguous class ";
        msg += ambiguousClass;
        msg+=", ";
        msg +="resolves to multiple keys [";
        foreach (object key in GetAmbiguousComponentKeys()) {
          msg += key;
          msg += " ";
        }
        msg +="resolves to multiple keys ]";
        return msg;
      }
    }

    public object[] GetAmbiguousComponentKeys()
    {
        return ambiguousComponentKeys;
    }
  }
}