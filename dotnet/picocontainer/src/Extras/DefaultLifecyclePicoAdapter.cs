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
using PicoContainer.Lifecycle;

namespace PicoContainer.Extras
{
	public class DefaultLifecyclePicoAdapter : LifecyclePicoAdapter
	{
		virtual public bool Started
		{
			get
			{
				return started;
			}
			
		}
		virtual public bool Stopped
		{
			get
			{
				return !started;
			}
			
		}
    
		virtual public bool Disposed
		{
			get
			{
				return disposed;
			}
			
		}
		virtual public PicoContainer PicoContainer
		{
			get
			{
				return picoContainer;
			}
			
		}
		
		private Startable StartableAggregatedComponent;
		private Stoppable StoppableAggregatedComponent;
		private IDisposable disposableAggregatedComponent;
		private bool started;
		private bool disposed;

		private PicoContainer picoContainer;
		
		public DefaultLifecyclePicoAdapter(PicoContainer picoContainer)
		{
			this.picoContainer = picoContainer;
		}
		
		private void  InitializeIfNotInitialized()
		{
			if (StartableAggregatedComponent == null)
			{
			  StartableAggregatedComponent = picoContainer.GetComponentMulticaster(true, false) as Startable ;
			}
			if (StoppableAggregatedComponent == null)
			{
        StoppableAggregatedComponent = picoContainer.GetComponentMulticaster(false, false) as Stoppable;
			}
			if (disposableAggregatedComponent == null)
			{
					disposableAggregatedComponent = picoContainer.GetComponentMulticaster(false, false) as IDisposable;
			}
		}
		
		public virtual void  Start()
		{
			CheckDisposed();
			InitializeIfNotInitialized();
			if (Started)
			{
				throw new System.SystemException("Already Started.");
			}
			started = true;
			if (StartableAggregatedComponent != null)
			{
				StartableAggregatedComponent.Start();
			}
		}
		
		public virtual void  Stop()
		{
			CheckDisposed();
			InitializeIfNotInitialized();
			if (started == false)
			{
				throw new System.SystemException("Already Stopped (or maybe never Started).");
			}
			started = false;
			if (StoppableAggregatedComponent != null)
			{
				StoppableAggregatedComponent.Stop();
			}
		}
		
		public virtual void  Dispose()
		{
			CheckDisposed();
			InitializeIfNotInitialized();
			disposed = true;
			if (disposableAggregatedComponent != null)
			{
				disposableAggregatedComponent.Dispose();
			}
		}
		
		private void  CheckDisposed()
		{
			if (disposed)
			{
				throw new System.SystemException("Components already disposed of");
			}
		}
	}
}