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
    private MutablePicoContainer mutablePicoContainer;
		
    private LifecyclePicoAdapter lifecyclePicoAdapter;
		
    public DefaultLifecyclePicoContainer(LifecyclePicoAdapter lifecyclePicoAdapter, MutablePicoContainer mutablePicoContainer) {
      this.lifecyclePicoAdapter = lifecyclePicoAdapter;
      this.mutablePicoContainer = mutablePicoContainer;
    }
		
    public DefaultLifecyclePicoContainer(MutablePicoContainer mutablePicoContainer) {
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
		
		virtual public ArrayList ChildContainers
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
		
		virtual public ICollection ComponentKeys
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

    virtual public ArrayList ComponentInstances
		{
      get {
        return mutablePicoContainer.ComponentInstances;
      }
		}

		virtual public ArrayList ParentContainers
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
			return mutablePicoContainer.GetComponentInstance(componentKey);
		}
		
		public virtual void  Stop()
		{
			lifecyclePicoAdapter.Stop();
		}
		
		public virtual object GetComponentMulticaster()
		{
        return mutablePicoContainer.GetComponentMulticaster();
		}
		
		public virtual bool HasComponent(object componentKey)
		{
			return mutablePicoContainer.HasComponent(componentKey);
		}
		
		public virtual object GetComponentMulticaster(bool callInInstantiationOrder, bool callUnmanagedComponents)
		{
			return mutablePicoContainer.GetComponentMulticaster(callInInstantiationOrder, callUnmanagedComponents);
		}
		
		public virtual object UnRegisterComponent(object componentKey)
		{
			return mutablePicoContainer.UnRegisterComponent(componentKey);
		}
		
		public virtual object RegisterComponentInstance(object componentKey, object componentInstance)
		{
			return mutablePicoContainer.RegisterComponentInstance(componentKey, componentInstance);
		}
		
		public virtual void  AddChild(MutablePicoContainer child)
		{
			mutablePicoContainer.AddChild(child);
		}
		
		public virtual void  AddParent(MutablePicoContainer parent)
		{
			mutablePicoContainer.AddParent(parent);
		}
		
		public virtual object RegisterComponentImplementation(object componentKey, System.Type componentImplementation)
		{
			return mutablePicoContainer.RegisterComponentImplementation(componentKey, componentImplementation);
		}
		
		public virtual object RegisterComponentInstance(object componentInstance)
		{
			return mutablePicoContainer.RegisterComponentInstance(componentInstance);
		}
		
		public virtual void  AddOrderedComponentAdapter(ComponentAdapter componentAdapter)
		{
			mutablePicoContainer.AddOrderedComponentAdapter(componentAdapter);
		}
		
		public virtual object RegisterComponentImplementation(System.Type componentImplementation)
		{
			return mutablePicoContainer.RegisterComponentImplementation(componentImplementation);
		}
		
		public virtual object RegisterComponentImplementation(object componentKey, System.Type componentImplementation, Parameter[] parameters)
		{
			return mutablePicoContainer.RegisterComponentImplementation(componentKey, componentImplementation);
		}
	}
}