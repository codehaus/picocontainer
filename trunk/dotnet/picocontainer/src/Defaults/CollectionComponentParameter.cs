using System;
using System.Collections;

namespace PicoContainer.Defaults
{
	[Serializable]
	public class CollectionComponentParameter : IParameter
	{
		/**
     * Use <code>ARRAY</code> as {@link Parameter}for an Array that must have elements.
     */
		public static readonly CollectionComponentParameter ARRAY = new CollectionComponentParameter();
		/**
     * Use <code>ARRAY_ALLOW_EMPTY</code> as {@link Parameter}for an Array that may have no
     * elements.
     */
		public static readonly CollectionComponentParameter ARRAY_ALLOW_EMPTY = new CollectionComponentParameter(true);

		private readonly bool emptyCollection;
		private readonly Type componentKeyType;
		private readonly Type componentValueType;

		/**
     * Expect an {@link Array}of an appropriate type as parameter. At least one component of
     * the array's component type must exist.
     */

		public CollectionComponentParameter() : this(false)
		{
		}

		/**
     * Expect an {@link Array}of an appropriate type as parameter.
     * 
     * @param emptyCollection <code>true</code> if an empty array also is a valid dependency
     *                   resolution.
     */

		public CollectionComponentParameter(bool emptyCollection) : this(typeof(void), emptyCollection)
		{
		}

		/**
     * Expect any of the collection types {@link Array},{@link Collection}or {@link Map}as
     * parameter.
     * 
     * @param componentValueType the type of the components (ignored in case of an Array)
     * @param emptyCollection <code>true</code> if an empty collection resolves the
     *                   dependency.
     */

		public CollectionComponentParameter(Type componentValueType, bool emptyCollection) : this(typeof (object), componentValueType, emptyCollection)
		{
		}

		/**
     * Expect any of the collection types {@link Array},{@link Collection}or {@link Map}as
     * parameter.
     * 
     * @param componentKeyType the type of the component's key
     * @param componentValueType the type of the components (ignored in case of an Array)
     * @param emptyCollection <code>true</code> if an empty collection resolves the
     *                   dependency.
     */

		public CollectionComponentParameter(Type componentKeyType, Type componentValueType, bool emptyCollection)
		{
			this.emptyCollection = emptyCollection;
			this.componentKeyType = componentKeyType;
			this.componentValueType = componentValueType;
		}

		/**
     * Resolve the parameter for the expected type. The method will return <code>null</code>
     * If the expected type is not one of the collection types {@link Array},
     * {@link Collection}or {@link Map}. An empty collection is only a valid resolution, if
     * the <code>emptyCollection</code> flag was set.
     * 
     * @param container {@inheritDoc}
     * @param adapter {@inheritDoc}
     * @param expectedType {@inheritDoc}
     * @return the instance of the collection type or <code>null</code>
     * @throws PicoInstantiationException {@inheritDoc}
     */

		public Object ResolveInstance(IPicoContainer container, IComponentAdapter adapter, Type expectedType)
		{
			// type check is done in isResolvable
			Object result = null;
			Type collectionType = GetCollectionType(expectedType);
			if (collectionType != null)
			{
				IDictionary adapterMap = GetMatchingComponentAdapters(container, adapter, componentKeyType, GetValueType(expectedType));
				if (typeof(Array).IsAssignableFrom(collectionType))
				{
					result = GetArrayInstance(container, expectedType, adapterMap);
				}
				else if (typeof(IDictionary).IsAssignableFrom(collectionType))
				{
						// todo mward fix...
					//result = GetMapInstance(container, expectedType, adapterMap);
				}
				else if ( typeof(ICollection).IsAssignableFrom(collectionType)) 
				{
					result = GetCollectionInstance(container,expectedType,adapterMap);
				}
				else
				{
					throw new PicoIntrospectionException(expectedType.Name + " is not a collective type");
				}
			}
			return result;
		}

