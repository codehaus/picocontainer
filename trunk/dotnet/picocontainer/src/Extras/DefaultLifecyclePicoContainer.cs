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
using PicoContainer;
using PicoContainer.Defaults;
using PicoContainer.Lifecycle;
using PicoContainer.Extras;

namespace PicoContainer.Extras
{
	
	public class DefaultLifecyclePicoContainer : LifecyclePicoAdapter, MutablePicoContainer
	{
    private DefaultPicoContainer mutablePicoContainer;
		
    private LifecyclePicoAdapter lifecyclePicoAdapter;
		
    public DefaultLifecyclePicoContainer(LifecyclePicoAdapter lifecyclePicoAdapter, DefaultPicoContainer mutablePicoContainer) {
      this.lifecyclePicoAdapter = lifecyclePicoAdapter;
      this.mutablePicoContainer = mutablePicoContainer;
    }
		
    public DefaultLifecyclePicoContainer(DefaultPicoContainer mutablePicoContainer) {
      this.mutablePicoContainer = mutablePicoContainer;
      lifecyclePicoAdapter = new DefaultLifecyclePicoAdapter(mutablePicoContainer);
    }
		
    public DefaultLifecyclePicoContainer() {
      mutablePicoContainer = new DefaultPicoContainer();
      lifecyclePicoAdapter = new DefaultLifecyclePicoAdapter(mutablePicoContainer);
    }

    virtual public PicoContainer PicoContainer
		{
			get
			{
				return mutablePicoContainer;
			}
			
		}
		virtual public bool Started
		{
			get
			{
				return lifecyclePicoAdapter.Started;
			}
			
		}
		
		virtual public IList ChildContainers
		{
      get {
            return mutablePicoContainer.ChildContainers;
          }
		
    }
		virtual public bool Disposed
		{
			get
			{
				return lifecyclePicoAdapter.Disposed;
			}
			
		}
		
		virtual public IList ComponentKeys
		{
      get {
        return mutablePicoContainer.ComponentKeys;
      }
		}
		virtual public bool Stopped
		{
			get
			{
				return lifecyclePicoAdapter.Stopped;
			}
			
		}

    virtual public IList ComponentInstances
		{
      get {
        return mutablePicoContainer.ComponentInstances;
      }
		}

		virtual public IList ParentContainers
		{ 
      get {
        return mutablePicoContainer.ParentContainers;
      }
		}
				
		public virtual ComponentAdapter FindComponentAdapter(object componentKey)
		{
			return mutablePicoContainer.FindComponentAdapter(componentKey);
		}
		
		public virtual void  Dispose()
		{
			lifecyclePicoAdapter.Dispose();
		}
		
		public virtual void  Start()
		{
			lifecyclePicoAdapter.Start();
		}
		
		public virtual object GetComponentInstance(object componentKey)
		{
			return (ComponentAdapter)mutablePicoContainer.GetComponentInstance(componentKey);
		}
		
		public virtual void  Stop()
		{
			lifecyclePicoAdapter.Stop();
		}
		
		public virtual object GetComponentMulticaster()
		{
        return this.GetComponentMulticaster(true,false);
		}
		
    public virtual object GetComponentMulticaster(bool callInInstantiationOrder, bool callUnmanagedComponents)
    {
    	return mutablePicoContainer.GetComponentMulticaster(callInInstantiationOrder, callUnmanagedComponents);
    }

    public virtual bool HasComponent(object componentKey)
		{
			return mutablePicoContainer.HasComponent(componentKey);
		}
		
		
		public virtual ComponentAdapter UnRegisterComponent(object componentKey)
		{
			return mutablePicoContainer.UnRegisterComponent(componentKey);
		}
		
		public virtual ComponentAdapter RegisterComponentInstance(object componentKey, object componentInstance)
		{
			return mutablePicoContainer.RegisterComponentInstance(componentKey, componentInstance);
		}
		
		public virtual bool AddChild(MutablePicoContainer child)
		{
			return mutablePicoContainer.AddChild(child);
		}

    public virtual bool RemoveChild(MutablePicoContainer child) {
      return mutablePicoContainer.RemoveChild(child);
    }

		public virtual bool AddParent(MutablePicoContainer parent)
		{
			return mutablePicoContainer.AddParent(parent);
		}

    public virtual bool RemoveParent(MutablePicoContainer parent) {
      return mutablePicoContainer.RemoveParent(parent);
    }

		public virtual ComponentAdapter RegisterComponentImplementation(object componentKey, System.Type componentImplementation)
		{
			return mutablePicoContainer.RegisterComponentImplementation(componentKey, componentImplementation);
		}
		
		public virtual ComponentAdapter RegisterComponentInstance(object componentInstance)
		{
			return mutablePicoContainer.RegisterComponentInstance(componentInstance);
		}
		
		public virtual void  AddOrderedComponentAdapter(ComponentAdapter componentAdapter)
		{
			mutablePicoContainer.AddOrderedComponentAdapter(componentAdapter);
		}
		
		public virtual ComponentAdapter RegisterComponentImplementation(System.Type componentImplementation)
		{
			return mutablePicoContainer.RegisterComponentImplementation(componentImplementation);
		}
		
		public virtual ComponentAdapter RegisterComponentImplementation(object componentKey, System.Type componentImplementation, Parameter[] parameters)
		{
			return mutablePicoContainer.RegisterComponentImplementation(componentKey, componentImplementation);
		}

    public void RegisterComponent(ComponentAdapter componentAdapter) {
      mutablePicoContainer.RegisterComponent(componentAdapter);
    }

	}
}