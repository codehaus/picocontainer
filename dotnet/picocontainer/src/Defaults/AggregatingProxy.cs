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
using System.Reflection;
using System.Collections;
using System.Runtime.Remoting.Contexts;	
using System.Runtime.Remoting.Activation;
using System.Runtime.Remoting.Messaging;
using System.Runtime.Remoting.Proxies;
using System.Runtime.Remoting;
using System.Runtime.Remoting.Services;

namespace PicoContainer {
  public class AggregatingProxy : RealProxy {

    ArrayList targets;   

    static Hashtable hashedTypes = new Hashtable();

    public AggregatingProxy(Type myType, ArrayList targets) : base(myType) {
      this.targets = targets;
    }

    public override IMessage Invoke(IMessage msg) {
      IMethodCallMessage call = (IMethodCallMessage)msg;
      IConstructionCallMessage ctor = call as IConstructionCallMessage;
      IMethodMessage m = msg as IMethodMessage;
      Type type = (Type)hashedTypes[m.TypeName];
      if (type == null) {
        string t= m.TypeName;
        string typeName = t.Split(',')[0];
        string assemblyName = t.Substring(typeName.Length+2);
        type = Assembly.Load(assemblyName).GetType(typeName);
        lock(hashedTypes) {
          hashedTypes.Add(t,type);
        }
      }
      
      // Probaly an error if this happens....
      if (ctor !=null) {
        throw new NotSupportedException("the proxy cannot create instances");
      } 
        
      object retVal = null;
      foreach (object aaa in targets) {
        if (type.IsAssignableFrom(aaa.GetType())) {
          
          retVal = m.MethodBase.Invoke(aaa,BindingFlags.Public,null,m.Args,null);
        }
      }

      return new ReturnMessage(retVal,new object[]{},0,call.LogicalCallContext,call);
    }
  }
}