		/**
     * Check for a successful dependency resolution of the parameter for the expected type. The
     * dependency can only be satisfied if the expected type is one of the collection types
     * {@link Array},{@link Collection}or {@link Map}. An empty collection is only a valid
     * resolution, if the <code>emptyCollection</code> flag was set.
     * 
     * @param container {@inheritDoc}
     * @param adapter {@inheritDoc}
     * @param expectedType {@inheritDoc}
     * @return <code>true</code> if matching components were found or an empty collective type
     *               is allowed
     */

		public bool IsResolvable(IPicoContainer container, IComponentAdapter adapter, Type expectedType)
		{
			Type collectionType = GetCollectionType(expectedType);
			Type valueType = GetValueType(expectedType);
			
			return collectionType != null && (emptyCollection || GetMatchingComponentAdapters(container, adapter, componentKeyType, valueType).Count > 0);
		}

		/**
     * Verify a successful dependency resolution of the parameter for the expected type. The
     * method will only return if the expected type is one of the collection types {@link Array},
     * {@link Collection}or {@link Map}. An empty collection is only a valid resolution, if
     * the <code>emptyCollection</code> flag was set.
     * 
     * @param container {@inheritDoc}
     * @param adapter {@inheritDoc}
     * @param expectedType {@inheritDoc}
     * @throws PicoIntrospectionException {@inheritDoc}
     */

		public void Verify(IPicoContainer container, IComponentAdapter adapter, Type expectedType)
		{
			Type collectionType = GetCollectionType(expectedType);
			if (collectionType != null)
			{
				Type valueType = GetValueType(expectedType);
				ICollection componentAdapters = GetMatchingComponentAdapters(container, adapter, componentKeyType, valueType).Values;
				if (componentAdapters.Count == 0)
				{
					if (!emptyCollection)
					{
						throw new PicoIntrospectionException(expectedType.Name
							+ " not resolvable, no components of type "
							+ GetValueType(expectedType).Name
							+ " available");
					}
				}
				else
				{
					foreach (IComponentAdapter componentAdapter in componentAdapters)
					{
						componentAdapter.Verify(container);
					}
				}
			}
			else
			{
				throw new PicoIntrospectionException(expectedType.Name + " is not a collective type");
			}
			return;
		}

		/**
     * Evaluate whether the given component adapter will be part of the collective type.
     * 
     * @param adapter a <code>ComponentAdapter</code> value
     * @return <code>true</code> if the adapter takes part
     */

		protected bool Evaluate(IComponentAdapter adapter)
		{
			return adapter != null; // use parameter, prevent compiler warning
		}

		/**
     * Collect the matching ComponentAdapter instances.
     * @param container container to use for dependency resolution
     * @param adapter {@link ComponentAdapter} to exclude
     * @param keyType the compatible type of the key
     * @param valueType the compatible type of the component
     * @return a {@link Map} with the ComponentAdapter instances and their component keys as map key.
     */

		protected IDictionary GetMatchingComponentAdapters(IPicoContainer container, 
			IComponentAdapter adapter, 
			Type keyType, 
			Type valueType)
		{
			IDictionary adapterMap = new Hashtable();
			IPicoContainer parent = container.Parent;
			if (parent != null)
			{
				IDictionary matchingComponentAdapters = GetMatchingComponentAdapters(parent, adapter, keyType, valueType);

				foreach (DictionaryEntry entry in matchingComponentAdapters)
				{
					adapterMap.Add(entry.Key, entry.Value);	
				}
				
			}
			ICollection allAdapters = container.ComponentAdapters;

			foreach (IComponentAdapter componentAdapter in allAdapters)
			{
				adapterMap.Remove(componentAdapter.ComponentKey);

			}
			IList adapterList = container.GetComponentAdaptersOfType(valueType);

			foreach (IComponentAdapter componentAdapter in adapterList)
			{
				object key = componentAdapter.ComponentKey;
				if (adapter != null && key.Equals(adapter.ComponentKey))
				{
					continue;
				}
				if (keyType.IsAssignableFrom(key.GetType()) && Evaluate(componentAdapter))
				{
					adapterMap.Add(key, componentAdapter);
				}
			}
			return adapterMap;
		}

