using System;

namespace NanoContainer.Test.TestModel
{
	/// <summary>
	/// Summary description for DefaultWebServerConfig.
	/// </summary>
    public class DefaultWebServerConfig : WebServerConfig {

                                          private int port = 80;

      public String getHost() {
        return "*";
      }

      public int getPort() {
        return port;
      }

      public void setPort(int port) {
        this.port = port;
      }

    }
  }
