using NanoContainer.Attributes;
using NanoContainer.Test.TestModel;

namespace NanoContainer.Tests.Attributes
{
	[RegisterWithContainer()]
	public class CachingCtorBasedComponent : ITestComponent
	{
		private WebServer ws;

		public CachingCtorBasedComponent(WebServer ws)
		{
			this.ws = ws;
		}

		public WebServer WebServer
		{
			get { return ws; }
		}
	}
}