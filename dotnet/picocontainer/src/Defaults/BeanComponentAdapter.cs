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
  /// Instantiates components using empty constructors and Setter Injection
  /// <remarks>
  /// <a href="http://docs.codehaus.org/display/PICO/Setter+Injection">Setter Injection</a>.
  /// For easy setting of primitive properties, also <see cref"BeanPropertyComponentAdapter"/>.
  /// Note that this class doesn't cache instances. If you want caching,
  /// use a <see cref="CachingComponentAdapter"/> around this one.
  /// </remarks>
  /// </summary>
  public class BeanComponentAdapter : DecoratingComponentAdapter {
    private ArrayList setters;

    public BeanComponentAdapter(IComponentAdapter theDelegate) :    base(theDelegate) {
  }


    public override object ComponentInstance {
      get {

        Object result = base.ComponentInstance;
        
        SetDependencies(result);

        return result;
      }
    }

    private void SetDependencies(object componentInstance) {
      IList unsatisfiableDependencyTypes = new ArrayList();
      MethodInfo[] setters = GetSetters();
      for (int i = 0; i < setters.Length; i++) {
        MethodInfo setter = setters[i];
        Type dependencyType = setter.GetParameters()[0].ParameterType;
        object dependency = GetDependencyInstance(dependencyType);
        if(dependency != null) {
          try {
            setter.Invoke(componentInstance, new object[]{dependency});
          } catch (Exception e) {
            throw new PicoIntrospectionException(e);
          }
        } else {
          unsatisfiableDependencyTypes.Add(dependencyType);
        }
      }
      if(unsatisfiableDependencyTypes.Count !=0) {
        throw new UnsatisfiableDependenciesException(this, unsatisfiableDependencyTypes);
      }
    }

    private object GetDependencyInstance(Type type) {
      IComponentAdapter adapterDependency = Container.GetComponentAdapterOfType(type);
      if (adapterDependency != null) {
        return adapterDependency.ComponentInstance;
      } else {
        return null;
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
