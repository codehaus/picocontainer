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
using System.Text;

namespace PicoContainer.Defaults {
  /// <summary>
  /// Instantiates components using Constructor Injection.
  /// <remarks>
  /// Note that this class doesn't cache instances. If you want caching,
  /// use a <see cref="CachingComponentAdapter"/> around this one.
  /// </remarks>
  /// </summary>
  public class ConstructorInjectionComponentAdapter : InstantiatingComponentAdapter {

    private bool instantiating;
    private bool verifying;
    private IList sortedMatchingConstructors;

    /// <summary>
    /// Explicitly specifies parameters, if null uses default parameters.
    /// </summary>
    /// <param name="componentKey"></param>
    /// <param name="componentImplementation"></param>
    /// <param name="parameters"></param>
    public ConstructorInjectionComponentAdapter(object componentKey, Type componentImplementation, IParameter[] parameters) 
      : base(componentKey, componentImplementation, parameters) {
      sortedMatchingConstructors = GetSortedMatchingConstructors();
    }

    public ConstructorInjectionComponentAdapter(object componentKey, Type componentImplementation) 
      : this (componentKey, componentImplementation, null) {
    }

    public override void Verify(){
      try {
        ArrayList adapterDependencies = new ArrayList();
        GetGreediestSatisifableConstructor(adapterDependencies);
        if (verifying) {
          throw new CyclicDependencyException(GetDependencyTypes(adapterDependencies));
        }
        verifying = true;
        foreach (IComponentAdapter adapterDependency in adapterDependencies) {
          adapterDependency.Verify();
        }
      } finally {
        verifying = false;
      }
    }

    protected ConstructorInfo GetGreediestSatisifableConstructor(ArrayList adapterDependencies) {      
      ArrayList conflicts = new ArrayList();
      ArrayList unsatisfiableDependencyTypes = new ArrayList();
        
      // if no parameters were provided, we'll just take the biggest one
      ConstructorInfo greediestConstructor = null;
      foreach (ConstructorInfo currentConstructor in sortedMatchingConstructors) {
        IList dependencies = new ArrayList();

        bool failedDependency = false;
        Type[] parameterTypes = TypeUtils.GetParameterTypes( currentConstructor);
        IParameter[] currentParameters = parameters != null ? parameters : CreateDefaultParameters(parameterTypes);
        

        for (int j = 0; j < currentParameters.Length; j++) {
          IComponentAdapter adapter = currentParameters[j].ResolveAdapter(Container, parameterTypes[j]);
          if (adapter == null) {
            failedDependency = true;
            unsatisfiableDependencyTypes.Add(parameterTypes[j]);
          } else {
            // we can't depend on ourself
            if (adapter.Equals(this)) {
              failedDependency = true;
              unsatisfiableDependencyTypes.Add(new ArrayList(parameterTypes));
            } else if (ComponentKey.Equals(adapter.ComponentKey)) {
              failedDependency = true;
              unsatisfiableDependencyTypes.Add(new ArrayList(parameterTypes));
            } else {
              dependencies.Add(adapter);
            }
          }
        } 
        if (!failedDependency) {
          if(conflicts.Count == 0 && greediestConstructor == null) {
            greediestConstructor = currentConstructor;
            adapterDependencies.AddRange(dependencies);
          } else if (conflicts.Count == 0 && Utils.TypeUtils.GetParameterTypes(greediestConstructor).Length > parameterTypes.Length) {
            // remember: we're sorted by length, therefore we've already found the optimal constructor
            break;
          } else {
            if (greediestConstructor != null) {
              conflicts.Add(greediestConstructor);
              greediestConstructor = null;
            }
            conflicts.Add(currentConstructor);
            adapterDependencies.Clear();
          }
        }
      }
      if (conflicts.Count != 0) {
        throw new TooManySatisfiableConstructorsException(ComponentImplementation, conflicts);
      }
      if (greediestConstructor == null && unsatisfiableDependencyTypes.Count > 0) {
        throw new UnsatisfiableDependenciesException(this, unsatisfiableDependencyTypes);
      }
      if (greediestConstructor == null) {
        // be nice to the user, show all constructors that were filtered out 
        ArrayList nonMatching = new ArrayList();
        ConstructorInfo[] constructors = ComponentImplementation.GetConstructors();
        for (int i = 0; i < constructors.Length; i++) {
          if (!sortedMatchingConstructors.Contains(constructors[i])) {
            nonMatching.Add(constructors[i]);
          }
        }
        StringBuilder sb = new StringBuilder();
        foreach (ConstructorInfo ci in nonMatching)
        {
          sb.Append(TypeUtils.ConstructorAsString(ci));  
        }
        throw new PicoInitializationException("The specified parameters do not match any of the following constructors: " + sb.ToString());
      }
      return greediestConstructor;
    }
    
    protected override object InstantiateComponent(ArrayList adapterDependencies) {
      try {
        ConstructorInfo constructor = GetGreediestSatisifableConstructor(adapterDependencies);
        if (instantiating) {
          throw new CyclicDependencyException(Utils.TypeUtils.GetParameterTypes(constructor));
        }
        instantiating = true;
        object[] parameters = GetConstructorArguments(adapterDependencies);

        return constructor.Invoke(parameters);
      } catch (PicoException) {
        throw;
      }
    catch (Exception e) {
    throw new PicoInvocationTargetInitializationException(e);
  } finally {
  instantiating = false;
}
    }

    private Type[] GetDependencyTypes(IList adapterDependencies) {
      Type[] result = new Type[adapterDependencies.Count];
      for (int i = 0; i < adapterDependencies.Count; i++) {
        IComponentAdapter adapterDependency = (IComponentAdapter) adapterDependencies[i];
        result[i] = adapterDependency.ComponentImplementation;
      }
      return result;
    }



    protected object[] GetConstructorArguments(IList adapterDependencies) {
      object[] result = new object[adapterDependencies.Count];
      int i = 0;
      foreach (IComponentAdapter adapterDependency in adapterDependencies) {
        result[i++] = adapterDependency.ComponentInstance;
      }
      return result;
    }

      private IList GetSortedMatchingConstructors() {
        ArrayList matchingConstructors = new ArrayList();
        ConstructorInfo[] allConstructors = ComponentImplementation.GetConstructors();
        // filter out all constructors that will definately not match 
        for (int i = 0; i < allConstructors.Length; i++) {
            ConstructorInfo constructor = allConstructors[i];
          Type []parameterTypes =Utils.TypeUtils.GetParameterTypes(constructor);
            if (parameters == null || parameterTypes.Length == parameters.Length) {
                matchingConstructors.Add(constructor);
            }
        }
        // optimize list of constructors moving the longest at the beginning
        if (parameters == null) {
            matchingConstructors.Sort(new ContructorComparator ());
        }
        return matchingConstructors;
    }

    class ContructorComparator : IComparer
    {
      public int Compare(Object arg0, Object arg1) {
        Type []parameterTypes0 =Utils.TypeUtils.GetParameterTypes((ConstructorInfo)arg0);
        Type []parameterTypes1 =Utils.TypeUtils.GetParameterTypes((ConstructorInfo)arg1);
        
        return parameterTypes1.Length - parameterTypes0.Length;
      }
    }
  }
}
