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
  public class BeanPropertyComponentAdapterFactory : DecoratingComponentAdapterFactory {

    IDictionary adapterCache = new Hashtable();

    public BeanPropertyComponentAdapterFactory(IComponentAdapterFactory theDelegate) : base(theDelegate) {
      //
      // TODO: Add constructor logic here
      //
    }

    public override IComponentAdapter CreateComponentAdapter(object componentKey, Type componentImplementation, IParameter[] parameters) {
      IComponentAdapter decoratedAdapter = base.CreateComponentAdapter(componentKey, componentImplementation, parameters);
      BeanPropertyComponentAdapter propertyAdapter = new BeanPropertyComponentAdapter(decoratedAdapter);
      adapterCache.Add(componentKey,  propertyAdapter);
      return propertyAdapter;
    }

    public BeanPropertyComponentAdapter getComponentAdapter(object key) {
      return (BeanPropertyComponentAdapter) adapterCache[key];
    }
  }


}
