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
using System.Collections;

using PicoContainer;

namespace PicoContainer.Test
{
	public class OverriddenStartableLifecycleManager : IStartableLifecycleManager 
	{
		private ArrayList started = new ArrayList();
		private ArrayList stopped = new ArrayList();

		public void StartComponent(object component) 
		{
			started.Add(component.GetType());
		}

		public void StopComponent(object component) 
		{
			stopped.Add(component.GetType());
		}

		public void DisposeOfComponent(object component) 
		{        
		}

		public ArrayList getStarted() 
		{
			return started;
		}

		public ArrayList getStopped() 
		{
			return stopped;
		}
	}
}
