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
using System.Reflection;

namespace PicoContainer
{
	public class ReflectionUsingLifecycleManager : IStartableLifecycleManager 
	{

		private static readonly Type[] NOPARMS = new Type[0];
		private static readonly object[] NOARGS = new object[0];

		public void StartComponent(object component) 
		{
			try 
			{
				InvokeMethodByName(component, "Start");
			} 
			catch (TargetInvocationException e) 
			{
				throw new PicoInvocationTargetStartException(e.GetBaseException());
			}
		}

		public void StopComponent(object component) 
		{
			try 
			{
				InvokeMethodByName(component, "Stop");
			} 
			catch (TargetInvocationException e) 
			{
				throw new PicoInvocationTargetStopException(e.GetBaseException());
			}
		}

		public void DisposeOfComponent(object component) 
		{
			try 
			{
				InvokeMethodByName(component, "Dispose");
			} 
			catch (TargetInvocationException e) 
			{
				throw new PicoInvocationTargetDisposalException(e.GetBaseException());
			}
		}

		protected void InvokeMethodByName(object component, string methodName) 
		{
			try 
			{
				MethodInfo method = component.GetType().GetMethod(methodName, NOPARMS);
				if (method == null) return;
				method.Invoke(component, NOARGS);
			} 
			catch (MethodAccessException) 
			{
				// fine.
			}
		}


	}
}
