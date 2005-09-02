using NanoContainer.Attributes;

namespace NanoContainer.Tests.TestModel
{
	[RegisterWithContainer("jet")]
	public class JetEngine : IEngine
	{
		public string Name
		{
			get { return "Jet Propelled"; }
		}
	}
}
