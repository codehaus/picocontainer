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
using System.Text;
using System.Runtime.Serialization;

using PicoContainer;

namespace PicoContainer.Defaults {
  [Serializable]
  public class AmbiguousComponentResolutionException : PicoIntrospectionException {
    private Type ambiguousClass;
    private object[] ambiguousComponentKeys;

    public AmbiguousComponentResolutionException(Type ambiguousClass, object[] componentKeys) {
      this.ambiguousClass = ambiguousClass;
      this.ambiguousComponentKeys = new Type[componentKeys.Length];
      for (int i = 0; i < componentKeys.Length; i++) {
        ambiguousComponentKeys[i] = componentKeys[i];
      }
    }

    public AmbiguousComponentResolutionException(){ }

    public AmbiguousComponentResolutionException(Exception ex) : base (ex) {}
    public AmbiguousComponentResolutionException(string message) : base(message) { }

    public AmbiguousComponentResolutionException(string message, Exception ex) : base(message,ex) {}

    protected AmbiguousComponentResolutionException(SerializationInfo info, StreamingContext context) : base (info, context) {}

    public override String Message {
      get {
        StringBuilder msg = new StringBuilder("Ambiguous class ");
        msg.Append(ambiguousClass).Append(", ").Append("resolves to multiple keys [");
        foreach (object key in AmbiguousComponentKeys) {
          msg.Append(key).Append(" ");
        }
        msg.Append("resolves to multiple keys ]");
        return msg.ToString();
      }
    }

    public object[] AmbiguousComponentKeys {
      get {
        return ambiguousComponentKeys;
      }
    }
  }
}