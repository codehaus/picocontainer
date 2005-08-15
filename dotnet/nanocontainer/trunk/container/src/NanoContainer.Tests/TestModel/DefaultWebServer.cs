using System.Text;
using NanoContainer.Attributes;
using NUnit.Framework;

namespace NanoContainer.Test.TestModel
{
	[RegisterWithContainer("webserver")]
	public class DefaultWebServer : WebServer
	{
		public DefaultWebServer(WebServerConfig wsc)
		{
			Assert.IsNotNull(wsc.Host);
			Assert.IsTrue(wsc.Port > 0);
		}

		public DefaultWebServer(WebServerConfig wsc, StringBuilder sb)
		{
			Assert.IsTrue(wsc.Port > 0, "No port number specified");
			Assert.IsNotNull("No host name specified", wsc.Host);
			sb.Append("-WebServer:" + wsc.Host + ":" + wsc.Port);
		}
	}
}