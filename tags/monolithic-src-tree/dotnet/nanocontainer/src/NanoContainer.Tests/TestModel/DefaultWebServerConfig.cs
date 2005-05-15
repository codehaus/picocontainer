using System;

namespace NanoContainer.Test.TestModel
{
	public class DefaultWebServerConfig : WebServerConfig
	{
		private int port = 80;

		public String Host
		{
			get { return "*"; }
		}

		public int Port
		{
			get { return port; }
			set { port = value; }
		}

	}
}