using NanoContainer.Attributes;

namespace NanoContainer.Tests.TestModel
{
	[RegisterWithContainer]
	[ConstantParameter(0, "Hello World")]
	[ConstantParameter(1, 70)]
	[ConstantParameter(2, 99.9f)]
	public class DependentOnConstants
	{
		private string name;
		private int count;
		private float percentage;

		public DependentOnConstants(string name, int count, float percentage)
		{
			this.name = name;
			this.count = count;
			this.percentage = percentage;
		}

		public string Name
		{
			get { return name; }
		}

		public int Count
		{
			get { return count; }
		}

		public float Percentage
		{
			get { return percentage; }
		}
	}
}
