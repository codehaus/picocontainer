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
using System.Runtime.CompilerServices;

namespace PicoContainer.Defaults
{
	public class SynchronizedComponentAdapter : DecoratingComponentAdapter
	{
		public SynchronizedComponentAdapter(IComponentAdapter theDelegate) : base(theDelegate)
		{
		}

		public override object ComponentKey
		{
			[MethodImpl(MethodImplOptions.Synchronized)]
			get { return base.ComponentKey; }
		}

		public override Type ComponentImplementation
		{
			[MethodImpl(MethodImplOptions.Synchronized)]
			get { return base.ComponentImplementation; }
		}

		public override object ComponentInstance
		{
			[MethodImpl(MethodImplOptions.Synchronized)]
			get { return base.ComponentInstance; }
		}

		[MethodImpl(MethodImplOptions.Synchronized)]
		public override void Verify()
		{
			base.Verify();
		}
	}
}