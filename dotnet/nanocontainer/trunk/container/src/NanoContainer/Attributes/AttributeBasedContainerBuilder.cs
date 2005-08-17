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

		protected override IMutablePicoContainer CreateContainer(IPicoContainer parent, object assemblyScope)
		{
			return new DefaultPicoContainer(parent);
		}

		protected override void ComposeContainer(IMutablePicoContainer container, object assemblyScope)
		{
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
			return (IComponentAdapter)Activator.CreateInstance(attribute.ComponentAdapter,new object[] {type});
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
