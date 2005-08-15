using NanoContainer.Attributes;
using NanoContainer.Test.TestModel;

namespace NanoContainer.Tests.Attributes
{
	[RegisterWithContainer(ComponentAdapterType.NonCaching)]
	public class NonCachingCtorBasedComponent : ITestComponent
	{
		private WebServer ws;

		public NonCachingCtorBasedComponent(WebServer ws)
		{
			this.ws = ws;
		}

		public WebServer WebServer
		{
			get { return ws; }
		}
	}
}