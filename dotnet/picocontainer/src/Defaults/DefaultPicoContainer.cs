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
using System.Collections;
using PicoContainer;
using PicoContainer.Extras;

namespace PicoContainer.Defaults {

  public class DefaultPicoContainer : MutablePicoContainer {
    private  ArrayList parents = new ArrayList();
    private  ArrayList children = new ArrayList();

    private  ArrayList unmanagedComponents = new ArrayList();

    private  ArrayList instantiantionOrderedComponentAdapters = new ArrayList();

    private  ComponentAdapterFactory componentAdapterFactory;

    private  Hashtable componentKeyToAdapterMap = new Hashtable();

    public DefaultPicoContainer(ComponentAdapterFactory componentAdapterFactory) {
      this.componentAdapterFactory = componentAdapterFactory;
    }

    public DefaultPicoContainer() : this(new DefaultComponentAdapterFactory()){
    }

    public  ICollection ComponentKeys {
      get {
        ArrayList result = new ArrayList();
        result.AddRange(componentKeyToAdapterMap.Keys);
        foreach (MutablePicoContainer delegator in parents) {
          foreach (object o in delegator.ComponentKeys) {
            if (!result.Contains(o)) {
              result.Add(o);
            }
          }
        }
        return result;
      }
    }

    public bool HasComponentAdapter(object adapter) {
      return this.GetComponentAdapters().Contains(adapter);
    }

    public ArrayList GetComponentAdapters() {
      return new ArrayList(componentKeyToAdapterMap.Values);
    }

    public void RegisterComponent(ComponentAdapter componentAdapter) {
      object componentKey = componentAdapter.ComponentKey;
      if (componentKeyToAdapterMap.Contains(componentKey)) {
        throw new DuplicateComponentKeyRegistrationException(componentKey);
      }
      componentKeyToAdapterMap.Add(componentKey, componentAdapter);
    }

    public object UnRegisterComponent(object componentKey) {
      object ret = componentKeyToAdapterMap[componentKey];
      componentKeyToAdapterMap.Remove(componentKey);
      return ret;
    }

    public  ComponentAdapter FindComponentAdapter(object componentKey) {
      ComponentAdapter result = FindComponentAdapterImpl(componentKey);
      if (result != null) {
        return result;
      } else {
        foreach (MutablePicoContainer delegator in parents) {
          ComponentAdapter componentAdapter = delegator.FindComponentAdapter(componentKey);
          if (componentAdapter != null) {
            return componentAdapter;
          }
        }
        return null;
      }
    }

    private ComponentAdapter FindComponentAdapterImpl(object componentKey) {
      ComponentAdapter result = (ComponentAdapter) componentKeyToAdapterMap[componentKey];
      if (result == null && componentKey is Type) {
        Type classKey = (Type) componentKey;
        result = FindImplementingComponentAdapter(classKey);
      }
      return result;
    }

    public object RegisterComponentInstance(object component) {
      return RegisterComponentInstance(component.GetType(), component);
    }

    public object RegisterComponentInstance(object componentKey, object componentInstance)  {
      ComponentAdapter componentAdapter = new InstanceComponentAdapter(componentKey, componentInstance);
      RegisterComponent(componentAdapter);

      AddOrderedComponentAdapter(componentAdapter);
      unmanagedComponents.Add(componentInstance);
      return componentKey;
    }

    public object RegisterComponentImplementation(Type componentImplementation) {
      return RegisterComponentImplementation(componentImplementation, componentImplementation);
    }

    public object GetComponentMulticaster(bool callInInstantiationOrder, bool callUnmanagedComponents) {
      ComponentMulticasterFactory componentMulticasterFactory = new DefaultComponentMulticasterFactory();

      ArrayList componentsToMulticast = ComponentInstances;
      if (!callUnmanagedComponents) {
        foreach (object obj in unmanagedComponents) {
          componentsToMulticast.Remove(obj);
        }
      }
      return componentMulticasterFactory.CreateComponentMulticaster(
        componentsToMulticast,
        callInInstantiationOrder
        );
    }

    public object GetComponentMulticaster()  {
      return GetComponentMulticaster(true, false);
    }

    public object RegisterComponentImplementation(object componentKey, Type componentImplementation) {
      return RegisterComponentImplementation(componentKey, componentImplementation, null);
    }

    public object RegisterComponentImplementation(object componentKey, Type componentImplementation, Parameter[] parameters)  {
      ComponentAdapter componentAdapter = componentAdapterFactory.CreateComponentAdapter(componentKey, componentImplementation, parameters);
      RegisterComponent(componentAdapter);
      return componentKey;
    }

    public void AddOrderedComponentAdapter(ComponentAdapter componentAdapter) {
      instantiantionOrderedComponentAdapters.Add(componentAdapter);
    }

    public ArrayList ComponentInstances  {
      get {
        foreach (object o in ComponentKeys) {
          GetComponentInstance(o);
        }
        ArrayList result = new ArrayList();
        foreach (ComponentAdapter componentAdapter in instantiantionOrderedComponentAdapters) {
          result.Add(componentAdapter.GetComponentInstance(this));
        }
        return result;
      }
    }

    public object GetComponentInstance(object componentKey)  {
      ComponentAdapter componentAdapter = FindComponentAdapter(componentKey);
      if (componentAdapter != null) {
        return componentAdapter.GetComponentInstance(this);
      } else {
        return null;
      }
    }

    public object FindComponentInstance(Type componentType)  {
      ArrayList foundKeys = new ArrayList();
      object result = null;
      foreach (object key in ComponentKeys) {
        object componentInstance = GetComponentInstance(key);
                    
        if (componentType.IsInstanceOfType(componentInstance)) {
          result = componentInstance;
          foundKeys.Add(key);
        }
      }

      if (foundKeys.Count == 0) {
        return null;
      } else if (foundKeys.Count > 1) {
        throw new AmbiguousComponentResolutionException(componentType, foundKeys.ToArray());
      }

      return result;
    }

    public bool HasComponent(object componentKey) {
      return ((ArrayList)ComponentKeys).Contains(componentKey);
    }

    public ComponentAdapter FindImplementingComponentAdapter(Type componentType)  {
      ArrayList found = new ArrayList();
      foreach (ComponentAdapter componentAdapter in GetComponentAdapters()) {
        if (componentType.IsAssignableFrom(componentAdapter.ComponentImplementation)) {
          found.Add(componentAdapter);
        }
      }

      if (found.Count == 1) {
        return ((ComponentAdapter) found[0]);
      } else if (found.Count == 0) {
        return null;
      } else {
        Type[] foundClasses = new Type[found.Count];
        for (int i = 0; i < foundClasses.Length; i++) {
          ComponentAdapter componentAdapter = (ComponentAdapter) found[i];
          foundClasses[i] = componentAdapter.ComponentImplementation;
        }

        throw new AmbiguousComponentResolutionException(componentType, foundClasses);
      }
    }

    public ArrayList ChildContainers {
      get {
        return ArrayList.ReadOnly(children);
      }
    }

    public ArrayList ParentContainers {
      get {
        return ArrayList.ReadOnly(parents);
      }
    }

    public void AddChild(MutablePicoContainer child) {
      if (!children.Contains(child)) {
        children.Add(child);
      }
      if (!child.ParentContainers.Contains(this)) {
        child.AddParent(this);
      }
    }

    public void AddParent(MutablePicoContainer parent) {
      if (!parents.Contains(parent)) {
        parents.Add(parent);
      }
      if (!parent.ChildContainers.Contains(this)) {
        parent.AddChild(this);
      }
    }

    
  }

}
