/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 * Ported to .NET by Jeremey Stell-Smith                                     *
 *****************************************************************************/



using System;

namespace PicoContainer
{
	public interface IPicoContainer : ILifecycleContainer 
	{
		/// <summary>
		/// Registers a component. Same as calling RegisterComponent(java.lang.Type, java.lang.Type)
		/// with the same argument.
		/// </summary>
		/// <param name="componentImplementation"></param>
		/// <exception cref="PicoRegistrationException">If a registration problem</exception>
		void RegisterComponent(Type componentImplementation);

		/// <summary>
		/// Alternate way of registering components with additional component type.
		/// </summary>
		/// <param name="componentType"></param>
		/// <param name="componentImplementation"></param>
		/// <exception cref="PicoRegistrationException">If a registration problem</exception>
		void RegisterComponent(Type componentType, Type componentImplementation);

		/// <summary>
		/// Registers a component that is instantiated and configured outside
		/// the container. Useful in cases where pico doesn't have sufficient
		/// knowledge to instantiate a component.
		/// </summary>
		/// <param name="componentType"></param>
		/// <param name="component"></param>
		/// <exception cref="PicoRegistrationException">If a registration problem</exception>
		void RegisterComponent(Type componentType, object component);

		void RegisterComponent(object component);

		void AddParameterToComponent(Type componentType, Type parameter, object arg);
	}
}
