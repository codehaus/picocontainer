using System;
using NUnit.Framework;

namespace NanoContainer.Test.TestModel
{
	/// <summary>
	/// Summary description for DefaultWebServer.
	/// </summary>
	public class DefaultWebServer : WebServer
	{
		public DefaultWebServer(WebServerConfig wsc)
		{
      Assert.IsNotNull(wsc.getHost());
      Assert.IsTrue(wsc.getPort()>0);
		}
	}
}