		private Type GetCollectionType(Type collectionType)
		{
			Type collectionClass = null;
			if (collectionType.IsArray)
			{
				collectionClass = typeof (Array);
			}
			else if (typeof (IDictionary).IsAssignableFrom(collectionType))
			{
				collectionClass = typeof (IDictionary);
			}
			else if (typeof (ICollection).IsAssignableFrom(collectionType))
			{
				collectionClass = typeof (ICollection);
			}
			return collectionClass;
		}

		private Type GetValueType(Type collectionType)
		{
			Type valueType = componentValueType;
			if (collectionType.IsArray)
			{
				valueType = collectionType.GetType();
			}
			return valueType;
		}

		private object[] GetArrayInstance(IPicoContainer container, Type expectedType, IDictionary adapterList)
		{
			object[] result = (Object[]) Array.CreateInstance(expectedType, adapterList.Count);
			int i = 0;

			foreach (IComponentAdapter componentAdapter in adapterList)
			{
				result[i] = container.GetComponentInstance(componentAdapter.ComponentKey);
				i++;
			}
			return result;
		}

		private ICollection GetCollectionInstance(IPicoContainer container, Type expectedType, IDictionary adapterList)
		{
			Type collectionType = expectedType;
			if (collectionType.IsInterface)
			{
				// The order of tests are significant. The least generic types last.
				if (typeof (IList).IsAssignableFrom(collectionType))
				{
					collectionType = typeof (ArrayList);
				}
//            } else if (BlockingQueue.class.isAssignableFrom(collectionType)) {
//                collectionType = ArrayBlockingQueue.class;
//            } else if (Queue.class.isAssignableFrom(collectionType)) {
//                collectionType = LinkedList.class;
					/*} else if (SortedSet.class.isAssignableFrom(collectionType)) {
                collectionType = TreeSet.class;
            } else if (Set.class.isAssignableFrom(collectionType)) {
                collectionType = HashSet.class;
            }*/
				else if (typeof (ICollection).IsAssignableFrom(collectionType))
				{
					collectionType = typeof (ArrayList);
				}
			}
			try
			{
				IList result = (IList) Activator.CreateInstance(collectionType);

				foreach (IComponentAdapter componentAdapter in adapterList)
				{
					result.Add(container.GetComponentInstance(componentAdapter.ComponentKey));
				}

				return result;
			}
			catch (Exception e)
			{
				throw new PicoInitializationException(e);

			}
		}


		// todo mward implement this
		/*private Hashtable getMapInstance(IPicoContainer container, Type expectedType, Hashtable adapterList) 
		{
			Type collectionType = expectedType;
			if (collectionType.IsInterface) 
			{
			// The order of tests are significant. The least generic types last.
			if (SortedMap.class.isAssignableFrom(collectionType)) {
				collectionType = TreeMap.class;
//            } else if (ConcurrentMap.class.isAssignableFrom(collectionType)) {
//                collectionType = ConcurrentHashMap.class;
			} else if (Map.class.isAssignableFrom(collectionType)) {
				collectionType = HashMap.class;
			}
		}
		try {
			Map result = (Map) collectionType.newInstance();
			for (final Iterator iterator = adapterList.entrySet().iterator(); iterator.hasNext();) {
				final Map.Entry entry = (Map.Entry) iterator.next();
				final Object key = entry.getKey();
				final ComponentAdapter componentAdapter = (ComponentAdapter) entry.getValue();
				result.put(key, container.getComponentInstance(key));
			}
			return result;
		} catch (InstantiationException e) {
			///CLOVER:OFF
			throw new PicoInitializationException(e);
			///CLOVER:ON
		} catch (IllegalAccessException e) {
			///CLOVER:OFF
			throw new PicoInitializationException(e);
			///CLOVER:ON
		}
	}*/


	}
}