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
using System.Reflection;

using NUnit.Framework;

using PicoContainer;
using PicoContainer.Test.TestModel;

namespace PicoContainer.Test
{
	public class OveriddenPicoTestContainer : HierarchicalPicoContainer
	{
		private Wilma wilma;
		private ArrayList started = new ArrayList();
		private ArrayList stopped = new ArrayList();

		public OveriddenPicoTestContainer(Wilma wilma) 
			: base(new NullContainer(), new NullStartableLifecycleManager(), new DefaultComponentFactory())
		{
			this.wilma = wilma;
		}

		protected override object MakeComponentInstance(Type compType, ConstructorInfo constructor, object[] args)
		{
			if (constructor.DeclaringType == typeof(WilmaImpl)) 
			{
				return wilma;
			}
			return base.MakeComponentInstance(compType, constructor, args);
		}

		protected void StartComponent(object component) 
		{
			started.Add(component.GetType());
		}

		protected void StopComponent(object component) 
		{
			stopped.Add(component.GetType());
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
