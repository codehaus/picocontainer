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

namespace PicoContainer {
  [Serializable]
  public class AssignabilityRegistrationException: PicoRegistrationException {
    private Type type;
    private Type typeToAssign;

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
