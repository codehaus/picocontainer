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
using System.Threading;
using System.Collections;
using PicoContainer;
using PicoContainer.Extras;

namespace PicoContainer.Defaults {

  public class DefaultPicoContainer : MutablePicoContainer {
    private  ArrayList parentsWeakReferences = new ArrayList();
    
    private int lockTimeOut = 0;
    private ReaderWriterLock rwLock = new ReaderWriterLock();

    private  IList children = new ArrayList();

    private  IList unmanagedComponents = new ArrayList();

    private  IList instantiantionOrderedComponentAdapters = new ArrayList();

    private  ComponentAdapterFactory componentAdapterFactory;

    private  Hashtable componentKeyToAdapterMap = new Hashtable();

    public DefaultPicoContainer(ComponentAdapterFactory componentAdapterFactory) {
      this.componentAdapterFactory = componentAdapterFactory;
    }

    public DefaultPicoContainer() : this(new DefaultComponentAdapterFactory()){
    }

    public  IList ComponentKeys {
      get {
        ArrayList result = new ArrayList();
        MutablePicoContainer delegator = null;
        LockCookie lc;
        result.AddRange(componentKeyToAdapterMap.Keys);
        rwLock.AcquireReaderLock(lockTimeOut);
        try {
          for (int x=0;x < parentsWeakReferences.Count;x++) {
            delegator = (MutablePicoContainer)((WeakReference)parentsWeakReferences[x]).Target;
            if (delegator == null) {
              try {
                lc = rwLock.UpgradeToWriterLock(lockTimeOut);
                try {              
                  parentsWeakReferences.RemoveAt(x);
                  x--;
                } finally {
                  rwLock.DowngradeFromWriterLock(ref lc);
                }
              } catch (Exception) {
                // ignore
              }
            } else {
              foreach (object o in delegator.ComponentKeys) {
                if (!result.Contains(o)) {
                  result.Add(o);
                }
              }
            }
          }
        
        } finally {
          rwLock.ReleaseReaderLock();
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

    public ComponentAdapter UnRegisterComponent(object componentKey) {
      ComponentAdapter ret = (ComponentAdapter)componentKeyToAdapterMap[componentKey];
      componentKeyToAdapterMap.Remove(componentKey);
      return ret;
    }

    public  ComponentAdapter FindComponentAdapter(object componentKey) {
      ComponentAdapter result = FindComponentAdapterImpl(componentKey);
      if (result != null) {
        return result;
      } else {
        MutablePicoContainer delegator = null;
        LockCookie lc;
        rwLock.AcquireReaderLock(Timeout.Infinite);
        try {
          for (int x=0;x < parentsWeakReferences.Count;x++) {
            delegator = (MutablePicoContainer)((WeakReference)parentsWeakReferences[x]).Target;
            if (delegator == null) {
              try {
                lc = rwLock.UpgradeToWriterLock(lockTimeOut);
                try {              
                  parentsWeakReferences.RemoveAt(x);
                  x--;
                } finally {
                  rwLock.DowngradeFromWriterLock(ref lc);
                }
              } catch (Exception) {
                // ignore
              }
            } else {
              ComponentAdapter componentAdapter = delegator.FindComponentAdapter(componentKey);
              if (componentAdapter != null) {
                return componentAdapter;
              }

            }
          }
        
        } finally {
          rwLock.ReleaseReaderLock();
        }
        return null;
      }
    }

    private ComponentAdapter FindComponentAdapterImpl(object componentKey) {
      ComponentAdapter result = (ComponentAdapter) componentKeyToAdapterMap[componentKey];
      Type componentKeyType = componentKey as Type;
      if (result == null && componentKeyType != null) {
        result = FindImplementingComponentAdapter(componentKeyType);
      }

      return result;
    }

    public ComponentAdapter RegisterComponentInstance(object component) {
      return RegisterComponentInstance(component.GetType(), component);
    }

    public ComponentAdapter RegisterComponentInstance(object componentKey, object componentInstance)  {
      ComponentAdapter componentAdapter = new InstanceComponentAdapter(componentKey, componentInstance);
      RegisterComponent(componentAdapter);

      AddOrderedComponentAdapter(componentAdapter);
      unmanagedComponents.Add(componentInstance);

      return componentAdapter;
    }

    public ComponentAdapter RegisterComponentImplementation(Type componentImplementation) {
      return RegisterComponentImplementation(componentImplementation, componentImplementation);
    }

    public object GetComponentMulticaster(bool callInInstantiationOrder, bool callUnmanagedComponents) {
      ComponentMulticasterFactory componentMulticasterFactory = new DefaultComponentMulticasterFactory();

      IList componentsToMulticast = ComponentInstances;
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

    public ComponentAdapter RegisterComponentImplementation(object componentKey, Type componentImplementation) {
      return RegisterComponentImplementation(componentKey, componentImplementation, null);
    }

    public ComponentAdapter RegisterComponentImplementation(object componentKey, Type componentImplementation, Parameter[] parameters)  {
      ComponentAdapter componentAdapter = componentAdapterFactory.CreateComponentAdapter(componentKey, componentImplementation, parameters);
      RegisterComponent(componentAdapter);
      return componentAdapter;
    }

    public void AddOrderedComponentAdapter(ComponentAdapter componentAdapter) {
      instantiantionOrderedComponentAdapters.Add(componentAdapter);
    }

    public IList ComponentInstances  {
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
      return ComponentKeys.Contains(componentKey);
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

    public IList ChildContainers {
      get {
        return ArrayList.ReadOnly(children);
      }
    }

    public IList ParentContainers {
      get {
        ArrayList rst = new ArrayList();
        MutablePicoContainer delegator = null;
        LockCookie lc;
        rwLock.AcquireReaderLock(Timeout.Infinite);
        for (int x=0;x < parentsWeakReferences.Count;x++) {
          delegator = (MutablePicoContainer)((WeakReference)parentsWeakReferences[x]).Target;
          if (delegator == null) {
            lc = rwLock.UpgradeToWriterLock(Timeout.Infinite);
            parentsWeakReferences.RemoveAt(x);
            x--;
            rwLock.DowngradeFromWriterLock(ref lc);
          } else {
            rst.Add(delegator);
          }
        }

        return ArrayList.ReadOnly(rst);
      }
    }

    public bool AddChild(MutablePicoContainer child) {
      bool added = false;
      if (!children.Contains(child)) {
        children.Add(child);
        added = true;
      }
      if (!child.ParentContainers.Contains(this)) {
        child.AddParent(this);
      }

      return added;
    }

    public bool RemoveChild(MutablePicoContainer child) {
      bool removed = children.Contains(child);
      if (removed) {
        children.Remove(child);
      }
      if (child.ParentContainers.Contains(this)) {
        child.RemoveParent(this);
      }
      return removed;
    }

    
    public bool AddParent(MutablePicoContainer parent) {
      bool added = false;
      bool found = false;
      MutablePicoContainer delegator = null;
      LockCookie lc ;
      rwLock.AcquireReaderLock(Timeout.Infinite);
      try {
        for (int x=0;x < parentsWeakReferences.Count;x++) {
          delegator = (MutablePicoContainer)((WeakReference)parentsWeakReferences[x]).Target;
          if (delegator == null) {
            try {
              lc = rwLock.UpgradeToWriterLock(lockTimeOut);
              try {              
                parentsWeakReferences.RemoveAt(x);
                x--;
              } finally {
                rwLock.DowngradeFromWriterLock(ref lc);
              }
            } catch (Exception) {
              // ignore
            }
          } else {
            if (delegator.Equals(parent)) {
              found = true;
            }
          }
        }
      }
      finally {
        rwLock.ReleaseReaderLock();
      }

      if (!found) {
        rwLock.AcquireWriterLock(lockTimeOut);
        try {
          parentsWeakReferences.Add(new WeakReference(parent));
          added = true;
        } finally {
          rwLock.ReleaseWriterLock();
        }
      }
      if (!parent.ChildContainers.Contains(this)) {
        parent.AddChild(this);
      }

      return added;
    }

    public bool RemoveParent(MutablePicoContainer parent) {

      bool removed = false;
      MutablePicoContainer delegator = null;
      LockCookie lc;
      rwLock.AcquireReaderLock(Timeout.Infinite);
      try {
        for (int x=0;x < parentsWeakReferences.Count;x++) {
          delegator = (MutablePicoContainer)((WeakReference)parentsWeakReferences[x]).Target;
          if (delegator == null || delegator.Equals(parent)) {
            try {
              lc = rwLock.UpgradeToWriterLock(lockTimeOut);
              try {              
                parentsWeakReferences.RemoveAt(x);
                x--;
              } finally {
                rwLock.DowngradeFromWriterLock(ref lc);
              }
            } catch (Exception) {
              // ignore
            }
          } else {
            if (delegator.Equals(parent)) {
              removed = true;
            }
          }
        }
      } finally {
        rwLock.ReleaseWriterLock();
      }
      return removed;
    }
  }

}
