/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 * C# port by Maarten Grootendorst                                           *
 *****************************************************************************/

using System;

namespace PicoContainer.Extras
{
	
	public class DecoratingComponentAdapterFactory : ComponentAdapterFactory
	{
		private ComponentAdapterFactory theDelegate;
		
		public DecoratingComponentAdapterFactory(ComponentAdapterFactory theDelegate)
		{
			this.theDelegate= theDelegate;
		}
		
		public virtual ComponentAdapter CreateComponentAdapter(object componentKey, System.Type componentImplementation, Parameter[] parameters)
		{
			return theDelegate.CreateComponentAdapter(componentKey, componentImplementation, parameters);
		}
	}
}