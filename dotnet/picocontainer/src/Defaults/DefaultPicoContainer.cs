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

namespace PicoContainer.Defaults
{
	public class DefaultPicoContainer : IMutablePicoContainer, IDisposable
	{
		private readonly IDictionary componentKeyToAdapterMap = new Hashtable();
		private readonly IComponentAdapterFactory componentAdapterFactory;
		private IPicoContainer parent;
		private readonly IList componentAdapters = new ArrayList();
		private readonly IList orderedComponentAdapters = new ArrayList();

		private bool started = false;
		private bool disposed = false;

		public DefaultPicoContainer(IComponentAdapterFactory componentAdapterFactory, IPicoContainer parent)
		{
			this.componentAdapterFactory = componentAdapterFactory;
			this.parent = parent;
		}

		public DefaultPicoContainer(IPicoContainer parent) : this(new DefaultComponentAdapterFactory(), parent)
		{
		}

		public DefaultPicoContainer(IComponentAdapterFactory componentAdapterFactory) : this(componentAdapterFactory, null)
		{
		}

		public DefaultPicoContainer() : this(new DefaultComponentAdapterFactory(), null)
		{
		}

		public IList ComponentAdapters
		{
			get { return ArrayList.ReadOnly(componentAdapters); }
		}

		public IComponentAdapter GetComponentAdapter(object componentKey)
		{
			IComponentAdapter adapter = (IComponentAdapter) componentKeyToAdapterMap[componentKey];
			if (adapter == null && parent != null)
			{
				adapter = parent.GetComponentAdapter(componentKey);
			}

			return adapter;
		}

		public IComponentAdapter GetComponentAdapterOfType(Type componentType)
		{
			IComponentAdapter adapterByKey = GetComponentAdapter(componentType);
			if (adapterByKey != null)
			{
				return adapterByKey;
			}

			IList found = GetComponentAdaptersOfType(componentType);

			if (found.Count == 1)
			{
				return (IComponentAdapter) found[0];
			}

			if (found.Count == 0)
			{
				if (parent != null)
				{
					return parent.GetComponentAdapterOfType(componentType);
				}
				else
				{
					return null;
				}
			}
			else
			{
				Type[] foundClasses = new Type[found.Count];
				for (int i = 0; i < foundClasses.Length; i++)
				{
					IComponentAdapter componentAdapter = (IComponentAdapter) found[i];
					foundClasses[i] = componentAdapter.ComponentImplementation;
				}

				throw new AmbiguousComponentResolutionException(componentType, foundClasses);
			}
		}

		public IList GetComponentAdaptersOfType(Type componentType)
		{
			IList found = new ArrayList();

			foreach (IComponentAdapter componentAdapter in ComponentAdapters)
			{
				if (componentType.IsAssignableFrom(componentAdapter.ComponentImplementation))
				{
					found.Add(componentAdapter);
				}
			}
			return found;
		}

		public IComponentAdapter RegisterComponent(IComponentAdapter componentAdapter)
		{
			object componentKey = componentAdapter.ComponentKey;
			if (componentKeyToAdapterMap.Contains(componentKey))
			{
				throw new DuplicateComponentKeyRegistrationException(componentKey);
			}
			componentAdapter.Container = this;
			componentAdapters.Add(componentAdapter);
			componentKeyToAdapterMap.Add(componentKey, componentAdapter);

			return componentAdapter;
		}

		public IComponentAdapter UnregisterComponent(object componentKey)
		{
			IComponentAdapter adapter = (IComponentAdapter) componentKeyToAdapterMap[componentKey];
			if (adapter != null)
			{
				componentKeyToAdapterMap.Remove(componentKey);
				componentAdapters.Remove(adapter);
				orderedComponentAdapters.Remove(adapter);
			}

			return adapter;
		}

		public IComponentAdapter RegisterComponentInstance(object component)
		{
			return this.RegisterComponentInstance(component.GetType(), component);
		}

		public IComponentAdapter RegisterComponentInstance(object componentKey, object componentInstance)
		{
			if (componentInstance == this)
			{
				throw new PicoRegistrationException("Can not register a container to itself. The container is already implicitly registered.");
			}

			IComponentAdapter componentAdapter = new InstanceComponentAdapter(componentKey, componentInstance);

			RegisterComponent(componentAdapter);

			return componentAdapter;
		}


		public IComponentAdapter RegisterComponentImplementation(Type componentImplementation)
		{
			return RegisterComponentImplementation(componentImplementation, componentImplementation);
		}

		public IComponentAdapter RegisterComponentImplementation(object componentKey, Type componentImplementation)
		{
			return RegisterComponentImplementation(componentKey, componentImplementation, null);
		}

		public IComponentAdapter RegisterComponentImplementation(object componentKey, Type componentImplementation, IParameter[] parameters)
		{
			IComponentAdapter componentAdapter = componentAdapterFactory.CreateComponentAdapter(componentKey, componentImplementation, parameters);

			RegisterComponent(componentAdapter);

			return componentAdapter;
		}


