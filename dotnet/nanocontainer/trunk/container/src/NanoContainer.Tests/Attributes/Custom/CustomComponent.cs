using NanoContainer.Attributes;

namespace NanoContainer.Tests.Attributes.Custom
{
	[RegisterWithContainer(ComponentAdapterType.Custom, 
		 ComponentAdapter=typeof(TestCustomComponentAdapter))]
	public class CustomComponent
	{
	}
}
