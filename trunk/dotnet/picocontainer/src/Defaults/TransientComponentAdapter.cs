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



namespace PicoContainer.Defaults {
  public class TransientComponentAdapter : AbstractComponentAdapter {


    private Parameter[] parameters;
    private ArrayList satisfiableConstructors;
    private ConstructorInfo greediestConstructor;
    private bool instantiating;

    public TransientComponentAdapter(Object componentKey,
      Type componentImplementation,
      Parameter[] parameters) : base(componentKey, componentImplementation) {
      this.parameters = parameters;
    }

    public TransientComponentAdapter(Object componentKey,
      Type componentImplementation) :base (componentKey, componentImplementation) {
      this.parameters = null;
    }

    public Type[] GetParameterTypes(ConstructorInfo ci) {
      ParameterInfo[] pis = ci.GetParameters();
      Type[] t=new Type[pis.Length];
      int x = 0;
      foreach (System.Reflection.ParameterInfo pi in pis) {
        t[x++] = pi.ParameterType;
      }
    
      return t;
    }
    public Type[] GetDependencies(MutablePicoContainer picoContainer) {
      ConstructorInfo constructor = GetConstructor(picoContainer);

      return this.GetParameterTypes(constructor);
    }

    private ConstructorInfo GetConstructor(MutablePicoContainer picoContainer) {
      if (greediestConstructor == null) {
          
        ArrayList allConstructors = new ArrayList(ComponentImplementation.GetConstructors());
        ArrayList satisfiableConstructors = GetSatisfiableConstructors(allConstructors, picoContainer);

        greediestConstructor = null;
          
        ArrayList conflicts = new ArrayList();
        for (int i = 0; i < satisfiableConstructors.Count; i++) {
          ConstructorInfo currentConstructor = (ConstructorInfo) satisfiableConstructors[i];
          if (greediestConstructor == null) {
            greediestConstructor = currentConstructor;
          } 
          else if (this.GetParameterTypes(greediestConstructor).Length < this.GetParameterTypes(currentConstructor).Length) {
            conflicts.Clear();
            greediestConstructor = currentConstructor;
          } 
          else if (this.GetParameterTypes(greediestConstructor).Length == this.GetParameterTypes(currentConstructor).Length) {
            conflicts.Add(greediestConstructor);
            conflicts.Add(currentConstructor);
          }
        }
        if (conflicts.Count != 0) {
          throw new TooManySatisfiableConstructorsException(ComponentImplementation, conflicts);
        }
      }
      return greediestConstructor;
    }

    private ArrayList GetSatisfiableConstructors(ArrayList constructors, MutablePicoContainer picoContainer) {
      if (satisfiableConstructors == null) {
        satisfiableConstructors = new ArrayList();
        ArrayList failedDependencies = new ArrayList();
        foreach (ConstructorInfo constructor in constructors) {

          Type[] parameterTypes = GetParameterTypes( constructor);
          Parameter[] currentParameters = parameters != null ? parameters : CreateDefaultParameters(parameterTypes);

          bool failedDependency = false;
          for (int i = 0; i < currentParameters.Length; i++) {
            ComponentAdapter adapter = currentParameters[i].ResolveAdapter(picoContainer);
            if (adapter == null) {
              failedDependency = true;
              failedDependencies.Add(parameterTypes[i]);
              break;
            } 
            else {
              if (adapter.Equals(this)) {
                failedDependency = true;
                failedDependencies.Add(parameterTypes[i]);
                break;
              }
              if (ComponentKey.Equals(adapter.ComponentKey)) {
                failedDependency = true;
                failedDependencies.Add(parameterTypes[i]);
                break;
              }
            }
          }
          if (!failedDependency) {
            satisfiableConstructors.Add(constructor);
          }
        }
        if (satisfiableConstructors.Count == 0) {
          throw new NoSatisfiableConstructorsException(ComponentImplementation, failedDependencies);
        }
      }

      return satisfiableConstructors;
    }

    public override  object GetComponentInstance(MutablePicoContainer picoContainer) {
      Type[] dependencyTypes = GetDependencies(picoContainer);
      ComponentAdapter[] adapterDependencies = new ComponentAdapter[dependencyTypes.Length];

      Parameter[] componentParameters = GetParameters(picoContainer);

      if (componentParameters.Length != adapterDependencies.Length) {
        throw new PicoInitializationException("The number of specified parameters (" +
          componentParameters.Length + ") doesn't match the number of arguments in the greediest satisfiable constructor (" +
          adapterDependencies.Length + "). When parameters are explicitly specified, specify them in the correct order, and one for each constructor argument." +
          "The greediest satisfiable constructor takes the following arguments: " + dependencyTypes.ToString());
                      
      }
      for (int i = 0; i < adapterDependencies.Length; i++) {
        adapterDependencies[i] = componentParameters[i].ResolveAdapter(picoContainer);
      }
      return InstantiateComponent(adapterDependencies, picoContainer);
    }

    private Object InstantiateComponent(ComponentAdapter[] adapterDependencies, MutablePicoContainer picoContainer) {
      try {
        ConstructorInfo constructor = GetConstructor(picoContainer);
        if (instantiating) {
          throw new CyclicDependencyException(constructor);
        }
        instantiating = true;
        Object[] parameters = new Object[adapterDependencies.Length];
        for (int i = 0; i < adapterDependencies.Length; i++) {
          ComponentAdapter adapterDependency = adapterDependencies[i];
          parameters[i] = adapterDependency.GetComponentInstance(picoContainer);
        }

        return constructor.Invoke(parameters);        
      } catch (CyclicDependencyException e) {
        throw e;
      } catch (Exception e) {
        throw new PicoInvocationTargetInitializationException(e);
      } 
      finally {
        instantiating = false;
      }
  
    }

    public static bool IsAssignableFrom(Type actual, Type requested) {
      return actual.IsAssignableFrom(requested);
    }

    private Parameter[] GetParameters(MutablePicoContainer componentRegistry) {
      if (parameters == null) {
        return CreateDefaultParameters(GetDependencies(componentRegistry));
      } 
      else {
        return parameters;
      }
    }

    public override int GetHashCode() {
      return base.GetHashCode();
    }

    public override bool Equals(Object obj) {
      if (!(obj is ComponentAdapter)) {
        return false;
      }
      ComponentAdapter other = (ComponentAdapter) obj;

      return ComponentKey.Equals(other.ComponentKey) &&
        ComponentImplementation.Equals(other.ComponentImplementation);
    }

    private Parameter[] CreateDefaultParameters(Type[] parameters) {
      ComponentParameter[] componentParameters = new ComponentParameter[parameters.Length];
      for (int i = 0; i < parameters.Length; i++) {
        componentParameters[i] = new ComponentParameter(parameters[i]);
      }
      return componentParameters;
    }
  }
}
