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
  /// Summary description for InstanciatingComponentAdapter.
  /// </summary>
  public abstract class InstantiatingComponentAdapter : AbstractComponentAdapter {
    private bool instantiating;
    private bool verifying;
    internal IParameter[] parameters;

    public InstantiatingComponentAdapter(object componentKey, Type componentImplementation, IParameter[] parameters) : base(componentKey, componentImplementation){
      this.parameters = parameters;
    }

    public override object ComponentInstance {
      get {
        IComponentAdapter[] dependencyAdapters = GetMostSatisifableDependencyAdapters(Container);
        object instance = InstantiateComponent(dependencyAdapters, Container);

        // Now, track the instantiation order
        foreach (IComponentAdapter dependencyAdapter in dependencyAdapters) {
          Container.AddOrderedComponentAdapter(dependencyAdapter);
        }
        return instance;
      }
    }

    private IParameter[] GetParameters(IPicoContainer picoContainer) {
      if (parameters == null || parameters.Length == 0) {
        return CreateDefaultParameters(GetMostSatisfiableDependencyTypes(picoContainer), picoContainer);
      } else {
        return parameters;
      }
    }

    protected static IParameter[] CreateDefaultParameters(Type[] parameters, IPicoContainer picoContainer) {
      IParameter[] componentParameters = new IParameter[parameters.Length];
      for (int i = 0; i < parameters.Length; i++) {
        if(typeof(IPicoContainer).IsAssignableFrom(parameters[i])) {
          componentParameters[i] = new ConstantParameter(picoContainer);
        } else {
          componentParameters[i] = new ComponentParameter();
        }
      }
      return componentParameters;
    }

    private IComponentAdapter[] GetMostSatisifableDependencyAdapters(IPicoContainer dependencyContainer) {
      Type[] mostSatisfiableDependencyTypes = GetMostSatisfiableDependencyTypes(dependencyContainer);
      IComponentAdapter[] mostSatisfiableDependencyAdapters = new IComponentAdapter[mostSatisfiableDependencyTypes.Length];

      IParameter[] componentParameters = GetParameters(dependencyContainer);

      if (componentParameters.Length != mostSatisfiableDependencyAdapters.Length) {
        throw new PicoInitializationException(
          "The number of specified parameters (" +
          componentParameters.Length + ") doesn't match the number of arguments in the greediest satisfiable constructor (" +
          mostSatisfiableDependencyAdapters.Length + "). When parameters are explicitly specified, specify them in the correct order, and one for each constructor argument." +
          "The greediest satisfiable constructor takes the following arguments: " + Utils.StringUtils.ArrayToString(mostSatisfiableDependencyTypes)
          );
      }
      for (int i = 0; i < mostSatisfiableDependencyAdapters.Length; i++) {
        mostSatisfiableDependencyAdapters[i] = componentParameters[i].ResolveAdapter(dependencyContainer,mostSatisfiableDependencyTypes[i]);
      }
      return mostSatisfiableDependencyAdapters;
    }

    protected virtual object InstantiateComponent(IComponentAdapter[] adapterDependencies, IPicoContainer dependencyContainer) {
      try {
        ConstructorInfo constructor = GetGreediestSatisifableConstructor(dependencyContainer);
        if (instantiating) {
          throw new CyclicDependencyException(Utils.TypeUtils.GetParameterTypes(constructor));
        }
        instantiating = true;
        object[] parameters = GetConstructorArguments(adapterDependencies);

        return constructor.Invoke(parameters);
      } 
      catch (PicoException) {
        throw ;
      } catch (Exception ex) {
        throw new PicoInvocationTargetInitializationException(ex);
      } finally {
        instantiating = false;
      }
    }


    public override void Verify() {
      try {
        IComponentAdapter[] adapterDependencies = GetMostSatisifableDependencyAdapters(Container);
        if (verifying) {
          throw new CyclicDependencyException(GetMostSatisfiableDependencyTypes(Container));
        }
        verifying = true;
        for (int i = 0; i < adapterDependencies.Length; i++) {
          IComponentAdapter adapterDependency = adapterDependencies[i];
          adapterDependency.Verify();
        }
      } finally {
        verifying = false;
      }
    }

    protected abstract Type[] GetMostSatisfiableDependencyTypes(IPicoContainer dependencyContainer) ;

    protected abstract ConstructorInfo GetGreediestSatisifableConstructor(IPicoContainer dependencyContainer) ;

    protected abstract object[] GetConstructorArguments(IComponentAdapter[] adapterDependencies);
  }
}