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

namespace PicoContainer.Defaults
{
	/// <summary>
	/// This ComponentAdapter will instantiate a new object for each call to <see cref="PicoContainer.IComponentAdapter.ComponentInstance"/>
	/// That means that
	/// when used with a PicoContainer, getComponentInstance will return a new
	/// object each time.
	/// </summary>
	public abstract class InstantiatingComponentAdapter : AbstractComponentAdapter
	{
		internal IParameter[] parameters;

		/// <summary>
		/// Constructor
		/// </summary>
		/// <param name="componentKey">The component's key</param>
		/// <param name="componentImplementation">The component implementing type</param>
		/// <param name="parameters">Parameters used to initialize the component</param>
		public InstantiatingComponentAdapter(object componentKey, Type componentImplementation, IParameter[] parameters) : base(componentKey, componentImplementation)
		{
			this.parameters = parameters;
		}

		/// <summary>
		/// Gets the component instance. 
		/// </summary>
		/// <returns>a component instance</returns>
		public override object ComponentInstance
		{
			get
			{
				ArrayList dependencyAdapterList = new ArrayList();
				object instance = InstantiateComponent(dependencyAdapterList);

				// Now, track the instantiation order
				foreach (IComponentAdapter dependencyAdapter in dependencyAdapterList)
				{
					Container.AddOrderedComponentAdapter(dependencyAdapter);
				}
				return instance;
			}
		}

		/// <summary>
		/// Creates default parameters if no parameters are passed in.
		/// </summary>
		/// <param name="parameters">The types of the required parameters</param>
		/// <returns>The default parameters</returns>
		protected IParameter[] CreateDefaultParameters(Type[] parameters)
		{
			IParameter[] componentParameters = new IParameter[parameters.Length];
			for (int i = 0; i < parameters.Length; i++)
			{
				if (typeof (IPicoContainer).IsAssignableFrom(parameters[i]))
				{
					componentParameters[i] = new ConstantParameter(Container);
				}
				else
				{
					componentParameters[i] = new ComponentParameter();
				}
			}
			return componentParameters;
		}

		/// <summary>
		/// Instantiate the object. 
		/// </summary>
		/// <param name="adapterDependencies">This list is filled with the dependent adapters of the instance.</param>
		/// <returns>Returns the new instance.</returns>
		protected abstract object InstantiateComponent(ArrayList adapterDependencies);

	}
}