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
using System.Reflection;
using System.Collections;

namespace PicoContainer.Defaults
{
	/// <summary>
	/// Instantiates components using empty constructors and Setter Injection
	/// <remarks>
	/// <a href="http://docs.codehaus.org/display/PICO/Setter+Injection">Setter Injection</a>.
	/// For easy setting of primitive properties, also <see cref="BeanPropertyComponentAdapter"/>.
	/// Note that this class doesn't cache instances. If you want caching,
	/// use a <see cref="CachingComponentAdapter"/> around this one.
	/// </remarks>
	/// </summary>
	[Serializable]
	public class SetterInjectionComponentAdapter : DecoratingComponentAdapter
	{
		private ArrayList setters;
		private readonly IParameter[] parameters;

		/// <summary>
		/// Constructor
		/// </summary>
		/// <param name="theDelegate">The component adapter to decorate</param>
		public SetterInjectionComponentAdapter(IComponentAdapter theDelegate) : this(theDelegate, null)
		{
		}

		public SetterInjectionComponentAdapter(IComponentAdapter theDelegate, IParameter[] parameters) : base(theDelegate)
		{
			this.parameters = parameters;
		}

		/// <summary>
		/// Gets the component instance. This method will usually create
		/// a new instance for each call.
		/// </summary>
		/// <remarks>
		/// Not all ComponentAdapters return a new instance for each call an example is the <see cref="PicoContainer.Defaults.CachingComponentAdapter"/>.<BR/>
		/// </remarks>
		/// <returns>a component instance</returns>
		/// <exception cref="PicoContainer.PicoInitializationException">if the component could not be instantiated.</exception>    
		public override object ComponentInstance
		{
			get
			{
				Object result = base.ComponentInstance;

				SetDependencies(result);

				return result;
			}
		}

		private void SetDependencies(object componentInstance)
		{
			IList unsatisfiableDependencyTypes = new ArrayList();
			MethodInfo[] setters = GetSetters();
			for (int i = 0; i < setters.Length; i++)
			{
				MethodInfo setter = setters[i];
				Type dependencyType = setter.GetParameters()[0].ParameterType;
				object dependency = GetDependencyInstance(dependencyType, unsatisfiableDependencyTypes);
				if (dependency != null)
				{
					try
					{
						setter.Invoke(componentInstance, new object[] {dependency});
					}
					catch (Exception e)
					{
						throw new PicoIntrospectionException(e);
					}
				}
				else
				{
					unsatisfiableDependencyTypes.Add(dependencyType);
				}
			}
			if (unsatisfiableDependencyTypes.Count != 0)
			{
				throw new UnsatisfiableDependenciesException(this, unsatisfiableDependencyTypes);
			}
		}

		private object GetDependencyInstance(Type type, IList unsatisfiableDependencyTypes)
		{
			if (parameters != null)
			{
				return GetDependencyInstanceFromParameters(type, unsatisfiableDependencyTypes);
			}

			IComponentAdapter adapterDependency = Container.GetComponentAdapterOfType(type);
			if (adapterDependency != null)
			{
				return adapterDependency.ComponentInstance;
			}
			else
			{
				unsatisfiableDependencyTypes.Add(type);
				return null;
			}
		}

		private object GetDependencyInstanceFromParameters(Type type, IList unsatisfiableDependencyTypes)
		{
			for (int i = 0; i < parameters.Length; i++)
			{
				IParameter parameter = parameters[i];
				IComponentAdapter adapter = parameter.ResolveAdapter(Container, type);
				if (adapter != null)
				{
					return adapter.ComponentInstance;
				}
			}
			unsatisfiableDependencyTypes.Add(type);
			return null;
		}

		private MethodInfo[] GetSetters()
		{
			if (setters == null)
			{
				setters = new ArrayList();
				PropertyInfo[] properties = ComponentImplementation.GetProperties();
				foreach (PropertyInfo property in properties)
				{
					MethodInfo method = property.GetSetMethod();
					if (method != null)
					{
						setters.Add(method);
					}
				}
			}

			return (MethodInfo[]) setters.ToArray(typeof (MethodInfo));
		}

	}
}