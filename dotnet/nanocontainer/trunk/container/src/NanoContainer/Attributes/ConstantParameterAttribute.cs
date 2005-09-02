namespace NanoContainer.Attributes
{
	public class ConstantParameterAttribute : PicoParameterAttribute
	{
		public ConstantParameterAttribute(int index, object value) : base(index, value)
		{
		}
	}
}
