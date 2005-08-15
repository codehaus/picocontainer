using System;
using System.Reflection;
using NanoContainer.IntegrationKit;
using PicoContainer;
using PicoContainer.Defaults;

namespace NanoContainer.Attributes
{
	public class AttributeBasedContainerBuilder : LifeCycleContainerBuilder
	{
		private Type registerAttributeType = typeof(RegisterWithContainerAttribute);
		private Type[] registerTypes;

		public AttributeBasedContainerBuilder(Assembly assembly)
		{
			AssemblyUtil assemblyUtil = new AssemblyUtil();
			this.registerTypes = assemblyUtil.GetTypes(assembly, registerAttributeType);
		}

		protected override IMutablePicoContainer CreateContainer(IPicoContainer parent, Object assemblyScope)
		{
			return new DefaultPicoContainer(parent);
		}

		protected override void ComposeContainer(IMutablePicoContainer container, Object assemblyScope)
		{
			foreach(Type type in registerTypes)
			{
				IComponentAdapter componentAdapter = null;
				RegisterWithContainerAttribute attribute = 
					type.GetCustomAttributes(registerAttributeType, true)[0] as RegisterWithContainerAttribute;
				
				if(attribute.DependencyInjection == DependencyInjectionType.Constructor)
				{
					componentAdapter = BuildConstructorInjectionAdapter(attribute, type);
				}
				else
				{
					componentAdapter = BuildSetterInjectionAdapter(attribute, type);
				}

				if(attribute.ComponentAdapterType == ComponentAdapterType.Caching)
				{
					componentAdapter = new CachingComponentAdapter(componentAdapter);
				}

				container.RegisterComponent(componentAdapter);
			}
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
