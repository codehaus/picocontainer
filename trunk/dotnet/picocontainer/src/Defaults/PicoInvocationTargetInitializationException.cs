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

namespace PicoContainer.Defaults
{
	
  [Serializable]
  public class PicoInvocationTargetInitializationException:PicoInstantiationException
	{

		public PicoInvocationTargetInitializationException(System.Exception cause):base(cause,"InvocationTargetException: " + cause.GetType().FullName + " " + cause.Message)
		{
		}
	}
}