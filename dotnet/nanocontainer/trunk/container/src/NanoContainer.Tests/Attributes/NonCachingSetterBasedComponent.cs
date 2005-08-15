using NanoContainer.Attributes;
using NanoContainer.Test.TestModel;

namespace NanoContainer.Tests.Attributes
{
	[RegisterWithContainer(ComponentAdapterType.NonCaching,
		DependencyInjection=DependencyInjectionType.Setter)]
	public class NonCachingSetterBasedComponent : ITestComponent
	{
		private WebServer ws;

		public NonCachingSetterBasedComponent()
		{
		}

		public WebServer WebServer
		{
			get { return ws; }
			set { ws = value; }
		}
	}
}