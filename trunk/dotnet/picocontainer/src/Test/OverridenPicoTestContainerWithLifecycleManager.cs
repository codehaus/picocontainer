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

using PicoContainer;
using PicoContainer.Test.TestModel;

namespace PicoContainer.Test
{
	public class OveriddenPicoTestContainerWithLifecycleManager : HierarchicalPicoContainer
	{
		private Wilma wilma;

		public OveriddenPicoTestContainerWithLifecycleManager(Wilma wilma, IStartableLifecycleManager slm) 
			: base(new NullContainer(), slm, new DefaultComponentFactory())
		{
			this.wilma = wilma;
		}

		protected override object MakeComponentInstance(Type compType, ConstructorInfo constructor, Object[] args) 
		{
			if (constructor.DeclaringType == typeof(WilmaImpl)) 
			{
				return wilma;
			}
			return base.MakeComponentInstance(compType, constructor, args);
		}
	}
}
