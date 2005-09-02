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
		private Type picoParameterAttributeType = typeof(PicoParameterAttribute);
		
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

		protected IComponentAdapter BuildComponentAdapter(RegisterWithContainerAttribute attribute, Type type)
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

		private IParameter[] createParametersArray(Type type)
		{
			object[] attributes = type.GetCustomAttributes(picoParameterAttributeType, true);

			if(attributes == null || attributes.Length == 0)
			{
				return null;
			}

			IParameter[] parameters = new IParameter[attributes.Length];

			foreach(PicoParameterAttribute attribute in attributes)
			{
				if(attribute is ConstantParameterAttribute)
				{
					parameters[attribute.Index] = new ConstantParameter(attribute.Value);
				}
				else
				{
					// TODO mward: need to support more ComponentParameter ctor types
					parameters[attribute.Index] = new ComponentParameter(attribute.Value);
				}
			}
			
			return parameters;
		}

		private IComponentAdapter BuildSetterInjectionAdapter(RegisterWithContainerAttribute attribute, Type type)
		{
			IParameter[] parameters = createParametersArray(type);

			if(attribute.Key == null)
			{
				if(parameters == null)
				{
					return new SetterInjectionComponentAdapter(type);
				}

				return new SetterInjectionComponentAdapter(type, parameters);
			}
			else if(parameters == null)
			{
				return new SetterInjectionComponentAdapter(attribute.Key, type);	
			}

			return new SetterInjectionComponentAdapter(attribute.Key, type, parameters);
			
		}

		private IComponentAdapter BuildConstructorInjectionAdapter(RegisterWithContainerAttribute attribute, Type type)
		{
			IParameter[] parameters = createParametersArray(type);

			if(attribute.Key == null)
			{
				if(parameters == null)
				{
					return new ConstructorInjectionComponentAdapter(type);	
				}

				return new ConstructorInjectionComponentAdapter(type, type, parameters);
				
			}
			else if(parameters == null)
			{
				return new ConstructorInjectionComponentAdapter(attribute.Key, type);	
			}

			return new ConstructorInjectionComponentAdapter(attribute.Key, type, parameters);
			
		}
	}
}
