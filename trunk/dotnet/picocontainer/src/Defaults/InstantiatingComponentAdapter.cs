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
using System.Reflection;
using PicoContainer.Utils;

namespace PicoContainer.Defaults
{
	/// <summary>
	/// This ComponentAdapter will instantiate a new object for each call to 
	/// <see cref="PicoContainer.IComponentAdapter.ComponentInstance"/>
	/// That means that
	/// when used with a PicoContainer, getComponentInstance will return a new
	/// object each time.
	/// </summary>
	[Serializable]
	public abstract class InstantiatingComponentAdapter : AbstractComponentAdapter
	{
		[NonSerialized] internal VerifyingGuard guard;
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
				return GetComponentInstance(Container);
			}
		}

		public abstract override object GetComponentInstance(IPicoContainer container);

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
				componentParameters[i] = ComponentParameter.DEFAULT;
			}
			return componentParameters;
		}

		/// <summary>
		/// 
		/// </summary>
		[Serializable]
		internal class VerifyingGuard : ThreadStaticCyclicDependencyGuard
		{
			protected IPicoContainer guardedContainer;
			private InstantiatingComponentAdapter ica;

			public VerifyingGuard(InstantiatingComponentAdapter ica, IPicoContainer container)
			{
				this.ica = ica;
				this.guardedContainer = container;
			}

			public override object Run()
			{
				ConstructorInfo constructor = ica.GetGreediestSatisfiableConstructor(guardedContainer);
				Type[] parameterTypes = TypeUtils.GetParameterTypes(constructor.GetParameters());
				IParameter[] currentParameters = ica.parameters != null ? ica.parameters : ica.CreateDefaultParameters(parameterTypes);
				for (int i = 0; i < currentParameters.Length; i++)
				{
					currentParameters[i].Verify(guardedContainer, ica, parameterTypes[i]);
				}

				return null;
			}
		}

		public override void Verify(IPicoContainer container)
		{
			if (guard == null) 
			{
				guard = new VerifyingGuard(this, container);
			}

			guard.Observe(ComponentImplementation);
		}

		protected abstract ConstructorInfo GetGreediestSatisfiableConstructor(IPicoContainer container);

	}
}