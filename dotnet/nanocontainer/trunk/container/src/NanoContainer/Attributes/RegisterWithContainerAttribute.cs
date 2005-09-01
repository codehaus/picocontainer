using System;

namespace NanoContainer.Attributes
{
	[AttributeUsage(AttributeTargets.Class)]
	public class RegisterWithContainerAttribute : Attribute
	{
		private object key;
		private ComponentAdapterType componentAdapterType = ComponentAdapterType.Caching;
		private DependencyInjectionType dependencyInjectionType = DependencyInjectionType.Constructor;
		private Type componentAdapter;

		public RegisterWithContainerAttribute()
		{ 
		}

		public RegisterWithContainerAttribute(object key)
		{ 
			this.key = key;
		}

		public RegisterWithContainerAttribute(ComponentAdapterType componentAdapterType) 
		{
			this.componentAdapterType = componentAdapterType;
		}

		public RegisterWithContainerAttribute(object key, ComponentAdapterType componentAdapterType) 
			: this(componentAdapterType)
		{
			this.key = key;
		}

		public object Key
		{
			get { return key; }
		}

		public ComponentAdapterType ComponentAdapterType
		{
			get { return componentAdapterType; }
			set { componentAdapterType = value; }
		}

		public Type ComponentAdapter
		{
			get { return componentAdapter; }
			set { componentAdapter = value; }
		}

		public DependencyInjectionType DependencyInjection
		{
			get { return dependencyInjectionType; }
			set { dependencyInjectionType = value; }
		}
	}
}
