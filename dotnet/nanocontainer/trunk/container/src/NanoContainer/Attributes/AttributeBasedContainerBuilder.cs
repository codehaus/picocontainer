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
				RegisterWithContainerAttribute attribute = 
					type.GetCustomAttributes(registerAttributeType, true)[0] as RegisterWithContainerAttribute;
				
				if(attribute.Key == null)
				{
					container.RegisterComponentImplementation(type);
				}
				else
				{
					container.RegisterComponentImplementation(attribute.Key, type);
				}
			}
		}
	}
}
