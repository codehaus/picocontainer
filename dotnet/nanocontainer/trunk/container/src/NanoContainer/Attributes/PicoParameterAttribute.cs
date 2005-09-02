using System;

namespace NanoContainer.Attributes
{
	[AttributeUsage(AttributeTargets.Class, AllowMultiple=true)]
	public abstract class PicoParameterAttribute : Attribute
	{
		private int index = 0;
		private object value = null;

		public PicoParameterAttribute(int index, object value)
		{
			this.index = index;
			this.value = value;
		}

		public object Value
		{
			get { return this.value; }
			set { this.value = value; }
		}

		public int Index
		{
			get { return index; }
		}
	}
}
