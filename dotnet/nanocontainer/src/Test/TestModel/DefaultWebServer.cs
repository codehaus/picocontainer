using NUnit.Framework;

namespace NanoContainer.Test.TestModel
{
	public class DefaultWebServer : WebServer
	{
		public DefaultWebServer(WebServerConfig wsc)
		{
			Assert.IsNotNull(wsc.Host);
			Assert.IsTrue(wsc.Port > 0);
		}
	}
}