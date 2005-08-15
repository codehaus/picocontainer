using System;

namespace NanoContainer.Attributes
{
	public class RegisterWithContainerAttribute : Attribute
	{
		private object key;
		private ComponentAdapterType componentAdapterType;

		public RegisterWithContainerAttribute()
		{ 
			componentAdapterType = ComponentAdapterType.CACHING;
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
		}
	}
}
