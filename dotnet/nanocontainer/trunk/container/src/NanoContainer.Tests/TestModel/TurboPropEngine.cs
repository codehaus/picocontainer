using NanoContainer.Attributes;

namespace NanoContainer.Tests.TestModel
{
	[RegisterWithContainer("turboprop")]
	public class TurboPropEngine : IEngine
	{
		public string Name
		{
			get { return "Turbo Propeller"; }
		}
	}
}
