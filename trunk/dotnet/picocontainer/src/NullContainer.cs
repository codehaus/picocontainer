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
	public class NullContainer : IContainer 
	{
		public bool HasComponent(Type compType) 
		{
			return false;
		}

		public object GetComponent(Type compType) 
		{
			return null;
		}

		public object[] Components
		{
			get { return new object[0]; }
		}

		public Type[] ComponentTypes 
		{
			get { return new Type[0]; }
		}
	}
}

