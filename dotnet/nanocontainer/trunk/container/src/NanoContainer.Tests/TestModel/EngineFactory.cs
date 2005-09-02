using NanoContainer.Attributes;

namespace NanoContainer.Tests.TestModel
{
	[RegisterWithContainer]
	public class EngineFactory
	{
		private IEngine[] engines;

		public EngineFactory(IEngine[] engines)
		{
			this.engines = engines;
		}

		public IEngine[] Engines
		{
			get { return engines; }
		}
	}
}
