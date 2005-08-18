using System.IO;
using System.Text;

namespace NanoContainer.Tests.Script
{
	public class ScriptFixture
	{
		/// <summary>
		/// Helper method for tests
		/// </summary>
		public static StreamReader BuildStreamReader(string code)
		{
			return new StreamReader(BuildStream(code));
		}

		public static Stream BuildStream(string code)
		{
			byte[] inputByteArray = Encoding.UTF8.GetBytes(code);
			return new MemoryStream(inputByteArray);
		}
	}
}
