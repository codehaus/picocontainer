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
using PicoContainer;
using PicoContainer.Defaults;

namespace PicoContainer.Extras
{
	
	public class InvokingComponentAdapterFactory:DecoratingComponentAdapterFactory
	{
		private string methodName;
		private System.Type[] parameterTypes;
		private object[] arguments;
		
		public InvokingComponentAdapterFactory(ComponentAdapterFactory theDelegate, string methodName, System.Type[] parameterTypes, object[] arguments):base(theDelegate)
		{
			this.methodName = methodName;
			this.parameterTypes = parameterTypes;
			this.arguments = arguments;
		}
		
		public override ComponentAdapter CreateComponentAdapter(object componentKey, System.Type componentImplementation, Parameter[] parameters)
		{
			return new Adapter(this,base.CreateComponentAdapter(componentKey, componentImplementation, parameters));
		}
		

		public class Adapter:DecoratingComponentAdapter
		{
			private class AnonymousClassPicoInitializationException : Exception
			{
				public AnonymousClassPicoInitializationException(System.ArgumentException e, Adapter enclosingInstance)
				{
					InitBlock(e, enclosingInstance);
				}
				private void  InitBlock(System.ArgumentException e, Adapter enclosingInstance)
				{
					this.e = e;
					this.enclosingInstance = enclosingInstance;
				}

				private System.ArgumentException e;
				private Adapter enclosingInstance;
				public Adapter EnclosingInstance
				{
					get
					{
						return enclosingInstance;
					}
					
				}
				public virtual string getMessage()
				{
					return "Illegal argument:" + e.Message;
				}
			}
			private void  InitBlock(InvokingComponentAdapterFactory enclosingInstance)
			{
				this.enclosingInstance = enclosingInstance;
			}
			private InvokingComponentAdapterFactory enclosingInstance;
			virtual public object InvocationResult
			{
				get
				{
					return invocationResult;
				}
				
			}
			public InvokingComponentAdapterFactory EnclosingInstance
			{
				get
				{
					return enclosingInstance;
				}
				
			}
			private System.Reflection.MethodInfo method;
			private object componentInstance = null;
			
			private object invocationResult;
			
			public Adapter(InvokingComponentAdapterFactory enclosingInstance, ComponentAdapter theDelegate):base(theDelegate)
			{
				InitBlock(enclosingInstance);
				try
				{
					method = theDelegate.ComponentImplementation.GetMethod(EnclosingInstance.methodName, EnclosingInstance.parameterTypes);
				}
				catch (System.MethodAccessException)
				{
					method = null;
				}
				catch (System.Security.SecurityException)
				{
					method = null;
				}
			}
			
			public override object GetComponentInstance(MutablePicoContainer picoContainer)
			{
				if (componentInstance == null)
				{
					componentInstance = base.GetComponentInstance(picoContainer);
					
					if (method != null)
					{
						try
						{
							invocationResult = method.Invoke(componentInstance, (object[]) EnclosingInstance.arguments);
						}
						catch (System.UnauthorizedAccessException e)
						{
							throw new PicoInvocationTargetInitializationException(e);
						}
						catch (System.ArgumentException e)
						{
							throw new AnonymousClassPicoInitializationException(e, this);
						}
						catch (System.Reflection.TargetInvocationException e)
						{
							throw new PicoInvocationTargetInitializationException(e.GetBaseException());
						}
					}
				}
				return componentInstance;
			}
		}
	}
}