		public void AddOrderedComponentAdapter(IComponentAdapter componentAdapter)
		{
			if (!orderedComponentAdapters.Contains(componentAdapter))
			{
				orderedComponentAdapters.Add(componentAdapter);
			}
		}

		public IList ComponentInstances
		{
			get { return GetComponentInstancesOfType(null); }
		}

		public IList GetComponentInstancesOfType(Type componentType)
		{
			IDictionary adapterToInstanceMap = new Hashtable();
			foreach (IComponentAdapter componentAdapter in componentAdapters)
			{
				if (componentType == null || componentType.IsAssignableFrom(componentAdapter.ComponentImplementation))
				{
					object componentInstance = componentAdapter.ComponentInstance;
					adapterToInstanceMap.Add(componentAdapter, componentInstance);

					// This is to ensure all are added. (Indirect dependencies will be added
					// from InstantiatingComponentAdapter).
					AddOrderedComponentAdapter(componentAdapter);
				}
			}
			IList result = new ArrayList();
			foreach (object componentAdapter in orderedComponentAdapters)
			{
				object componentInstance = adapterToInstanceMap[componentAdapter];
				if (componentInstance != null)
				{
					// may be null in the case of the "implicit" adapter
					// representing "this".
					result.Add(componentInstance);
				}
			}
			return ArrayList.ReadOnly(result);
		}


		public object GetComponentInstance(object componentKey)
		{
			IComponentAdapter componentAdapter = GetComponentAdapter(componentKey);
			if (componentAdapter != null)
			{
				return componentAdapter.ComponentInstance;
			}
			else
			{
				return null;
			}
		}

		public object GetComponentInstanceOfType(Type componentType)
		{
			IComponentAdapter componentAdapter = GetComponentAdapterOfType(componentType);
			return componentAdapter == null ? null : componentAdapter.ComponentInstance;
		}

		public IComponentAdapter UnregisterComponentByInstance(object componentInstance)
		{
			foreach (IComponentAdapter componentAdapter in ComponentAdapters)
			{
				if (componentAdapter.ComponentInstance.Equals(componentInstance))
				{
					return UnregisterComponent(componentAdapter.ComponentKey);
				}
			}
			return null;
		}

		public IPicoContainer Parent
		{
			get { return parent; }
			set { parent = value; }
		}

		public void Verify()
		{
			IList nestedVerificationExceptions = new ArrayList();
			foreach (IComponentAdapter componentAdapter in ComponentAdapters)
			{
				try
				{
					componentAdapter.Verify();
				}
				catch (UnsatisfiableDependenciesException e)
				{
					nestedVerificationExceptions.Add(e);
				}
			}

			if (nestedVerificationExceptions.Count > 0)
			{
				throw new PicoVerificationException(nestedVerificationExceptions);
			}
		}


		public void Start()
		{
			if (started) throw new ApplicationException("Already started");
			if (disposed) throw new ApplicationException("Already disposed");
			IList adapters = OrderComponentAdaptersWithContainerAdaptersLast(componentAdapters);
			foreach (IComponentAdapter componentAdapter in  adapters)
			{
				if (typeof (IStartable).IsAssignableFrom(componentAdapter.ComponentImplementation))
				{
					IStartable startable = (IStartable) componentAdapter.ComponentInstance;
					startable.Start();
				}
			}
			started = true;
		}


		public void Stop()
		{
			if (!started) throw new ApplicationException("Not started");
			if (disposed) throw new ApplicationException("Already disposed");
			IList adapters = OrderComponentAdaptersWithContainerAdaptersLast(componentAdapters);

			for (int x = adapters.Count - 1; x >= 0; x--)
			{
				IComponentAdapter componentAdapter = (IComponentAdapter) adapters[x];
				if (typeof (IStartable).IsAssignableFrom(componentAdapter.ComponentImplementation))
				{
					IStartable startable = (IStartable) componentAdapter.ComponentInstance;
					startable.Stop();
				}
			}
			started = false;
		}

		public void Dispose()
		{
			if (disposed) throw new SystemException("Already disposed");
			IList adapters = OrderComponentAdaptersWithContainerAdaptersLast(componentAdapters);
			for (int x = adapters.Count - 1; x >= 0; x--)
			{
				IComponentAdapter componentAdapter = (IComponentAdapter) adapters[x];
				if (typeof (IDisposable).IsAssignableFrom(componentAdapter.ComponentImplementation))
				{
					IDisposable disposable = (IDisposable) componentAdapter.ComponentInstance;
					disposable.Dispose();
				}
			}
			disposed = true;

		}


		public static IList OrderComponentAdaptersWithContainerAdaptersLast(IList ComponentAdapters)
		{
			ArrayList result = new ArrayList();
			ArrayList containers = new ArrayList();
			foreach (IComponentAdapter adapter in ComponentAdapters)
			{
				if (typeof (IPicoContainer).IsAssignableFrom(adapter.ComponentImplementation))
				{
					containers.Add(adapter);
				}
				else
				{
					result.Add(adapter);
				}

			}
			result.AddRange(containers);

			return result;
		}

	}


}