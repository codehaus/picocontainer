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

using PicoContainer.Utils;


namespace PicoContainer.Defaults {
  /// <summary>
  /// Summary description for ConstructorComponentAdapter.
  /// </summary>
  public class ConstructorInjectionComponentAdapter : InstantiatingComponentAdapter {

    public ConstructorInjectionComponentAdapter(object componentKey, Type componentImplementation, IParameter[] parameters) 
      : base(componentKey, componentImplementation, parameters) {
    }

    public ConstructorInjectionComponentAdapter(object componentKey, Type componentImplementation) 
      : this (componentKey, componentImplementation, null) {
    }

    protected override Type[] GetMostSatisfiableDependencyTypes(IPicoContainer dependencyContainer) {
      ConstructorInfo constructor = GetGreediestSatisifableConstructor(dependencyContainer);

      return TypeUtils.GetParameterTypes(constructor);
    }


    protected override ConstructorInfo GetGreediestSatisifableConstructor(IPicoContainer dependencyContainer) {
      ConstructorInfo[] allConstructors = ComponentImplementation.GetConstructors();
      IList satisfiableConstructors = GetAllSatisfiableConstructors(allConstructors, dependencyContainer);
                                        
      int arity = parameters == null ? -1 : parameters.Length; 
        
      // if no parameters were provided, we'll just take the biggest one
      ConstructorInfo greediestConstructor = null;
      IList conflicts = new ArrayList();
      IList nonMatching = new ArrayList();
      for (int i = 0; i < satisfiableConstructors.Count; i++) {
        ConstructorInfo currentConstructor = (ConstructorInfo) satisfiableConstructors[i];
        Type[] parameterTypes = TypeUtils.GetParameterTypes( currentConstructor);
        if (arity >= 0) {
          if (arity == parameterTypes.Length) {
            int j;
            for (j = 0; j < arity; j++) {
              IComponentAdapter adapter = parameters[j].ResolveAdapter(dependencyContainer, parameterTypes[j]);
              if (adapter == null) {
                nonMatching.Add(currentConstructor);
                break;
              }
            }
            if (j == arity) {
              if (greediestConstructor == null) {
                greediestConstructor = currentConstructor;
              } else {
                conflicts.Add(greediestConstructor);
                conflicts.Add(currentConstructor);
              }
            }
          }
        } else if (greediestConstructor == null) {
          greediestConstructor = currentConstructor;
        } else if (TypeUtils.GetParameterTypes( greediestConstructor).Length < parameterTypes.Length) {
          conflicts.Clear();
          greediestConstructor = currentConstructor;
        } else if (TypeUtils.GetParameterTypes( greediestConstructor).Length == parameterTypes.Length) {
          conflicts.Add(greediestConstructor);
          conflicts.Add(currentConstructor);
        }
      }
      if (conflicts.Count > 0) {
        throw new TooManySatisfiableConstructorsException(ComponentImplementation, conflicts);
      }
      if (greediestConstructor == null && nonMatching.Count > 0) {
        throw new PicoInitializationException("The specified parameters do not match any of the following constructors: " + nonMatching.ToString());
      }
      return greediestConstructor;
    }
  
    private IList GetAllSatisfiableConstructors(IList constructors, IPicoContainer picoContainer) {
      
      IList satisfiableConstructors = new ArrayList();
      IList unsatisfiableDependencyTypes = new ArrayList();
      foreach (ConstructorInfo constructor in constructors) {

        Type[] parameterTypes = TypeUtils.GetParameterTypes( constructor);
        IParameter[] currentParameters = parameters != null ? parameters : CreateDefaultParameters(parameterTypes,picoContainer);

        bool failedDependency = false;
        IComponentAdapter adapter = null;
        for (int i = 0; i < currentParameters.Length; i++) {
          adapter = currentParameters[i].ResolveAdapter(picoContainer,parameterTypes[i]);
          if (adapter == null) {
            failedDependency = true;
            unsatisfiableDependencyTypes.Add(parameterTypes[i]);
          } 
          else {
            if (adapter.Equals(this)) {
              failedDependency = true;
              unsatisfiableDependencyTypes.Add(parameterTypes[i]);
            }
            if (ComponentKey.Equals(adapter.ComponentKey)) {
              failedDependency = true;
              unsatisfiableDependencyTypes.Add(parameterTypes[i]);
            }
          }
        }
        if (!failedDependency) {
          satisfiableConstructors.Add(constructor);
        }
      }
      if (satisfiableConstructors.Count == 0) {
        throw new UnsatisfiableDependenciesException(this, unsatisfiableDependencyTypes);
      }


      return satisfiableConstructors;
    }

    protected override object[] GetConstructorArguments(IComponentAdapter[] adapterDependencies) {
      object[] result = new object[adapterDependencies.Length];
      for (int i = 0; i < adapterDependencies.Length; i++) {
        IComponentAdapter adapterDependency = adapterDependencies[i];
        result[i] = adapterDependency.ComponentInstance;
      }
      return result;
    }
  }
}
