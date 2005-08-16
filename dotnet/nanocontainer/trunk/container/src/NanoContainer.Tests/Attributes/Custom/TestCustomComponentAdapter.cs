using System;
using PicoContainer.Defaults;

namespace NanoContainer.Tests.Attributes.Custom
{
	public class TestCustomComponentAdapter : CachingComponentAdapter
	{
		public TestCustomComponentAdapter(Type type) : base(new ConstructorInjectionComponentAdapter(type))
		{
		}
	}
}
