using System;
using System.Collections;
using System.Reflection;
using NanoContainer.IntegrationKit;
using PicoContainer;
using PicoContainer.Defaults;

namespace NanoContainer.Attributes
{
	public class AttributeBasedContainerBuilder : LifeCycleContainerBuilder
	{
		private AssemblyUtil assemblyUtil = null;
		private Type registerAttributeType = typeof(RegisterWithContainerAttribute);
		
		public AttributeBasedContainerBuilder()
		{
			assemblyUtil = new AssemblyUtil();
		}

		protected IList FindTypesToRegister(IList assemblies)
		{
			IList registerTypes = new ArrayList();

			foreach(string assembly in assemblies)
			{
				Type[] types = assemblyUtil.GetTypes(Assembly.LoadFrom(assembly), registerAttributeType);

				foreach(Type type in types)
				{
					registerTypes.Add(type);
				}
			}

			return registerTypes;
		}

		protected override IMutablePicoContainer CreateContainer(IPicoContainer parent, IList assemblies)
		{
			return new DefaultPicoContainer(parent);
		}

		protected override void ComposeContainer(IMutablePicoContainer container, IList assemblies)
		{
			IList registerTypes = FindTypesToRegister(assemblies);
			foreach(Type type in registerTypes)
			{
				IComponentAdapter componentAdapter = null;
				RegisterWithContainerAttribute attribute = 
					type.GetCustomAttributes(registerAttributeType, true)[0] as RegisterWithContainerAttribute;
				if(attribute.ComponentAdapterType == ComponentAdapterType.Custom)
				{
					componentAdapter = BuildCustomComponentAdapter(attribute, type);
				}
				else
				{
					componentAdapter = BuildComponentAdapter(attribute, type);
				}
				container.RegisterComponent(componentAdapter);
			}
		}

		private IComponentAdapter BuildComponentAdapter(RegisterWithContainerAttribute attribute, Type type)
		{
			IComponentAdapter result;
			if(attribute.DependencyInjection == DependencyInjectionType.Constructor)
			{
				result = BuildConstructorInjectionAdapter(attribute, type);
			}
			else
			{
				result = BuildSetterInjectionAdapter(attribute, type);
			}
			if(attribute.ComponentAdapterType == ComponentAdapterType.Caching)
			{
				result = new CachingComponentAdapter(result);
			}
			return result;
		}

		private IComponentAdapter BuildCustomComponentAdapter(RegisterWithContainerAttribute attribute, Type type)
		{
			return (IComponentAdapter)Activator.CreateInstance(attribute.ComponentAdapter, new object[] {type});
		}

		private IComponentAdapter BuildSetterInjectionAdapter(RegisterWithContainerAttribute attribute, Type type)
		{
			if(attribute.Key == null)
			{
				return new SetterInjectionComponentAdapter(type);
			}
			else
			{
				return new ConstructorInjectionComponentAdapter(attribute.Key, type);
			}
		}

		private IComponentAdapter BuildConstructorInjectionAdapter(RegisterWithContainerAttribute attribute, Type type)
		{
			if(attribute.Key == null)
			{
				return new ConstructorInjectionComponentAdapter(type);
			}
			else
			{
				return new ConstructorInjectionComponentAdapter(attribute.Key, type);
			}
		}
	}
}
