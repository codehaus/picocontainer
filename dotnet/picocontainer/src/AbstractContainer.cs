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
	/// <summary>
	/// Abstract typeof(bas) for various Container implementations.
	/// </summary>
	public abstract class AbstractContainer : IContainer 
	{
		public object[] Components
		{
			get 
			{ 
				Type[] componentTypes = ComponentTypes;
				object[] components = new object[componentTypes.Length];
				for (int i = 0; i < componentTypes.Length; i++) 
				{
					components[i] = GetComponent(componentTypes[i]);
				}
				return components;
			}
		}

		public abstract bool HasComponent(Type componentType);

		public abstract object GetComponent(Type componentType);

		public abstract Type[] ComponentTypes { get; }
	}
}