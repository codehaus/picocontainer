using NanoContainer.Attributes;

namespace NanoContainer.Tests.TestModel
{
	[RegisterWithContainer,
		ConstantParameter(0, "Boeing"),
		ComponentParameter(1, "jet")]
	public class Airplane
	{
		private string manufacturer;
		private IEngine engine;

		public Airplane(string manufacturer, IEngine engine)
		{
			this.manufacturer = manufacturer;
			this.engine = engine;
		}

		public string Manufacturer
		{
			get { return manufacturer; }
		}

		public IEngine Engine
		{
			get { return engine; }
		}
	}
}