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

namespace PicoContainer {
  public interface MutablePicoContainer : PicoContainer {

    object RegisterComponentImplementation(object componentKey, Type componentImplementation);

    object RegisterComponentImplementation(object componentKey, Type componentImplementation, Parameter[] parameters);

    object RegisterComponentImplementation(Type componentImplementation);

    object RegisterComponentInstance(object componentInstance);

    object RegisterComponentInstance(object componentKey, object componentInstance);

    object UnRegisterComponent(object componentKey);

    void AddOrderedComponentAdapter(ComponentAdapter componentAdapter);

    void AddChild(MutablePicoContainer child);

    void AddParent(MutablePicoContainer parent);
  }
}
