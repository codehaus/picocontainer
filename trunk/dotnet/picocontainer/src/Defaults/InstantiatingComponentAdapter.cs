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
		[NonSerialized] protected DefaultVerifyingGuard verifyingGuard;
		protected IParameter[] parameters;
		/// <summary>
		/// Flag indicating instanciation of non-public classes.
		/// </summary>
		protected bool allowNonPublicClasses;

		/// <summary>
		/// Constructor
		/// </summary>
		/// <param name="componentKey">The component's key</param>
		/// <param name="componentImplementation">The component implementing type</param>
		/// <param name="parameters">Parameters used to initialize the component</param>
		/// <param name="allowNonPublicClasses">flag to allow instantiation of non-public classes.</param>
		public InstantiatingComponentAdapter(object componentKey, Type componentImplementation, IParameter[] parameters, bool allowNonPublicClasses) 
			: base(componentKey, componentImplementation)
		{
			CheckConcrete();

			this.parameters = parameters;
			this.allowNonPublicClasses = allowNonPublicClasses;
		}

		private void CheckConcrete()
		{
			// Assert that the component class is concrete.
			if (ComponentImplementation.IsAbstract || ComponentImplementation.IsInterface)
			{
				throw new NotConcreteRegistrationException(ComponentImplementation);
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
		protected class DefaultVerifyingGuard : ThreadStaticCyclicDependencyGuard
		{
			protected IPicoContainer guardedContainer;
			protected InstantiatingComponentAdapter ica;

			public DefaultVerifyingGuard(InstantiatingComponentAdapter ica, IPicoContainer guardedContainer)
			{
				this.ica = ica;
				this.guardedContainer = guardedContainer;
			}

			// TODO move to constructor injection adapter ... mward
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

		protected abstract ConstructorInfo GetGreediestSatisfiableConstructor(IPicoContainer container);

		public override void Verify(IPicoContainer container)
		{
			if (verifyingGuard == null) 
			{
				verifyingGuard = new DefaultVerifyingGuard(this, container);
			}

			verifyingGuard.Observe(ComponentImplementation);
		}

	}
}