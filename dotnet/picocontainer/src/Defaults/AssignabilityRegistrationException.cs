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
using System.Runtime.Serialization;

using PicoContainer;

namespace PicoContainer.Defaults {
  [Serializable]
  public class AssignabilityRegistrationException: PicoRegistrationException {
    private Type type;
    private Type typeToAssign;

    public AssignabilityRegistrationException(){ }

    public AssignabilityRegistrationException(Exception ex) : base (ex) {}
    public AssignabilityRegistrationException(string message) : base(message) { }

    public AssignabilityRegistrationException(string message, Exception ex) : base(message,ex) {}

    protected AssignabilityRegistrationException(SerializationInfo info, StreamingContext context) : base (info, context) {}

    public AssignabilityRegistrationException(Type type, Type typeToAssign) {
      this.type = type;
      this.typeToAssign = typeToAssign;
    }

    public override String Message {
      get {
        return "The type:" + type.Name + "  was not assignable from the class " + typeToAssign.Name;
      }
    }
  }
}
