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
using System.Security;
using System.Collections;

namespace PicoContainer.Defaults {
  /// <summary>
  /// Summary description for BeanComponentAdapter.
  /// </summary>
  public class BeanComponentAdapter : InstantiatingComponentAdapter {
    private ArrayList setters;
    public BeanComponentAdapter(object componentKey, Type componentImplementation, IParameter[] parameters)  : base(componentKey, componentImplementation, parameters) {
      
    }

    protected override Type[] GetMostSatisfiableDependencyTypes(IPicoContainer dependencyContainer) {
      MethodInfo[] setters = GetSetters();
      Type[] dependencies = new Type[setters.Length];
      for (int i = 0; i < setters.Length; i++) {
        dependencies[i] = Utils.TypeUtils.GetParameterTypes(setters[i])[0];
      }
      return dependencies;
    }

    protected override object InstantiateComponent(IComponentAdapter[] adapterDependencies, IPicoContainer dependencyContainer) {
      object result = base.InstantiateComponent(adapterDependencies, dependencyContainer);
      SetDependencies(result, adapterDependencies);
      return result;
    }

    protected override ConstructorInfo GetGreediestSatisifableConstructor(IPicoContainer dependencyContainer) {
      try {
        ConstructorInfo ci = ComponentImplementation.GetConstructor(new Type[0]);
        if (ci == null) {
          throw new PicoIntrospectionException("No empty constructor for " + ComponentImplementation.Name);
        }
        return ci;
      }
      catch (SecurityException e) {
        throw new PicoInvocationTargetInitializationException(e);
      }
    }

    protected override object[] GetConstructorArguments(IComponentAdapter[] adapterDependencies) {
      return null;
    }

    private void SetDependencies(object componentInstance, IComponentAdapter[] adapterDependencies) {
      IList unsatisfiableDependencyTypes = new ArrayList();
      MethodInfo[] setters = GetSetters();
      for (int i = 0; i < setters.Length; i++) {
        MethodInfo setter = setters[i];
        IComponentAdapter adapterDependency = adapterDependencies[i];
        if(adapterDependency != null) {
          object dependency = adapterDependency.ComponentInstance;
          try {
            setter.Invoke(componentInstance, new object[]{dependency});
          } catch (Exception e) {
            throw new PicoIntrospectionException(e);
          }
        } else {
          unsatisfiableDependencyTypes.Add(Utils.TypeUtils.GetParameterTypes(setter)[0]);
        }
      }
      if(unsatisfiableDependencyTypes.Count !=0) {
        throw new UnsatisfiableDependenciesException(this, unsatisfiableDependencyTypes);
      }
    }




    private  MethodInfo[] GetSetters() {
      if (setters == null) {
        setters = new ArrayList();
        PropertyInfo[] properties = ComponentImplementation.GetProperties();
        foreach (PropertyInfo property in properties) {
          MethodInfo method = property.GetSetMethod();
          if (method != null) {
            setters.Add(method);
          }
        }
      }
    
      return (MethodInfo[])setters.ToArray(typeof(MethodInfo));
    }

    
  }
}
