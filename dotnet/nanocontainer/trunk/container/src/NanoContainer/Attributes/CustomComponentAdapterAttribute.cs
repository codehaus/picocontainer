using System;
using PicoContainer;

namespace NanoContainer.Attributes
{
	public class CustomComponentAdapterAttribute : Attribute
	{
		private Type type;

		public CustomComponentAdapterAttribute(Type type)
		{
			if(type is IComponentAdapter)
			{
				this.type = type;
			}
			else
			{
				throw new ArgumentException("Type must inherit from" + typeof(IComponentAdapter).FullName);
			}
		}

		public Type Type
		{
			get { return type; }
		}
	}
}
