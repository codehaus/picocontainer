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
using System.Reflection;
using System.Runtime.Remoting.Contexts;	
using System.Runtime.Remoting.Activation;
using System.Runtime.Remoting.Messaging;
using System.Runtime.Remoting.Proxies;
using System.Runtime.Remoting;
using System.Runtime.Remoting.Services;

namespace PicoContainer.Defaults {
  public class ImplementationHidingComponentAdapter : DecoratingComponentAdapter {

    private readonly InterfaceFinder interfaceFinder = new InterfaceFinder();
    private bool strict;

    public ImplementationHidingComponentAdapter(IComponentAdapter theDelegate, bool strict) : base(theDelegate){
      this.strict = strict;
    }

    public ImplementationHidingComponentAdapter(IComponentAdapter theDelegate) : base(theDelegate){
    }

    public override object ComponentInstance {

      get {
        Type[] interfaces;
        Type delegateType = this.Delegate.ComponentKey as Type;
        if (delegateType != null && delegateType.IsInterface) {
          interfaces = new Type[] {delegateType};
        } else {
          interfaces = interfaceFinder.GetInterfaces(Delegate.ComponentImplementation);
        }
        if (interfaces.Length == 0) {
          if (strict) {
            throw new PicoIntrospectionException("Can't hide implementation for " + Delegate.ComponentImplementation.Name + ". It doesn't implement any interfaces.");
          } else {
            return Delegate.ComponentInstance;
          }
        }

        return new DelegatingInvocationHandler(this, interfaces).GetTransparentProxy();
      }
    }

    private object DelegatedComponentInstance {
      get {
        return base.ComponentInstance;
      }
    }


    // TODO: 
    private class DelegatingInvocationHandler : RealProxy, IRemotingTypeInfo, ISwappable{ 
      private ImplementationHidingComponentAdapter adapter;
      private object delegatedInstance;
      private ArrayList interfaces;
      public DelegatingInvocationHandler(ImplementationHidingComponentAdapter adapter, Type[] interfaces) : base(typeof(ISwappable)) {
        this.adapter = adapter;
        this.interfaces = new ArrayList(interfaces);
      }
      
      
      // C# implementation problem, there is for as far as I know no System.identityHashCode available for c#
      // to perform the GetHashCode() method.
      public override IMessage Invoke(IMessage msg) {
        IMethodCallMessage call = (IMethodCallMessage)msg;
        IConstructionCallMessage ctor = call as IConstructionCallMessage;
        IMethodMessage m = msg as IMethodMessage;
        if (delegatedInstance == null) {
          delegatedInstance = adapter.DelegatedComponentInstance;
        }

        String t = typeof(ISwappable).FullName;
        if (m.TypeName.IndexOf(typeof(ISwappable).FullName) != -1) {
          return new ReturnMessage(m.MethodBase.Invoke(this,BindingFlags.Public,null,m.Args,null),new object[]{},0,call.LogicalCallContext,call);
        } else {
          return new ReturnMessage(m.MethodBase.Invoke(delegatedInstance,BindingFlags.Public,null,m.Args,null),new object[]{},0,call.LogicalCallContext,call);
        }
      }

      public object PicoHotSwap(object newSubject) {
        object result = delegatedInstance;
        delegatedInstance = newSubject;
        return result;
      }

      string typeName;
      public string TypeName {
        get {
          return typeName;
        }
        set {
          typeName = value;
        }
      }

      public bool CanCastTo(Type testType,object obj) {
        return interfaces.Contains(testType) || testType == typeof(ISwappable);
      }
    }  
  }
}
