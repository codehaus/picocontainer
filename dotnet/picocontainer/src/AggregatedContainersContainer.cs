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

namespace PicoContainer
{

	public class AggregatedContainersContainer : AbstractContainer 
	{
		private readonly IContainer[] containers;

		public AggregatedContainersContainer(IContainer[] containers) 
		{
			PicoNullReferenceException.AssertNotNull("containers can't be null", containers);
			for (int i = 0; i < containers.Length; i++) 
			{
				PicoNullReferenceException.AssertNotNull("Container at position " + i + " was null", containers[i]);
			}
			this.containers = containers;
		}

		public class Filter : AggregatedContainersContainer 
		{
			public readonly IContainer Subject;

			public Filter(IContainer container) : base(new IContainer[]{container})
			{
				Subject = container;
			}
		}

		public override bool HasComponent(Type compType) 
		{
			for (int i = 0; i < containers.Length; i++) 
			{
				IContainer container = containers[i];
				if (container.HasComponent(compType)) 
				{
					return true;
				}
			}
			return false;
		}

		public override object GetComponent(Type compType) 
		{
			for (int i = 0; i < containers.Length; i++) 
			{
				IContainer container = containers[i];
				if (container.HasComponent(compType)) 
				{
					return container.GetComponent(compType);
				}
			}
			return null;
		}

		public override Type[] ComponentTypes
		{
			get 
			{
				ArrayList componentTypes = new ArrayList();
				foreach (IContainer container in containers) 
				{
					foreach (Type type in container.ComponentTypes) 
					{
						componentTypes.Add(type);
					}
				}
				return (Type[]) componentTypes.ToArray(typeof(Type));
			}
		}
	}
}