using NanoContainer.Attributes;
using NanoContainer.Test.TestModel;

namespace NanoContainer.Tests.Attributes
{
	[RegisterWithContainer(DependencyInjection=DependencyInjectionType.Setter)]
	public class CachingSetterBasedComponent : ITestComponent
	{
		private WebServer ws;

		public CachingSetterBasedComponent()
		{
		}

		public WebServer WebServer
		{
			get { return ws; }
			set { ws = value; }
		}
	}
}