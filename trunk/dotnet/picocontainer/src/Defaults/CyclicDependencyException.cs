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
using System.Runtime.Serialization;
using PicoContainer;

namespace PicoContainer.Defaults
{
	[Serializable]
	public class CyclicDependencyException : PicoInitializationException
	{
		private readonly Type[] dependencies;

		public CyclicDependencyException()
		{
		}

		public CyclicDependencyException(Exception ex) : base(ex)
		{
		}

		public CyclicDependencyException(string message) : base(message)
		{
		}

		public CyclicDependencyException(string message, Exception ex) : base(message, ex)
		{
		}

		public CyclicDependencyException(Type[] dependencies)
		{
			this.dependencies = dependencies;
		}

		protected CyclicDependencyException(SerializationInfo info, StreamingContext context) : base(info, context)
		{
		}

		public Type[] Dependencies
		{
			get { return dependencies; }
		}

		public override String Message
		{
			get { return "Cyclic dependency: (" + Utils.StringUtils.ArrayToString(dependencies) + ")"; }
		}
	}
}